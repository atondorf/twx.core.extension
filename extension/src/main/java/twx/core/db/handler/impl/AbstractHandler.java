package twx.core.db.handler.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.InfoTablePrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import ch.qos.logback.classic.Logger;
import twx.core.db.handler.ConnectionCallback;
import twx.core.db.handler.ConnectionManager;
import twx.core.db.handler.ModelManager;
import twx.core.db.handler.PreparedStatementHandler;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.ModelManager;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.handler.TransactionManager;
import twx.core.db.model.DbModel;
import twx.core.db.util.InfoTableUtil;

public abstract class AbstractHandler implements DbHandler {
    private static Logger _logger = LogUtilities.getInstance().getDatabaseLogger(TransactionManager.class);

    private ConnectionManager conncetionManager = null;
    private TransactionManager transactionManager = null;
    private DbInfo dbHandlerInfo = new DbInfo();
    private ModelManager dbModelManager = null;

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

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    public int executeUpdate(String sql) throws Exception {
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var stm = con.createStatement(); ) {
                return stm.executeUpdate(sql);
            }
        });
    }

    public InfoTable executeQuery(String sql) throws Exception {
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var stm = con.createStatement(); ) {
                var rs = stm.executeQuery(sql);
                // directly return the resultset as inforable 
                return InfoTableUtil.createInfoTableFromResultset(rs);
            }
        });
    }

    public InfoTable executeUpdateBatch(InfoTable sqlTable) throws Exception {
        // create infotable of result.INTEGER ... 
        var resultTable = createUpdateResult();
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var stm = con.createStatement(); ) {
                for (ValueCollection val : sqlTable.getRows()) {
                    String sql = val.getStringValue("sql");
                    stm.addBatch(sql);
                }
                // get resultset of batch and add them to result table
                int[] result = stm.executeBatch();
                addUpdateResult(resultTable, result);
                return resultTable;
            }
        });
    }

    public InfoTable executeQueryBatch(InfoTable sqlTable) throws Exception {
        // create infotable of result.INFOTABLE ...         
        var resultTable = createQueryResult();
        // Execute SQL Callable        
        return this.execute((Connection con) -> {
            try ( var stm = con.createStatement(); ) {
                // no real batch for query, so in loop ... 
                for (ValueCollection val : sqlTable.getRows()) {
                    String sql = val.getStringValue("sql");
                    // get resultset of query and add them to result table
                    var rs = stm.executeQuery(sql);
                    addQueryResult(resultTable, rs );
                }
                return resultTable;
            }
        });
    }

    public InfoTable executeUpdatePrepared(String sql, InfoTable values) throws Exception {
        // create infotable of result.INTEGER ...          
        InfoTable resultTable = createUpdateResult(); 
        var dsDef = values.getDataShape();
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var prepStmt = new PreparedStatementHandler( con, sql, dsDef );  ) {
                for (ValueCollection val : values.getRows()) {
                    prepStmt.set(val);
                    prepStmt.addBatch();
                }
                // get resultset of batch and add them to result table
                int[] rs = prepStmt.executeBatch();
                addUpdateResult(resultTable, rs);
            }
            return resultTable;
        });
    }

    public InfoTable executeQueryPrepared(String sql, InfoTable values) throws Exception {
        // create infotable of result.INTEGER ... 
        var resultTable = createQueryResult();
        var dsDef = values.getDataShape();
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var prepStmt = new PreparedStatementHandler( con, sql, dsDef );  ) {
                for (ValueCollection val : values.getRows()) {
                    prepStmt.set(val);
                    // assign the result to the table 
                    var rs = prepStmt.executeQuery();
                    // get resultset of query and add it to result table
                    addQueryResult(resultTable, rs);
                }
            }
            return resultTable;
        });
    }
    
    public InfoTable executeQueryPrepared(String sql, InfoTable values, Integer rowIdx) throws Exception {
        // definition of Datashape needed .
        var dsDef = values.getDataShape();
        // Execute SQL Callable
        return this.execute((Connection con) -> {
            try ( var prepStmt = new PreparedStatementHandler( con, sql, dsDef );  ) {
                ValueCollection val = values.getRow(rowIdx);
                prepStmt.set(val);
                // assign the result to the table 
                var rs = prepStmt.executeQuery();
                // only one resultset, return it directly ... 
                return InfoTableUtil.createInfoTableFromResultset(rs);
            }
        });
    }

    public <T> T execute(ConnectionCallback<T> callback) throws Exception {
        Connection connection = null;
        try {
            connection = this.conncetionManager.getConnection();
            T result = callback.execute(connection);
            this.conncetionManager.commit(connection);
            return result;
        } catch (SQLException ex) {
            this.conncetionManager.rollback(connection);
            this.logSQLException("SQL-Exception on callback - Rollback", ex);
            throw ex;
        } catch (Exception ex) {
            this.conncetionManager.rollback(connection);
            this.logException("Exception on callback - Rollback", ex);
            throw ex;
        } finally {
            this.conncetionManager.close(connection);
        }
    }

    // endregion
    // region Result helpers ...
    // --------------------------------------------------------------------------------
    
    protected static InfoTable createUpdateResult() {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("result", BaseTypes.INTEGER));
        return table;
    }

    protected static InfoTable addIntResult(InfoTable table, Integer value ) throws Exception {
        var col = new ValueCollection();
        col.SetIntegerValue("result", value );
        table.addRow(col);
        return table;
    }   

    protected static InfoTable addUpdateResult(InfoTable table, int[] values ) throws Exception {
        for (int i = 0; i < values.length; i++) {
            var col = new ValueCollection();
            col.SetIntegerValue("result", values[i] );
            table.addRow(col);
        }
        return table;
    } 

    protected static InfoTable createQueryResult() {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("result", BaseTypes.INFOTABLE));
        return table;
    }

    protected static InfoTable addTableResult(InfoTable table, InfoTable row) throws Exception {
        var col = new ValueCollection();
        col.SetInfoTableValue("result", row );
        table.addRow(col);
        return table;
    }

    protected static InfoTable addQueryResult(InfoTable table, ResultSet rs) throws Exception {
        var row = InfoTableUtil.createInfoTableFromResultset(rs);
        var col = new ValueCollection();
        col.SetInfoTableValue("result", row );
        table.addRow(col);
        return table;
    }

    // endregion
    // region Exception & Logging Handler ...
    // --------------------------------------------------------------------------------
    public void logException(String message, Exception exception) {
        _logger.error(message, exception);
    }

    public void logSQLException(String message, SQLException exception) {
        _logger.error(message, exception);
        _logger.error(printSQLException(exception));
    }

    protected static String printSQLException(SQLException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
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
