package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class Transaction {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(Transaction.class);
  

  private Connection connection;
  
  private int depth = 0;
  
  private boolean committed = false;
  
  private boolean closed = false;
  
  private List<DataChange> dataChanges = Lists.newArrayList();
  
  Transaction( Connection connection) {
    this.connection = connection;
  }
  

  public Connection getConnection() {
    return this.connection;
  }
  
  public List<DataChange> getDataChanges() {
    return this.dataChanges;
  }
  
  public boolean isCommitted() {
    return this.committed;
  }
  
  public boolean isClosed() {
    return this.closed;
  }
  
  protected void appendDataChanges(List<DataChange> dataChanges) {
    this.dataChanges.addAll(dataChanges);
  }
  
  public void clearDataChanges() {
    this.dataChanges.clear();
  }
  
  public void commit(DatabaseConnectionManager databaseConnectionManager) {
    if (this.depth == 0) {
      databaseConnectionManager.commit(this.connection);
      this.committed = true;
    } 
  }
  
  public void rollback(DatabaseConnectionManager databaseConnectionManager) {
    if (this.depth == 0) {
      databaseConnectionManager.rollback(this.connection);
      this.committed = false;
      clearDataChanges();
    } 
  }
  
  public void close(DatabaseConnectionManager databaseConnectionManager) {
    if (this.depth == 0) {
      databaseConnectionManager.close(this.connection);
      this.closed = true;
    } else {
      decreaseDepth();
    } 
  }
  
  public int getDepth() {
    return this.depth;
  }
  
  protected void setDepth(int depth) {
    this.depth = depth;
  }
  
  public void incrementDepth() {
    this.depth++;
  }
  
  private void decreaseDepth() {
    if (this.depth > 0) {
      this.depth--;
    } else {
      _logger.warn("Can't decrease depth it is already 0!");
    } 
  }
  
  protected void finalize() {
    try {
      if (!this.connection.isClosed()) {
        _logger.warn("Rollback and close Transaction");
        this.connection.rollback();
        this.connection.close();
      } 
    } catch (SQLException e) {
      throw new ThingworxRuntimeException(e);
    } 
  }
}
