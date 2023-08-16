package twx.core.db.imp;

import java.sql.Connection;

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

import twx.core.db.IDatabaseHandler;
import twx.core.db.model.DBModelManager;
import twx.core.db.model.DbModel;

public class DBUtil {

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
    // endregion
    // Helpers to get JDBC Connection objects  from Abstract Database ... 
    // --------------------------------------------------------------------------------
    public static DataSource getDataSource() throws Exception {
        return getAbstractDatabase().getDataSource();
    }

    public static DataSource getDataSource(String thingName) throws Exception {
        return getAbstractDatabase(thingName).getDataSource();
    }

    public static Connection getConnection() throws Exception {
        return getAbstractDatabase().getConnection();
    }

    public static Connection getConnection(String thingName) throws Exception {
        return getAbstractDatabase(thingName).getConnection();
    }
    // endregion 
    // Helpers to get Core handlers from Abstract Database ... 
    // --------------------------------------------------------------------------------
    public static IDatabaseHandler getDatabaseHandler() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getDatabaseHandler(abstractDatabase);
    }

    public static IDatabaseHandler getDatabaseHandler(AbstractDatabase abstractDB) throws Exception {
        // TODO ... change this in future to implement other DBs than SQL-Server ... 
        return new MsSQLDatabaseHandler( abstractDB, getConfiguredApplication(abstractDB) );
    }

    public static DbModel getDBModel() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        return getDBModel(abstractDatabase);
    }

    public static DbModel getDBModel(AbstractDatabase abstractDB) throws Exception {
        return DBModelManager.getModel( getConfiguredApplication(abstractDB) );
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