package twx.core.db.handler.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.thingworx.logging.LogUtilities;

import ch.qos.logback.classic.Logger;
import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db.handler.ConnectionCallback;
import twx.core.db.handler.DDLBuilder;
import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.model.DbModel;

public abstract class AbstractHandler implements DbHandler {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(TransactionManager.class);

    private ConnectionManager conncetionManager = null;
    private TransactionManager transactionManager = null;
    private DbInfo dbHandlerInfo = new DbInfo();
    private DbModel dbModel = null;

    public AbstractHandler(ConnectionManager connectionManager) {
        this.conncetionManager = connectionManager;
        this.transactionManager = new TransactionManager(this.conncetionManager);
        this.initialize();
    }

    protected void initialize() {
    }

    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    @Override
    public DbInfo getDbInfo() {
        return this.dbHandlerInfo;
    }

    @Override
    public String getDefaultCatalog() {
        return getConnectionManager().getCatalog();
    }

    @Override 
    public Boolean isDefaultCatalog(String catalogName) {
        return this.getDefaultCatalog().equals(catalogName);
    }

    @Override 
    public Boolean isDefaultSchema(String schemaName) {
        return this.getDefaultSchema().equals(schemaName);
    }

    // endregion
    // region Database Handler ...
    // --------------------------------------------------------------------------------
    public ConnectionManager getConnectionManager() {
        return this.conncetionManager;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public Connection getConnection() {
        return this.conncetionManager.getConnection();
    }

    public void close(Connection connection) {
        this.conncetionManager.close(connection);
    }

    public void commit(Connection connection) {
        this.conncetionManager.commit(connection);
    }

    public void rollback(Connection connection) {
        this.conncetionManager.rollback(connection);
    }

    // endregion
    // region DDL Handler ...
    // --------------------------------------------------------------------------------
    public DbModel getDbModel() {
        return this.dbModel;
    }

    public void setDbModel(DbModel dbModel) {
        this.dbModel = dbModel;
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------


    // endregion
    // region Exception & Logging Handler ...
    // --------------------------------------------------------------------------------
    public  void logException(String message, Exception exception ) {
        _logger.error( message, exception );
    }

    public  void logSQLException(String message, SQLException exception ) {
        _logger.error( message, exception );
        _logger.error( printSQLException(exception) );
    }

    protected static String printSQLException(SQLException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(pw);
                pw.println("SQLState: " + ((SQLException) e).getSQLState());
                pw.println("Error Code: " + ((SQLException) e).getErrorCode());
                pw.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    pw.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
        return sw.toString();
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public <T> T execute(ConnectionCallback<T> callback) throws SQLException {
        Connection connection = null;
        try {
            connection = this.conncetionManager.getConnection();
            return callback.execute(connection);
        } catch (SQLException ex) {
            _logger.error("Exception on callback" + ex.getMessage() );
            throw ex;
        } finally {
           this.conncetionManager.close(connection);
        }
    }

    public <T> T executeTransaction(ConnectionCallback<T> callback) throws SQLException {
        Connection connection = null;
        try {
            connection = this.conncetionManager.getConnection();
            T result = callback.execute(connection);
            this.conncetionManager.commit(connection);
            return result;
        } catch (SQLException ex) {
            this.conncetionManager.rollback(connection);
            _logger.error("Exception on callback" + ex.getMessage() );
            throw ex;
        } finally {
           this.conncetionManager.close(connection);
        }
    }
    // endregion

}