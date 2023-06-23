package twx.core.db.imp;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.things.Thing;
import com.thingworx.webservices.context.ThreadLocalContext;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.webservices.context.ThreadLocalContext;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ConfigurationTableCollection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.types.ConfigurationTable;
import com.thingworx.entities.RootEntity;

public class DBUtil {

    public static AbstractDatabase getAbstractDatabase() throws Exception {
        Object ctx = ThreadLocalContext.getMeContext();
        if (ctx instanceof AbstractDatabase)
            return (AbstractDatabase) ctx;
        throw new ThingworxRuntimeException("Object : " + ctx + " is not Database.");
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

    public static DataSource getDataSource() throws Exception {
        return getAbstractDatabase().getDataSource();
    }

    public static Connection getConnection() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        if (abstractDatabase != null) {
            return abstractDatabase.getConnection();
        }
        return null;
    }

    public static Connection beginTransaction() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        abstractDatabase.beginTransaction();
        return abstractDatabase.getConnection();
    }

    public static void endTransaction(Connection conn) throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        if (!conn.getAutoCommit()) {
            abstractDatabase.commit(conn);
        }
        abstractDatabase.endTransaction(conn);
    }

    public static void commit(Connection conn) throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        if (!conn.getAutoCommit()) {
            abstractDatabase.commit(conn);
        }
    }

    public static void rollback(Connection conn) throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        if (!conn.getAutoCommit()) {
            abstractDatabase.rollback(conn);
        }
    }

    public static DatabaseMetaData getMetaData() throws Exception {
        return getConnection().getMetaData();
    }

    public static String getConfiguredKey() throws Exception {
        Object configValue = getConfiguredJDBCURL();
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
        Object configValue = getConfiguredJDBCURL();
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
        Object configValue = getConfiguredJDBCURL();
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

    public static JSONObject getSpec(Connection conn) throws Exception {
        JSONObject obj = new JSONObject();
        var meta = conn.getMetaData();
        obj.put("dbProductName", meta.getDatabaseProductName());
        obj.put("dbProductVersion", meta.getDatabaseProductVersion());
        obj.put("dbDriverName", meta.getDriverName());
        obj.put("dbDriverVersion", meta.getDriverVersion());
        return obj;
    }

    public static String getCatalog(Connection conn) throws Exception {
        return conn.getCatalog();
    }

    public static String getSchemas(Connection conn) throws Exception {
        return "";
    }

    public static String getTables() throws Exception {
        return "";
    }

    public static String getColumns() throws Exception {
        return "";
    }

}