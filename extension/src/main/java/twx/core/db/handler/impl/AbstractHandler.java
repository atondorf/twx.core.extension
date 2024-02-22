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
        return this.execute((Connection con) -> {
            var stm = con.createStatement();
            return stm.executeUpdate(sql);
        });
    }

    public InfoTable executeQuery(String sql) throws Exception {
        return this.execute((Connection con) -> {
            var stm = con.createStatement();
            var rs = stm.executeQuery(sql);
            var table = InfoTableUtil.createInfoTableFromResultset(rs);
            rs.close();
            return table;
        });
    }

    public InfoTable executeUpdateBatch(InfoTable sqlTable) throws Exception {
        // create column for result ... 
        var table = sqlTable.clone();
        table.addField(new FieldDefinition("result", BaseTypes.INTEGER));

        return this.execute((Connection con) -> {
            var stm = con.createStatement();
            for (ValueCollection val : table.getRows()) {
                String sql = val.getStringValue("sql");
                stm.addBatch(sql);
            }
            // real batch, so two step processing ... 
            int[] result = stm.executeBatch();
            for (int i = 0; i < result.length; i++) {
                var row = table.getRow(i);
                row.SetIntegerValue("result", new IntegerPrimitive(result[i]));
            }
            return table;
        });
    }

    public InfoTable executeQueryBatch(InfoTable sqlTable) throws Exception {
        // create column for result ... 
        var table = sqlTable.clone();
        table.addField( new FieldDefinition("result", BaseTypes.INFOTABLE) );
        
        return this.execute((Connection con) -> {
            var stm = con.createStatement();
            // no real batch for query, so in loop ... 
            for (ValueCollection val : table.getRows()) {
                String sql = val.getStringValue("sql");
                // assign the result to the table 
                var rs = stm.executeQuery(sql);
                val.SetInfoTableValue("result", InfoTableUtil.createInfoTableFromResultset(rs) );
            }
            return table;
        });
    }

    public InfoTable executeUpdatePrepared(String sql, InfoTable values) throws Exception {
        InfoTable resTable = new InfoTable();
        resTable.addField(new FieldDefinition("sql", BaseTypes.STRING));
        resTable.addField(new FieldDefinition("result", BaseTypes.INTEGER));

        return resTable;
    }

    public InfoTable executeQueryPrepared(String sql, InfoTable values) throws Exception {

        var dsdef = values.getDataShape();

        InfoTable resTable = new InfoTable();
        resTable.addField(new FieldDefinition("sql", BaseTypes.STRING));
        resTable.addField(new FieldDefinition("result", BaseTypes.INFOTABLE));

        return resTable;
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
