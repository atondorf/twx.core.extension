package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.valuestreams.QueryContext;
import com.thingworx.webservices.context.ThreadLocalContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import org.apache.commons.lang3.tuple.Pair;

public class TransactionManager {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(TransactionManager.class);
  
  public static final String GENERIC_EXCEPTION_MSG = "An error has occurred. Contact your administrator for further details.";
  
  private DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
  
  private static Map<Object, Transaction> transactionMap = Collections.synchronizedMap(new WeakHashMap<>());
  
  private TransactionLookUp transactionLookUp;
  
  TransactionManager() {
    this.transactionLookUp = new TransactionLookUp(this);
  }
  
  public DatabaseConnectionManager getDatabaseConnectionManager() {
    return this.databaseConnectionManager;
  }
  
  public void startTransaction() throws Exception {
    Object key = getTransactionKey();
    startTransaction(key);
  }
  

  private Transaction startTransaction( Object key) throws Exception {
    Optional<Transaction> found = getTransaction(key);
    if (found.isPresent()) {
      Transaction transaction1 = found.get();
      transaction1.incrementDepth();
      if (_logger.isDebugEnabled())
        _logger.debug("Transaction already started depth:" + transaction1.getDepth()); 
      return transaction1;
    } 
    Transaction transaction = new Transaction(this.databaseConnectionManager.getConnection());
    transactionMap.put(key, transaction);
    return transaction;
  }
  
  private Optional<Transaction> getTransaction(Object key) {
    if (transactionMap.containsKey(key))
      return Optional.of(transactionMap.get(key)); 
    return Optional.empty();
  }
  
  public void commitTransaction() {
    Object key = getTransactionKey();
    Optional<Transaction> foundTransaction = getTransaction(key);
    if (foundTransaction.isPresent()) {
      Transaction transaction = foundTransaction.get();
      commitTransaction(transaction);
    } 
  }
  
  private void commitTransaction(Transaction transaction) {
    try {
      if (transaction != null)
        transaction.commit(this.databaseConnectionManager); 
    } catch (Exception ex) {
      _logger.warn("Error commiting transaction:" + transaction);
      throw new ThingworxRuntimeException("Error commiting transaction:" + transaction, ex);
    } 
  }
  
  public void rollbackTransaction() {
    Object key = getTransactionKey();
    Optional<Transaction> foundTransaction = getTransaction(key);
    if (foundTransaction.isPresent()) {
      Transaction transaction = foundTransaction.get();
      rollbackTransaction(transaction);
    } 
  }
  
  private void rollbackTransaction(Transaction transaction) {
    try {
      if (transaction != null)
        transaction.rollback(this.databaseConnectionManager); 
    } catch (Exception ex) {
      _logger.warn("Error rolling back transaction:" + transaction);
      throw new ThingworxRuntimeException("Error rolling back transaction:" + transaction, ex);
    } 
  }
  
  public void closeTransaction() {
    Object key = getTransactionKey();
    Optional<Transaction> foundTransaction = getTransaction(key);
    if (foundTransaction.isPresent()) {
      Transaction transaction = foundTransaction.get();
      closeTransaction(key, transaction);
    } 
  }
  
  private void closeTransaction(Object key, Transaction transaction) {
    try {
      if (transaction != null)
        transaction.close(this.databaseConnectionManager); 
    } catch (Exception ex) {
      _logger.warn("Error closing transaction:" + transaction);
      throw new ThingworxRuntimeException("Error closing transaction:" + transaction, ex);
    } finally {
      if (transaction != null && transaction.isClosed())
        transactionMap.remove(key); 
      if (transaction != null && transaction.isCommitted() && 
        !transaction.getDataChanges().isEmpty()) {
        DataChangeDispatcher dataChangeDispatcher = new DataChangeDispatcher();
        dataChangeDispatcher.postDispatchDataChange(transaction.getDataChanges());
        transaction.clearDataChanges();
      } 
    } 
  }
  
  protected void stopAllTransactions() {
    for (Map.Entry<Object, Transaction> entry : transactionMap.entrySet()) {
      Transaction transaction = entry.getValue();
      try {
        transaction.rollback(this.databaseConnectionManager);
      } catch (Exception ex) {
        _logger.warn("Error rollbacking transaction:" + transaction);
      } finally {
        closeTransaction(entry.getKey(), transaction);
      } 
    } 
    transactionMap.clear();
  }
  
  public QueryResult execute(DatabaseTableHandler databaseTableHandler) {
    Transaction transaction = null;
    Object key = getTransactionKey();
    try {
      transaction = startTransaction(key);
      Connection connection = transaction.getConnection();
      Optional<QueryResult> found = databaseTableHandler.execute(connection);
      if (found.isPresent() && !((QueryResult)found.get()).getDataChanges().isEmpty()) {
        (new DataChangeDispatcher()).onDispatchDataChange(((QueryResult)found.get()).getDataChanges());
        transaction.appendDataChanges(((QueryResult)found.get()).getDataChanges());
      } 
      commitTransaction(transaction);
      return found.orElseGet(() -> new QueryResult(0, true));
    } catch (Exception e) {
      rollbackTransaction(transaction);
      throw getTransactionException(e);
    } finally {
      closeTransaction(key, transaction);
    } 
  }
  
  private ThingworxRuntimeException getTransactionException(Exception e) {
    _logger.error(e.getMessage());
    _logger.debug(e.getMessage(), e);
    if (e instanceof SQLException)
      return new ThingworxRuntimeException(getExceptionMessage(e)); 
    return new ThingworxRuntimeException(e.getLocalizedMessage(), e);
  }
  
  private String getExceptionMessage(Exception e) {
    if (e instanceof SQLException && ((SQLException)e).getSQLState().startsWith("23"))
      return "An error has occurred. Contact your administrator for further details."; 
    return "An error has occurred. Contact your administrator for further details.";
  }
  
  public <T> T executeHandler(ExecuteHandler<T> executeHandler) {
    Transaction transaction = null;
    Object key = getTransactionKey();
    try {
      transaction = startTransaction(key);
      T result = executeHandler.execute();
      commitTransaction(transaction);
      return result;
    } catch (Exception e) {
      rollbackTransaction(transaction);
      throw getTransactionException(e);
    } finally {
      closeTransaction(key, transaction);
    } 
  }
  
  public List<Pair<DatabaseTableHandler, QueryResult>> executeList(List<DatabaseTableHandler> databaseTableHandlerList) {
    List<Pair<DatabaseTableHandler, QueryResult>> queryResults = Lists.newArrayList();
    for (DatabaseTableHandler databaseTableHandler : databaseTableHandlerList) {
      try {
        QueryResult result = execute(databaseTableHandler);
        queryResults.add(Pair.of(databaseTableHandler, result));
      } catch (Exception ex) {
        queryResults.add(Pair.of(databaseTableHandler, new QueryResult(0, false)));
      } 
    } 
    return queryResults;
  }
  
  void startTransactionLookUp() {
    if (this.transactionLookUp == null)
      this.transactionLookUp = new TransactionLookUp(this); 
    this.transactionLookUp.start();
  }
  
  void stopTransactionLookUp() {
    if (this.transactionLookUp != null)
      this.transactionLookUp.stop(); 
  }
  
  public Map<Object, Transaction> getTransactionMap() {
    return Maps.newHashMap(transactionMap);
  }
  

  private Object getTransactionKey() {
    QueryContext queryContext = ThreadLocalContext.getQueryContextObj();
    if (queryContext != null)
      return queryContext; 
    throw new ThingworxRuntimeException("Transaction key can't be null.");
  }
}
