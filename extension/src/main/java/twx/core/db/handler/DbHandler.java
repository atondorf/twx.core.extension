package twx.core.db.handler;

import java.sql.Connection;
import java.sql.SQLException;

import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db.handler.DbInfo;
import twx.core.db.model.DbModel;

public interface DbHandler {

    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    public String getName();

    public String getKey();

    public String getDefaultCatalog();
    // get's the default Schema of the connection ... 
    public String getDefaultSchema();
    // endregion

    // region Connections & Transactions ...
    // --------------------------------------------------------------------------------
    public ConnectionManager getConnectionManager();

    public TransactionManager getTransactionManager();

    public Connection getConnection();

    public void close(Connection connection);

    public void commit(Connection connection);

    public void rollback(Connection connection);

    // endregion

    // region DDL Handler ...
    // --------------------------------------------------------------------------------
    public DbInfo getDbInfo();

    public DDLBuilder getDDLBuilder();

    public DDLReader getDDLReader();

    public DbModel getDbModel();

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public SQLBuilder getSqlBuilder();

    // endregion
    // region Generic Handler ...
    // --------------------------------------------------------------------------------
    public <T> T execute(ConnectionCallback<T> callback) throws SQLException;

    public <T> T executeTransaction(ConnectionCallback<T> callback) throws SQLException;

    // endregion

}
