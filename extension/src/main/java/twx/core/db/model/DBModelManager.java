package twx.core.db.model;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONObject;
import com.thingworx.types.BaseTypes;
import twx.core.db.IDatabaseHandler;

public class DBModelManager {

    // region Handling of cached Model ...
    // --------------------------------------------------------------------------------
    private static final ConcurrentMap<String, DbModel> modelMap = new ConcurrentHashMap<String, DbModel>();

    public static List<String> getModelNames() {
        return new ArrayList<>(modelMap.keySet());
    }

    public static DbModel getModel(String modelName) {
        DbModel model = modelMap.get(modelName);
        if (model == null) {
            model = modelMap.computeIfAbsent(modelName, k -> new DbModel(modelName));
        }
        return model;
    }

    // endregion
    // region TWX-Services Metadata Configuration ...
    // --------------------------------------------------------------------------------
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
            Integer typeId = rs.getInt("DATA_TYPE");
            // @Todo-AT: own type mapping ...
            JDBCType jdbcType = JDBCType.valueOf(typeId);
            BaseTypes twxType = BaseTypes.JDBCTypeToBaseType(typeId);

            col.setTwxType(twxType);
            col.setSqlType(jdbcType);
            col.setSqlSize(rs.getInt("COLUMN_SIZE"));
            col.setNullable(((String) "YES").equals(rs.getString("IS_NULLABLE")));
            col.setAutoIncrement(((String) "YES").equals(rs.getString("IS_AUTOINCREMENT")));

            rs.getString("REMARKS"); // comments ...
            rs.getString("COLUMN_DEF"); // default value ...
        }
        return dbTable;
    }

    protected static DbTable queryModelKeys(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getPrimaryKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            var primKey = dbTable.getPrimaryKey();
            String name = rs.getString("PK_NAME");
            if (name != null) {
                primKey.setName(name);
                String colName = rs.getString("COLUMN_NAME");
                Integer colOrdinal = rs.getInt("KEY_SEQ");
                primKey.addColumn(colName, colOrdinal);
            }
        }
        return dbTable;
    }

    protected static DbTable queryModelIndexes(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getIndexInfo(null, dbTable.getSchemaName(), dbTable.getName(), false, false);
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            if (name != null) {
                DbIndex index = dbTable.getOrAddIndex(name);
                String colName = rs.getString("COLUMN_NAME");
                Integer colOrdinal = rs.getInt("ORDINAL_POSITION");
                index.addColumn(colName, colOrdinal);
                index.setUnique(!rs.getBoolean("NON_UNIQUE"));
            }
        }
        return dbTable;
    }

    protected static DbTable queryModelForeignKeys(DbTable dbTable, IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getImportedKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            DbForeignKey fk = dbTable.getOrAddForeignKey(rs.getString("FK_NAME"));
            fk.setToSchema(rs.getString("FKTABLE_SCHEM"));
            fk.setToTable(rs.getString("FKTABLE_NAME"));
            fk.setOnDelete(rs.getInt("UPDATE_RULE"));
            fk.setOnUpdate(rs.getInt("DELETE_RULE"));

            fk.addColumn(rs.getInt("KEY_SEQ"), rs.getString("FKCOLUMN_NAME"), rs.getString("PKCOLUMN_NAME"));
        }
        return dbTable;
    }
    // endregion
}