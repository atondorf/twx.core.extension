package twx.core.db.handler.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.thingworx.logging.LogUtilities;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.IntegerPrimitive;

import ch.qos.logback.classic.Logger;
import twx.core.db.handler.ConnectionCallback;
import twx.core.db.handler.ConnectionManager;
import twx.core.db.handler.ModelManager;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.ModelManager;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.handler.TransactionManager;
import twx.core.db.model.DbModel;
import twx.core.db.util.InfoTableUtil;

public abstract class AbstractHandler implements DbHandler {
    private static Logger _logger = LogUtilities.getInstance().getDatabaseLogger(TransactionManager.class);

    private ConnectionManager   conncetionManager = null;
    private TransactionManager  transactionManager = null;
    private DbInfo              dbHandlerInfo = new DbInfo();
    private ModelManager        dbModelManager = null;

    public AbstractHandler(ConnectionManager connectionManager) {
        this.conncetionManager = connectionManager;
        this.transactionManager = new TransactionManager(this.conncetionManager);
        this.dbModelManager = new AbstractModelManager(this);
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
    // region Model Management ...
    // --------------------------------------------------------------------------------
    public ModelManager getModelManager() {
        return this.dbModelManager;
    }

    public DbModel getDbModel() {
        return this.getModelManager().getModel();
    }

    public DbModel queryDbModel() throws SQLException {
        return this.getModelManager().queryModel();    
    }

    public DbModel updateDbModel() throws SQLException {
        return this.getModelManager().updateModel(null);    
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public int executeUpdate(String sql) throws SQLException {
        return this.execute((Connection con ) -> { 
            try( var stm = con.createStatement() ) {
                return stm.executeUpdate(sql);
            }
            catch(SQLException ex ) {
                throw ex;
            }
        });
    }

    public InfoTable executeBatch(InfoTable sqlTable) throws SQLException {
        return this.execute((Connection con ) -> { 
            try( var stm = con.createStatement() ) {
                for( ValueCollection val : sqlTable.getRows() ) {
                    String sql = val.getStringValue("sql");
                    stm.addBatch(sql);
                }
                int[] result = stm.executeBatch();
                for( int i = 0; i < result.length; i++ ) {
                    var row = sqlTable.getRow(i);
                    row.SetIntegerValue("result", new IntegerPrimitive(result[i]) );
                }
                return sqlTable;
            }
            catch(SQLException ex) {
                throw ex;
            }
            catch(Exception ex) {
               throw new SQLException(ex);
            }
        });
    }

    public InfoTable executeQuery(String sql) throws SQLException {
        return this.execute((Connection con ) -> { 
            try( var stm = con.createStatement() ) {
                var rs = stm.executeQuery(sql);
                var table = InfoTableUtil.createInfoTableFromResultset(rs);
                rs.close();
                return table;
            }
            catch(SQLException ex) {
                throw ex;
            }
        });
    }

    public InfoTable executePreparedUpdate(String sql, InfoTable values) throws SQLException {
        return null;
    }

    public InfoTable executePreparedQuery(String sql, InfoTable values) throws SQLException {
        return null;
    }

    public <T> T execute(ConnectionCallback<T> callback) throws SQLException {
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
}
