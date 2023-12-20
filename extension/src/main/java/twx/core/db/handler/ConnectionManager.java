package twx.core.db.handler;

import java.sql.Connection;

import javax.sql.DataSource;

import com.thingworx.things.database.AbstractDatabase;

/*
 *  std interface to handle connections ... 
 */
public interface ConnectionManager {

    public String getCatalog();

    public DataSource getDataSource();

    public AbstractDatabase getAbstractDatabase();

    public Connection getConnection();

    public void close(Connection connection);

    public void commit(Connection connection);
      
    public void rollback(Connection connection);
}
