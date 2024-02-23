package twx.core.db.handler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.thingworx.types.InfoTable;

import twx.core.db.handler.DbInfo;
import twx.core.db.model.DbModel;

public interface DbHandler {

    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    public DbInfo getDbInfo();
    
    public String getName();

    public String getKey();

    public String getDefaultCatalog();

    public Boolean isDefaultCatalog(String catalogName);
    // get's the default Schema of the connection ... 
    public String getDefaultSchema();

    public Boolean isDefaultSchema(String schemaName);
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

    // region Model Handling ...
    // --------------------------------------------------------------------------------
    public ModelManager getModelManager();

    public DbModel getDbModel();

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public SQLBuilder getSqlBuilder();

    // endregion
    // region Transactions Handlers ...
    // --------------------------------------------------------------------------------
    public <T> T execute(ConnectionCallback<T> callback) throws Exception;

    public int executeUpdate(String sql) throws Exception;

    public InfoTable executeQuery(String sql) throws Exception;

    public InfoTable executeUpdateBatch(InfoTable sqlTable) throws Exception;

    public InfoTable executeQueryBatch(InfoTable sqlTable) throws Exception;

    public InfoTable executeUpdatePrepared(String sql, InfoTable values) throws Exception;

    public InfoTable executeQueryPrepared(String sql, InfoTable values) throws Exception;

    public InfoTable executeQueryPrepared(String sql, InfoTable values, Integer rowIdx) throws Exception;
    // endregion 
    // region Exception & Logging Handler ...
    // --------------------------------------------------------------------------------
    public void logException(String message, Exception exception );

    public void logSQLException(String message, SQLException exception );
    // endregion

}
