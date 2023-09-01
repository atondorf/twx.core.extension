package twx.core.db.handler.mssql;

import java.sql.Connection;
import java.sql.SQLException;

import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db.handler.DDLBuilder;
import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.handler.impl.AbstractHandler;
import twx.core.db.model.DbModel;

public class MsSqlHandler extends AbstractHandler {
    public static final String DATABASENAME = "MsSql";
    public static final String DATABASE_KEY = "sqlserver";
    public static final String DATABASE_DEFAULT_SCHEMA = "dbo";

    public MsSqlHandler(ConnectionManager connectionManager) throws Exception {
        super(connectionManager);
    }

    @Override
    public void initialize() {
        super.initialize();
        
        // initialise the DbInfos, Typemaps & Co. 
        DbInfo info = this.getDbInfo();
        // system Schemas ...
        info.addSystemSchema("DB_ACCESSADMIN");
        info.addSystemSchema("DB_DATAREADER");
        info.addSystemSchema("DB_BACKUPOPERATOR");
        info.addSystemSchema("DB_DATAWRITER");
        info.addSystemSchema("DB_DDLADMIN");
        info.addSystemSchema("DB_DENYDATAREADER");
        info.addSystemSchema("DB_DENYDATAWRITER");
        info.addSystemSchema("DB_OWNER");
        info.addSystemSchema("DB_SECURITYADMIN");
        info.addSystemSchema("GUEST");
        info.addSystemSchema("INFORMATION_SCHEMA");
        info.addSystemSchema("SYS");
        // Type Mapping ...

        // create the DB-Model ... 
        this.setDbModel( new DbModel("Test") );
    }

    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    @Override
    public String getName() {
        return DATABASENAME;
    }

    @Override
    public String getKey() {
        return DATABASE_KEY;
    }
    
    @Override
    public String getDefaultCatalog() {
        return getConnectionManager().getCatalog();
    }

    @Override
    public String getDefaultSchema() {
        return DATABASE_DEFAULT_SCHEMA;
    }

    // endregion
    // region Connections & Transactions ...
    // --------------------------------------------------------------------------------


    // endregion

    // region DDL Handler ...
    // --------------------------------------------------------------------------------
    @Override
    public DDLBuilder getDDLBuilder() {
        return new MsSqlDDLBuilder(this);
    }

    @Override
    public DDLReader getDDLReader() {
        return new MsSqlDDLReader(this);
    }

    @Override
    public SQLBuilder getSqlBuilder() {
        return new MsSqlSQLBuilder(this);
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    
    // endregion

}
