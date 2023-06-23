package twx.core.db.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import twx.core.db.IDatabaseHandler;

public class DBModelUtil {
    
    IDatabaseHandler           db = null;
    Connection          con = null;
    DatabaseMetaData    meta = null;

    public DBModelUtil(IDatabaseHandler db, Connection con) throws SQLException {
        this.db = db;
        this.con = con;
        this.meta = con.getMetaData();
    }

    public DbModel queryModelFromDB() throws SQLException {
        String dbName = this.con.getCatalog();
        String productName      = meta.getDatabaseProductName();
        String productVersion   = meta.getDatabaseProductVersion();
        String driverName       = meta.getDriverName();
        String driverVersion    = meta.getDriverVersion();

        DbModel dbModel = new DbModel(dbName);
        return this.addSchemas(dbModel);
    }

    protected DbModel addSchemas(DbModel dbModel) throws SQLException {
        ResultSet rs = meta.getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
/*/            if (!isSystemSchema(schemaName)) {
                DbSchema dbSchema = dbModel.addSchema(schemaName);
                // this.addTables(dbSchema);
            }
*/            
        }
        return dbModel;
    }

    protected DbSchema addTables(DbSchema dbSchema) throws SQLException {
        ResultSet rs = meta.getTables(null, dbSchema.getName(), null, null);
        while (rs.next()) {
            String tableSchema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");
            if (tableSchema.equals(dbSchema.getName())) {
                DbTable dbTable = dbSchema.addTable(tableName);
                this.addColumns(dbTable);
                this.addKeys(dbTable);
                this.addIndexes(dbTable);
                this.addForeignKeys(dbTable);
            }
        }
        return dbSchema;
    }

    protected DbTable addColumns(DbTable dbTable) throws SQLException {
        ResultSet rs = meta.getColumns(null, dbTable.getSchemaName(), dbTable.getName(), null);
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

    protected DbTable addKeys(DbTable dbTable) throws SQLException {
        ResultSet rs = meta.getPrimaryKeys(null, dbTable.getSchemaName(), dbTable.getName());
        while (rs.next()) {
            String colName = rs.getString("COLUMN_NAME");
            DbColumn column = dbTable.getColumn(colName);
            column.setPrimaryKeySeq(rs.getInt("KEY_SEQ"));
        }
        return dbTable;
    }

    protected DbTable addIndexes(DbTable dbTable) throws SQLException {
        ResultSet rs = meta.getIndexInfo(null,dbTable.getSchemaName(), dbTable.getName(), false, false );
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            if( name != null ) {
                DbIndex index = dbTable.getOrAddIndex(name);
                index.setColumn(rs.getString("COLUMN_NAME"));
                index.setType(rs.getInt("TYPE"));
                index.setUnique(!rs.getBoolean("NON_UNIQUE"));
            }
        }
        return dbTable;
    }

    protected DbTable addForeignKeys(DbTable dbTable) throws SQLException {
        ResultSet rs = meta.getImportedKeys(null, dbTable.getSchemaName(), dbTable.getName());
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
