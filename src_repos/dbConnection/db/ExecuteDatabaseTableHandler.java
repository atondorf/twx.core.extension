package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import java.sql.Connection;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

class ExecuteDatabaseTableHandler implements ExecuteHandler<QueryResult> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteDatabaseTableHandler.class);
  

  private DatabaseTableHandler databaseTableHandler;
  

  private Transaction transaction;
  
  ExecuteDatabaseTableHandler( DatabaseTableHandler databaseTableHandler,  Transaction transaction) {
    Validate.notNull(databaseTableHandler);
    this.databaseTableHandler = databaseTableHandler;
    Validate.notNull(transaction);
    this.transaction = transaction;
  }
  
  public QueryResult execute() throws Exception {
    if (_logger.isDebugEnabled())
      _logger.debug("Execute database table handler:" + this.databaseTableHandler); 
    Connection connection = this.transaction.getConnection();
    Optional<QueryResult> found = this.databaseTableHandler.execute(connection);
    if (found.isPresent())
      this.transaction.appendDataChanges(((QueryResult)found.get()).getDataChanges()); 
    return found.orElseGet(() -> new QueryResult(0, true));
  }
}
