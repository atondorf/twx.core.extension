package twx.core.db.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.RootEntity;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.types.ConfigurationTable;
import com.thingworx.types.collections.ConfigurationTableCollection;
import com.thingworx.webservices.context.ThreadLocalContext;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbHandlerImplBase;
import twx.core.db.handler.mssql.MSSqlHandler;
import twx.core.db.model.DbModel;

public class TwxDbUtil {

    // region Handling of cached Model ...
    // --------------------------------------------------------------------------------
    private static final ConcurrentMap<String, DbModel>    modelMap    = new ConcurrentHashMap<String, DbModel>();
    private static final ConcurrentMap<String, DbHandler>  handlerMap  = new ConcurrentHashMap<String, DbHandler>();

    // Helpers to get Abstract Database ... 
    // --------------------------------------------------------------------------------
    public static AbstractDatabase getAbstractDatabase() throws Exception {
        Object ctx = ThreadLocalContext.getMeContext();
        if (ctx instanceof AbstractDatabase)
            return (AbstractDatabase) ctx;
        throw new ThingworxRuntimeException("Object : " + ctx + " is not a Database Thing");
    }

    public static AbstractDatabase getAbstractDatabase(String thingName) {
        Thing thing = (Thing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null) {
            if (thing.isVisible()) {
                if (thing instanceof AbstractDatabase) {
                    return (AbstractDatabase) thing;
                }
                throw new ThingworxRuntimeException("Object : " + thing.getName() + " is not Database.");
            }
            throw new ThingworxRuntimeException("Thing:" + thing.getName() + " is not visible for current user.");
        }
        throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
    }

    public static AbstractDatabase getAbstractDatabaseDirect(String thingName) {
        Thing thing = (Thing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null) {
            if (thing instanceof AbstractDatabase) {
                return (AbstractDatabase) thing;
            }
            throw new ThingworxRuntimeException("Object : " + thing.getName() + " is not Database.");
        }
        throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
    }
    // Helpers to get Core handlers from Abstract Database ... 
    // --------------------------------------------------------------------------------
    public static String getDbThingName() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getDbThingName(abstractDatabase);
    }

    public static String getDbThingName(AbstractDatabase abstractDatabase) {
        // check if the AbstractDatabase is a Database Thing
        String thingName = "";
        if (abstractDatabase instanceof com.thingworx.things.database.DatabaseSystem) {
            thingName = abstractDatabase.getName();
        }
        // ceck if the AbstractDatabase is a SQL Thing
        else if (abstractDatabase instanceof com.thingworx.things.database.SQLThing) {
            ConfigurationTableCollection configurationData = abstractDatabase.getConfigurationData();
            ConfigurationTable persistenceProviderConfig = configurationData.getConfigurationTable("ConnectionProvider");
            thingName = persistenceProviderConfig.getRowValue("persistenceProviderName").getStringValue();
        }
        return thingName;
    }

    public static List<String> getHandlerNames() {
        return new ArrayList<>(handlerMap.keySet());
    }

    public static DbHandler getHandler() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getHandler(abstractDatabase);
    }

    public static DbHandler getHandler(AbstractDatabase abstractDatabase) throws Exception {
        String dbThingName = getDbThingName(abstractDatabase);
        if( handlerMap.containsKey(dbThingName) )
            return handlerMap.get(dbThingName);
        return createHandler(abstractDatabase);
    }

    public static DbHandler getHandler(String handlerName) throws Exception {
        if( handlerMap.containsKey(handlerName) )
            return handlerMap.get(handlerName);
        return null;
    }

    public static DbHandler createHandler() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return createHandler(abstractDatabase);
    }

    public static DbHandler createHandler(AbstractDatabase abstractDatabase) throws Exception {
        String dbThingName = getDbThingName(abstractDatabase);
        // TODO - Factory for creation ... currently only MSSQL
        DbHandlerImplBase dbHandler = new MSSqlHandler(abstractDatabase);
        handlerMap.put( dbThingName, dbHandler );
        dbHandler.setAppliction(getConfiguredApplication(abstractDatabase));
        dbHandler.setCatalog(getConfiguredCatalog(abstractDatabase));
        return dbHandler;    
    }

    public static void removeHandler(String modelName) throws Exception {
        var keys = handlerMap.keySet().iterator();
        while (keys.hasNext()) {
            String currentKey = (String) keys.next();
            if( currentKey.equals(modelName) )
                keys.remove();
        }
    }

    public static void purgeHandlers() throws Exception {
        var keys = handlerMap.keySet().iterator();
        while (keys.hasNext()) {
            String currentKey = (String) keys.next();
            keys.remove();
        }
    }

    // endregion 
    // Helpers to get JDBC configuration items ... 
    // --------------------------------------------------------------------------------
    public static String getConfiguredKey() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getConfiguredKey(abstractDatabase);
    }

    public static String getConfiguredKey(AbstractDatabase abstractDatabase) throws Exception {
        Object configValue = getConfiguredJDBCURL(abstractDatabase);
        if (configValue instanceof String) {
            String jdbcUrl = (String) configValue;
            int startIndex = jdbcUrl.indexOf(':') + 1;
            int endIndex = jdbcUrl.indexOf(':', startIndex);
            if (!jdbcUrl.startsWith("jdbc:") || endIndex == -1)
                throw new IllegalArgumentException("Invalid JDBC url.");
            return jdbcUrl.substring(startIndex, endIndex);
        }
        throw new ThingworxRuntimeException("jdbc connection url need to be set");
    }

    public static String getConfiguredCatalog() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getConfiguredCatalog(abstractDatabase);
    }

    public static String getConfiguredCatalog(AbstractDatabase abstractDatabase) throws Exception {
        Object configValue = getConfiguredJDBCURL(abstractDatabase);
        if (configValue instanceof String) {
            String jdbcUrl = (String) configValue;
            int startIndex = jdbcUrl.indexOf("databaseName=") + 13;
            int endIndex = jdbcUrl.indexOf(';', startIndex);
            if ( startIndex < 13 || endIndex == -1)
                throw new IllegalArgumentException("Invalid JDBC url. Does not contain databaseName=[]");
            return jdbcUrl.substring(startIndex, endIndex);
        }
        throw new ThingworxRuntimeException("jdbc connection url need to be set");
    }

    public static String getConfiguredApplication() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getConfiguredApplication(abstractDatabase);
    }

    public static String getConfiguredApplication(AbstractDatabase abstractDatabase) throws Exception {
        Object configValue = getConfiguredJDBCURL(abstractDatabase);
        if (configValue instanceof String) {
            String jdbcUrl = (String) configValue;
            int startIndex  = jdbcUrl.indexOf("applicationName=") + 16;
            int endIndex    = jdbcUrl.indexOf(';', startIndex);
            if ( startIndex < 16 || endIndex == -1)
                throw new IllegalArgumentException("Invalid JDBC url. Does not contain applicationName=[]");
            return jdbcUrl.substring(startIndex, endIndex);
        }
        throw new ThingworxRuntimeException("jdbc connection url need to be set");
    }

    public static Object getConfiguredJDBCURL() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getConfiguredJDBCURL(abstractDatabase);
    }

    public static Object getConfiguredJDBCURL(AbstractDatabase abstractDatabase) throws Exception {
        Object configValue = null;
        // check if the AbstractDatabase is a Database Thing
        if (abstractDatabase instanceof com.thingworx.things.database.DatabaseSystem) {
            configValue = abstractDatabase.getConfigurationSetting("ConnectionInfo", "jDBCConnectionURL");
        }
        // ceck if the AbstractDatabase is a SQL Thing
        else if (abstractDatabase instanceof com.thingworx.things.database.SQLThing) {
            ConfigurationTableCollection configurationData = abstractDatabase.getConfigurationData();
            ConfigurationTable persistenceProviderConfig = configurationData.getConfigurationTable("ConnectionProvider");
            String persistenceProviderName = persistenceProviderConfig.getRowValue("persistenceProviderName").getStringValue();
            RootEntity persistenceProvider = EntityUtilities.findEntityDirect(persistenceProviderName, RelationshipTypes.ThingworxRelationshipTypes.PersistenceProvider);
            configValue = persistenceProvider.getConfigurationSetting("ConnectionInformation", "jdbcUrl");
        }
        return configValue;
    }
    // endregion
}