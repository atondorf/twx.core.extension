package twx.core.db.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import twx.core.concurrency.imp.AtomicManager;
import twx.core.db.IDatabaseHandler;

public class DBModelManager {

    // the singleton instance ...
    private static final DBModelManager SINGLETON = new DBModelManager();

    private static final ConcurrentMap<String, DbModel> modelMap = new ConcurrentHashMap<String, DbModel>();

    public static DbModel getDBModel(String application) {
        DbModel model = modelMap.get(application);
        if (model == null) {
            model = modelMap.computeIfAbsent(application, k -> new DbModel(application));
        }
        return model;
    }

    public static DbModel queryModel(IDatabaseHandler handler) throws SQLException {
        String catalog = handler.getCatalog();
        String application = handler.getApplication();
        DbModel dbModel = new DbModel(catalog, application);
        return queryModelSchemas(dbModel, handler);
    }

    protected static DbModel queryModelSchemas(DbModel dbModel, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
            if (!handler.isSystemSchema(schemaName)) {
                DbSchema dbSchema = dbModel.addSchema(schemaName);
                queryModelTables(dbSchema, handler);
            }
        }
        return dbModel;
    }

    protected static DbSchema queryModelTables(DbSchema dbSchema, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getTables(null, dbSchema.getName(), null, null);
        while (rs.next()) {
            String tableSchema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");
            if (tableSchema.equals(dbSchema.getName())) {
                DbTable dbTable = dbSchema.addTable(tableName);
                queryModelColumns(dbTable, handler);
                queryModelKeys(dbTable, handler);
                queryModelIndexes(dbTable, handler);
                queryModelForeignKeys(dbTable, handler);
            }   
        }
        return dbSchema;
    }

    protected static DbTable queryModelColumns(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getColumns(null, dbTable.getSchemaName(), dbTable.getName(), null);
        while (rs.next()) {
            DbColumn col = dbTable.addColumn(rs.getString("COLUMN_NAME"));
            String typeName = rs.getString("TYPE_NAME");
            if (typeName.contains("identity")) {
                typeName = typeName.split(" ")[0];
            }
            col.setTypeName(typeName);
            col.setTypeId(rs.getInt("DATA_TYPE"));
            col.setSize(rs.getInt("COLUMN_SIZE"));
            col.setNullable(((String) "YES").equals(rs.getString("IS_NULLABLE")));
            col.setAutoIncrement(((String) "YES").equals(rs.getString("IS_AUTOINCREMENT")));
        }
        return dbTable;
    }

    protected static DbTable queryModelKeys(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getPrimaryKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            String colName = rs.getString("COLUMN_NAME");
            DbColumn column = dbTable.getColumn(colName);
            column.setPrimaryKeySeq(rs.getInt("KEY_SEQ"));
        }
        return dbTable;
    }

    protected static DbTable queryModelIndexes(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getIndexInfo(null, dbTable.getSchemaName(),dbTable.getName(), false, false);
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            if (name != null) {
                DbIndex index = dbTable.getOrAddIndex(name);
                index.setColumn(rs.getString("COLUMN_NAME"));
                index.setType(rs.getInt("TYPE"));
                index.setUnique(!rs.getBoolean("NON_UNIQUE"));
            }
        }
        return dbTable;
    }

    protected static DbTable queryModelForeignKeys(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getImportedKeys(null, dbTable.getSchemaName(),dbTable.getName());
        while (rs.next()) {
            DbForeignKey fk = dbTable.addForeignKey(rs.getString("FK_NAME"));
            fk.setColumn(rs.getString("PKCOLUMN_NAME"));
            fk.setToSchema(rs.getString("FKTABLE_SCHEM"));
            fk.setToTable(rs.getString("FKTABLE_NAME"));
            fk.setToColumn(rs.getString("FKCOLUMN_NAME"));
            fk.setOnDelete(rs.getInt("UPDATE_RULE"));
            fk.setOnUpdate(rs.getInt("DELETE_RULE"));
        }
        return dbTable;
    }

}