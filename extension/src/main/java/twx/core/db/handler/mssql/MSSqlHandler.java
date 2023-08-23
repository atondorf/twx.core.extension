package twx.core.db.handler.mssql;

import javax.sql.DataSource;

import com.thingworx.things.database.AbstractDatabase;

import twx.core.db.handler.DbHandlerImplBase;
import twx.core.db.handler.DbHandlerInfo;
import twx.core.db.handler.JdbcModelManager;

public class MSSqlHandler extends DbHandlerImplBase {
    public static final String DATABASENAME = "MsSql";
    public static final String DATABASE_KEY = "sqlserver";

    public MSSqlHandler(DataSource dataSource) throws Exception {
        super(dataSource);
    }

    public MSSqlHandler(AbstractDatabase dbThing) throws Exception {
        super(dbThing);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.setModelManager( new JdbcModelManager(this) );
        this.setSqlBuilder( new MSSqlBuilder(this) );

        DbHandlerInfo info = this.getHandlerInfo();
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

    }

    @Override
    public String getName() {
        return DATABASENAME;
    }
    @Override
    public String getKey() {
        return DATABASE_KEY;
    }





}
