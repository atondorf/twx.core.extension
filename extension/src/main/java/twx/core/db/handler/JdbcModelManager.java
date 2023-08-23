package twx.core.db.handler;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;

import twx.core.db.model.DbColumn;
import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbIndex;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbSchema;
import twx.core.db.model.DbTable;
import twx.core.db.util.TwxDataShapeUtil;

public class JdbcModelManager {

    final static Logger logger = LoggerFactory.getLogger(JdbcModelManager.class);

    private DbHandler handler = null;
    private Connection connection = null;

    public JdbcModelManager(DbHandler handler) {
        this.handler = handler;
    }

    public DbHandler getDbHandler() {
        return handler;
    }

    // endrgion
    // region Model From DatatShape ...
    // --------------------------------------------------------------------------------

    public DbTable getTableFromDataShape(DataShape ds) {
        String shortName = TwxDataShapeUtil.getShortName(ds);
        DbTable table = new DbTable(shortName);
        table.setDataShapeName(ds.getName());
        table.setDescription(ds.getDescription());
        this.addColumsFromDataShape(table, ds);
        return table;
    }

    protected DbTable addColumsFromDataShape(DbTable table, DataShape ds) {
        DbHandlerInfo info = handler.getHandlerInfo();

        for (FieldDefinition fieldDefinition : ds.getFields().getOrderedFieldsByOrdinal() ) {
            String fieldName = fieldDefinition.getName();
            BaseTypes baseType = fieldDefinition.getBaseType();
            // create and add a column ... 
            DbColumn col = table.addColumn(fieldName);
            col.setTwxType(baseType);
            col.setOrdinal(fieldDefinition.getOrdinal());
            col.setDescription(fieldDefinition.getDescription());

            fieldDefinition.getDefaultValue();
            fieldDefinition.isPrimaryKey();
        }
        return table;
    }

    // endrgion
    // region Model From DB ...
    // --------------------------------------------------------------------------------
    public DbModel queryModel(Connection con) throws SQLException {
        String catalog = con.getCatalog();
        DbModel dbModel = new DbModel(catalog);
        queryModelSchemas(dbModel, con);
        return dbModel;
    }

    protected DbModel queryModelSchemas(DbModel dbModel, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
            if (!handler.getHandlerInfo().isSystemSchema(schemaName)) {
                DbSchema dbSchema = dbModel.addSchema(schemaName);
                queryModelTables(dbSchema, con);
            }
        }
        return dbModel;
    }

    protected DbSchema queryModelTables(DbSchema dbSchema, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getTables(null, dbSchema.getName(), null, null);
        while (rs.next()) {
            String tableSchema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");
            if (tableSchema.equals(dbSchema.getName())) {
                DbTable dbTable = dbSchema.addTable(tableName);
                queryModelColumns(dbTable, con);
                queryModelIndexes(dbTable, con);
                queryModelKeys(dbTable, con);
                queryModelForeignKeys(dbTable, con);
            }
        }
        return dbSchema;
    }

    protected DbTable queryModelColumns(DbTable dbTable, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getColumns(null, dbTable.getSchemaName(), dbTable.getName(), null);
        while (rs.next()) {
            DbColumn col = dbTable.addColumn(rs.getString("COLUMN_NAME"));
            Integer typeId = rs.getInt("DATA_TYPE");
            // @Todo-AT: own type mapping ...
            JDBCType jdbcType = JDBCType.valueOf(typeId);
            BaseTypes twxType = BaseTypes.JDBCTypeToBaseType(typeId);

            col.setTwxType(twxType);
            col.setSqlType(jdbcType);
            col.setSqlTypeName(rs.getString("TYPE_NAME"));
            col.setSqlSize(rs.getInt("BUFFER_LENGTH"));
            col.setOrdinal(rs.getInt("ORDINAL_POSITION"));
            col.setNullable(((String) "YES").equals(rs.getString("IS_NULLABLE")));
            col.setAutoIncrement(((String) "YES").equals(rs.getString("IS_AUTOINCREMENT")));

            rs.getString("REMARKS"); // comments ...
            rs.getString("COLUMN_DEF"); // default value ...
        }
        return dbTable;
    }

    protected DbTable queryModelKeys(DbTable dbTable, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getPrimaryKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            String name = rs.getString("PK_NAME");
            if (name != null) {
                dbTable.setPrimaryKey(name);
                String colName = rs.getString("COLUMN_NAME");
                Integer colOrdinal = rs.getInt("KEY_SEQ");
                // set the column of the primary key ...
                dbTable.getColumn(colName).setPrimaryKeySeq(colOrdinal);
            }
        }
        return dbTable;
    }

    protected DbTable queryModelIndexes(DbTable dbTable, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getIndexInfo(null, dbTable.getSchemaName(), dbTable.getName(), false, false);
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

    protected DbTable queryModelForeignKeys(DbTable dbTable, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getImportedKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            DbForeignKey fk = dbTable.getOrAddForeignKey(rs.getString("FK_NAME"));
            fk.setForeignSchemaName(rs.getString("FKTABLE_SCHEM"));
            fk.setForeignTableName(rs.getString("FKTABLE_NAME"));
            fk.setOnDelete(rs.getInt("UPDATE_RULE"));
            fk.setOnUpdate(rs.getInt("DELETE_RULE"));

            fk.addColumn(rs.getInt("KEY_SEQ"), rs.getString("FKCOLUMN_NAME"), rs.getString("PKCOLUMN_NAME"));
        }
        return dbTable;
    }

}
