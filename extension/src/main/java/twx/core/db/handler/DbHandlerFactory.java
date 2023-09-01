package twx.core.db.handler;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.thingworx.things.database.AbstractDatabase;

import twx.core.db.ConnectionManager;
import twx.core.db.handler.mssql.MsSqlHandler;
import twx.core.db.impl.AbstractDatabaseConnectionManager;
import twx.core.db.impl.DataSourceConnectionManager;
import twx.core.db.util.DatabaseUtil;

public class DbHandlerFactory {
    private static final DbHandlerFactory instance = new DbHandlerFactory();

    private final Map<String, DbHandler>  handlerMap  = new HashMap<>();

    private DbHandlerFactory() {}

    public static DbHandlerFactory getInstance() {
        return DbHandlerFactory.instance;
    }

    public DbHandler getDbHandler(AbstractDatabase abstractDatabase) throws Exception {
        // TODO ... clearyfy if one Handler per SQLTHing or per DatabaseThing ... 
        String thingName = abstractDatabase.getName();
        DbHandler dbHandler = this.handlerMap.get(thingName);
        if( dbHandler == null ) {
            dbHandler = this.createDbHandler(abstractDatabase);
            handlerMap.put(thingName, dbHandler);
        }
        return dbHandler;
    }

    public DbHandler createDbHandler(AbstractDatabase abstractDatabase ) throws Exception {
        String dbKey = DatabaseUtil.getConfiguredKey(abstractDatabase);
        switch( dbKey ) {
            case MsSqlHandler.DATABASE_KEY:
                return createMsSqlHandler(abstractDatabase);
            default:
                throw new Exception("DatabaseType is not supported ... ");
        }
    }

    public DbHandler createMsSqlHandler(AbstractDatabase abstractDatabase) throws Exception {
        var connectionManager = new AbstractDatabaseConnectionManager(abstractDatabase);
        return createMsSqlHandler(connectionManager);
    }

    public DbHandler createMsSqlHandler(DataSource datasource) throws Exception {
        var connectionManager = new DataSourceConnectionManager(datasource);
        return createMsSqlHandler(connectionManager);
    }

    public DbHandler createMsSqlHandler(ConnectionManager connectionManager) throws Exception {
        var databaseHandler = new MsSqlHandler(connectionManager);
        return databaseHandler;
    }
}
