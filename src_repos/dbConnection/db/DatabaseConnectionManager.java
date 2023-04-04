package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseConnectionManager.class);
  
  protected Connection getConnection() throws Exception {
    try {
      if (_logger.isDebugEnabled())
        _logger.debug("Try to get database Connection."); 
      Connection connection = DatabaseUtility.getConnection();
      connection.setAutoCommit(false);
      if (_logger.isDebugEnabled())
        _logger.debug("Connection to database acquire."); 
      return connection;
    } catch (SQLException e) {
      _logger.error("Error getting database connection", e);
      throw new ThingworxRuntimeException(e);
    } 
  }
  
  protected void close(Connection connection) {
    if (connection == null)
      return; 
    try {
      if (connection.isClosed())
        return; 
      connection.setAutoCommit(true);
      connection.close();
    } catch (SQLException e) {
      _logger.error("Error closing connection", e);
    } 
  }
  
  void commit(Connection connection) {
    if (connection == null)
      return; 
    try {
      connection.commit();
    } catch (SQLException e) {
      _logger.error("Error in commit", e);
      rollback(connection);
    } 
  }
  
  void rollback(Connection connection) {
    if (connection == null)
      return; 
    try {
      connection.rollback();
    } catch (SQLException e) {
      _logger.error("Error in rollback", e);
    } 
  }
}
