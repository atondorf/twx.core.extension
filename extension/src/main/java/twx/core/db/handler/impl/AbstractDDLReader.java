package twx.core.db.handler.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.thingworx.types.BaseTypes;

import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbInfo;
import twx.core.db.model.DbColumn;
import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbForeignKeyColumn;
import twx.core.db.model.DbIndex;
import twx.core.db.model.DbIndexColumn;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbSchema;
import twx.core.db.model.DbTable;

public class AbstractDDLReader implements DDLReader {
    private DbHandler dbHandler = null;
    private DbInfo dbInfo = null;

    public AbstractDDLReader(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.dbInfo = dbHandler.getDbInfo();
    }

    // region DDLReader Interface ...
    // --------------------------------------------------------------------------------
    public DbModel queryModel() throws SQLException {
        return dbHandler.execute(connection -> {
            return queryModel(connection);
        });
    }

    // endregion
    // region DDLReader Helpers ...
    // --------------------------------------------------------------------------------
    public DbModel queryModel(Connection con) throws SQLException {
        String catalog = con.getCatalog();
        DbModel dbModel = new DbModel(catalog);
        // 1. Iteration, get schemas, tables, columns, indexes ...
        queryModelSchemas(dbModel, con);
        // 2. Iteration, get foreign keys
        queryModelForeignKeys(dbModel, con);
        return dbModel;
    }

    protected DbModel queryModelSchemas(DbModel dbModel, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
            if (!this.dbInfo.isSystemSchema(schemaName)) {
                // DbSchema dbSchema = this.dbInfo.isDefaultSchema(schemaName) ? dbModel.getDefaultSchema() : dbModel.getOrCreateSchema(schemaName);
                DbSchema dbSchema = this.dbInfo.isDefaultSchema(schemaName) ? dbModel.getDefaultSchema() : dbModel.getOrCreateSchema(schemaName);
                queryModelTables(dbSchema, con);
            }
        }
        return dbModel;
    }

    protected DbSchema queryModelTables(DbSchema dbSchema, Connection con) throws SQLException {
        // check for default Schema Name ...
        String dbSchemaName = dbSchema.isDefault() ? dbInfo.getDefaultSchema() : dbSchema.getName();
        ResultSet rs = con.getMetaData().getTables(null, dbSchemaName, null, null);
        while (rs.next()) {
            String tableSchema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");
            String tableType = rs.getString("TABLE_TYPE");
            if (tableSchema.equals(dbSchemaName) && !this.dbInfo.isSystemTable(tableName)) {
                DbTable dbTable = dbSchema.createTable(tableName);
                // dbTable.addSetting(DbTableSetting.TABLE_TYPE, tableType);
                queryModelColumns(dbTable, con);
                queryModelIndexes(dbTable, con);
                queryModelKeys(dbTable, con);
            }
        }
        return dbSchema;
    }

    protected DbTable queryModelColumns(DbTable dbTable, Connection con) throws SQLException {
        // check for default Schema Name ...
        String dbSchemaName = dbTable.getSchema().isDefault() ? dbInfo.getDefaultSchema() : dbTable.getSchema().getName();
        ResultSet rs = con.getMetaData().getColumns(null, dbSchemaName, dbTable.getName(), null);
        while (rs.next()) {
            DbColumn col = dbTable.createColumn(rs.getString("COLUMN_NAME"));
            // type Name ...
            String typename = rs.getString("TYPE_NAME");
            if (typename.equals("varchar") || typename.equals("varbinary")) {
                typename += "(" + rs.getString("CHAR_OCTET_LENGTH") + ")";
            }
            col.setTypeName(typename);
            // integer jdbcType and Thingworx Basetype ... 
            Integer jdbcType = rs.getInt("DATA_TYPE");
            col.setType(jdbcType);
            col.setTwxType(this.dbInfo.jdbc2Base(jdbcType));

            col.setSize(rs.getInt("COLUMN_SIZE"));
            col.setOrdinal(rs.getInt("ORDINAL_POSITION"));            
            col.setNullable( rs.getString("IS_NULLABLE").equals("YES") );
            col.setAutoIncrement( rs.getString("IS_AUTOINCREMENT").equals("YES") );
            col.setDefaultValue( rs.getString("COLUMN_DEF") );
        }
        return dbTable;
    }

    protected DbTable queryModelIndexes(DbTable dbTable, Connection con) throws SQLException {
        // check for default Schema Name ...
        String dbSchemaName = dbTable.getSchema().isDefault() ? dbInfo.getDefaultSchema() : dbTable.getSchema().getName();
        //
        ResultSet rs = con.getMetaData().getIndexInfo(null, dbSchemaName, dbTable.getName(), false, false);
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            if (name != null) {
                DbIndex index = dbTable.getOrCreateIndex(name);
                DbIndexColumn indexColumn = index.getOrCreateIndexColumn(rs.getString("COLUMN_NAME"));
                indexColumn.setOrdinal(rs.getInt("ORDINAL_POSITION"));
                if (!rs.getBoolean("NON_UNIQUE")) {
                    index.setUnique(true);
                }
            }
        }
        return dbTable;
    }

    protected DbTable queryModelKeys(DbTable dbTable, Connection con) throws SQLException {
        // check for default Schema Name ...
        String dbSchemaName = dbTable.getSchema().isDefault() ? dbInfo.getDefaultSchema() : dbTable.getSchema().getName();

        ResultSet rs = con.getMetaData().getPrimaryKeys(null, dbSchemaName, dbTable.getName());
        while (rs.next()) {
            String name = rs.getString("PK_NAME");
            if (name != null) {
                var index = dbTable.getIndex(name);
                if (index != null) {
                    index.setPrimarayKey(true);
                }
            }
        }
        return dbTable;
    }

    protected DbModel queryModelForeignKeys(DbModel dbModel, Connection con) throws SQLException {
        var schemas = dbModel.getSchemas();
        for( var schema : schemas ) {
            var tables = schema.getTables();  
            for( var table : tables ) {
                queryTableForeignKeys(table, con);    
            }
        }
        return dbModel;
    }

    protected DbTable queryTableForeignKeys(DbTable dbTable, Connection con) throws SQLException {
        ResultSet rs = con.getMetaData().getImportedKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            String name = rs.getString("FK_NAME");
            if (name != null) {
                DbForeignKey foreignKey = dbTable.getOrCreateForeignKey(name);
                foreignKey.setForeignSchemaName(rs.getString("PKTABLE_SCHEM"));
                foreignKey.setForeignTableName(rs.getString("PKTABLE_NAME"));
                foreignKey.setOnDelete(rs.getInt("UPDATE_RULE"));
                foreignKey.setOnUpdate(rs.getInt("DELETE_RULE"));
                DbForeignKeyColumn foreignKeyColumn = foreignKey.getOrCreateForeignKeyColumn(rs.getString("FKCOLUMN_NAME"));
                foreignKeyColumn.setOrdinal(rs.getInt("KEY_SEQ"));
                foreignKeyColumn.setForeignColumnName(rs.getString("PKCOLUMN_NAME"));
            }
        }
        return dbTable;
    }

}
