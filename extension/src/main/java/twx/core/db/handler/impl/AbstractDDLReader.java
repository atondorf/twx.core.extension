package twx.core.db.handler.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbHandler;
import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTable;
import twx.core.db.handler.DbInfo;

public class AbstractDDLReader implements DDLReader {
    private DbHandler   dbHandler = null;
    private DbInfo dbInfo = null;
    private Connection  connection = null;

    public AbstractDDLReader(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.dbInfo = dbHandler.getDbInfo();
        this.connection = dbHandler.getConnection();
    }

    public DbModel queryModel() throws SQLException {
        try {
            this.connection = dbHandler.getConnection();
            String catalog = connection.getCatalog();
            DbModel dbModel = new DbModel(catalog);
            this.queryTables(dbModel);
            
            return dbModel;
        }
        finally {
            dbHandler.close(this.connection);            
        }
    }

    protected DbModel queryTables(DbModel dbModel) throws SQLException {
        ResultSet rs = this.connection.getMetaData().getTables(null,null, null, null);
        while (rs.next()) {
            String tableSchema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");
            if( !this.dbInfo.isSystemSchema(tableSchema) ) {
                DbTable dbTable = dbModel.addTable(tableName);
/*              queryModelColumns(dbTable, con);
                queryModelIndexes(dbTable, con);
                queryModelKeys(dbTable, con);
                queryModelForeignKeys(dbTable, con);
*/            
            }
         }
        return dbModel;
    }

 
    public DbTable queryTable() {
        return null;
    }
   
}
