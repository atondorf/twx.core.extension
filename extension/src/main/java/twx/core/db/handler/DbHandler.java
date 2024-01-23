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

    public DbModel queryDbModel() throws SQLException;

    public DbModel updateDbModel() throws SQLException;

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public SQLBuilder getSqlBuilder();

    // endregion
    // region Transactions Handlers ...
    // --------------------------------------------------------------------------------
    public <T> T execute(ConnectionCallback<T> callback) throws SQLException;

    public int executeUpdate(String sql) throws SQLException;

    public InfoTable executeBatch(InfoTable sqlTable) throws SQLException;

    public InfoTable executeQuery(String sql) throws SQLException;

    public InfoTable executePreparedUpdate(String sql, InfoTable values) throws SQLException;

    public InfoTable executePreparedQuery(String sql, InfoTable values) throws SQLException;
    // endregion 
    // region Exception & Logging Handler ...
    // --------------------------------------------------------------------------------
    public void logException(String message, Exception exception );

    public void logSQLException(String message, SQLException exception );
    // endregion

}
