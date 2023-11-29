package twx.core.db.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.RootEntity;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.types.ConfigurationTable;
import com.thingworx.types.collections.ConfigurationTableCollection;
import com.thingworx.webservices.context.ThreadLocalContext;

import ch.qos.logback.classic.Logger;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbHandlerFactory;
import twx.core.db.liquibase.LiquibaseRunner;

public class DatabaseUtil {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseUtil.class);
    
    // region Database Handler ... 
    // --------------------------------------------------------------------------------  
    public static DbHandler getHandler() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getHandler(abstractDatabase);
    }

    public static DbHandler getHandler(String thingName) throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase(thingName);
        return getHandler(abstractDatabase);
    }

    public static DbHandler getHandler(AbstractDatabase abstractDatabase) throws Exception {
        return DbHandlerFactory.getInstance().getDbHandler(abstractDatabase);
    }
    // endregion 
    // Helpers to get Liquibase Runner ... 
    // --------------------------------------------------------------------------------
    public static LiquibaseRunner getLiquibaseRunner() throws Exception  {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        LiquibaseRunner lbRunner = new LiquibaseRunner(abstractDatabase);
        return lbRunner;
    }
    // endregion 
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

    public static DbHandler getDatabaseHandler() {
        return null;
    }

    public static DbHandler getDatabaseHandler(AbstractDatabase abstractDatabase ) {
        return null;
    }


    // Helpers to get Core handlers from Abstract Database ... 
    // --------------------------------------------------------------------------------
    public static String getDatabaseThingName() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getDatabaseThingName(abstractDatabase);
    }

    public static String getDatabaseThingName(AbstractDatabase abstractDatabase) {
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