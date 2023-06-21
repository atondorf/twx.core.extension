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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
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
            if ( thing.isVisible() ) {
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
        return getDataSource().getConnection();
    }

    public static String getKey() throws Exception {
        AbstractDatabase abstractDatabase = getAbstractDatabase();
        Object configValue = null;
        if (abstractDatabase instanceof com.thingworx.things.database.DatabaseSystem) {
          configValue = abstractDatabase.getConfigurationSetting("ConnectionInfo", "jDBCConnectionURL");
        } else if (abstractDatabase instanceof com.thingworx.things.database.SQLThing) {
          ConfigurationTableCollection configurationData = abstractDatabase.getConfigurationData();
          ConfigurationTable persistenceProviderConfig = configurationData.getConfigurationTable("ConnectionProvider");
          String persistenceProviderName = persistenceProviderConfig.getRowValue("persistenceProviderName").getStringValue();
          RootEntity persistenceProvider = EntityUtilities.findEntityDirect(persistenceProviderName, RelationshipTypes.ThingworxRelationshipTypes.PersistenceProvider);
          configValue = persistenceProvider.getConfigurationSetting("ConnectionInformation", "jdbcUrl");
        } 
        if (configValue instanceof String) {
          String jdbcUrl = (String)configValue;
          int startIndex = jdbcUrl.indexOf(':') + 1;
          int endIndex = jdbcUrl.indexOf(':', startIndex);
          if (!jdbcUrl.startsWith("jdbc:") || endIndex == -1)
            throw new IllegalArgumentException("Invalid JDBC url."); 
          return jdbcUrl.substring(startIndex, endIndex);
        } 
        throw new ThingworxRuntimeException("jdbc connection url need to be set");
      }

}