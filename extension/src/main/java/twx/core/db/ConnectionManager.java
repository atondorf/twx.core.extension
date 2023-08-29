package twx.core.db;

import java.sql.Connection;

/*
 *  std interface to handle connections ... 
 */
public interface ConnectionManager {

    public Connection getConnection() throws Exception;
      
    public void close(Connection connection);
      
    public void commit(Connection connection);
      
    public void rollback(Connection connection);

}
