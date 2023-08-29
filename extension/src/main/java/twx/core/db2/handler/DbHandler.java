package twx.core.db2.handler;

import java.sql.Connection;

import javax.sql.DataSource;

import com.thingworx.things.database.AbstractDatabase;

import twx.core.db2.model.DbModel;

public interface DbHandler {
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    public String getName();

    public String getKey();

    public String getCatalog();

    public String getApplication();

    public DbHandlerInfo getHandlerInfo();

    public JdbcSqlBuilder getSqlBuilder();
    // endregion
    // region Access to Models ... 
    // --------------------------------------------------------------------------------
    public JdbcModelManager getModelManager();

    public DbModel getModel();

    public DbModel queryModel() throws Exception;

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    public AbstractDatabase getAbstractDatabase();

    public DataSource getDataSource() throws Exception;

    public Connection getConnection() throws Exception;

    public boolean isConnected() throws Exception;

    public void beginTransaction() throws Exception;

    public void endTransaction(Connection conn) throws Exception;

    public void commit(Connection conn) throws Exception;

    public void rollback(Connection conn) throws Exception;
    // endregion
}
