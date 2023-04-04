package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.RootEntity;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.types.ConfigurationTable;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ConfigurationTableCollection;
import com.thingworx.webservices.context.ThreadLocalContext;
import java.sql.Connection;

public class DatabaseUtility {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseUtility.class);

  private static final String CONNECTION_INFO = "ConnectionInfo";

  private static final String JDBC_CONNECTION_URL = "jDBCConnectionURL";

  private static final String JDBC = "jdbc:";

  private static final String GET_DATABASE_THING_NAME = "GetDefaultConfiguredDatabaseThing";

  private static final String RESULT = "result";

  private static Thing getDatabaseThing() throws Exception {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());

    if (queryDatabaseContext.getDatabaseThingName() == null)
      queryDatabaseContext.setDatabaseThingName(getDatabaseThingName());

    return ThingUtility.findThingDirect(queryDatabaseContext.getDatabaseThingName());
  }

  private static String getDatabaseThingName() throws Exception {
    Thing configurationThing = ConfigurationUtility.getConfiguration();
    InfoTable result = configurationThing.processAPIServiceRequest("GetDefaultConfiguredDatabaseThing", null);
    if (result != null && result.getRowCount().intValue() > 0) {
      String databaseThingName = result.getFirstRow().getStringValue("result");
      if (databaseThingName != null)
        return databaseThingName;
    }
    _logger.error("No result return for service:GetDefaultConfiguredDatabaseThing");
    throw new ThingworxRuntimeException("Can't find Database thing name on " + configurationThing
        .getName());
  }

  private static AbstractDatabase getAbstractDatabase() throws Exception {
    Object context = ThreadLocalContext.getMeContext();
    if (context instanceof AbstractDatabase)
      return (AbstractDatabase) context;
    Thing databaseThing = getDatabaseThing();
    if (databaseThing instanceof AbstractDatabase)
      return (AbstractDatabase) databaseThing;
    throw new ThingworxRuntimeException("Object : " + context + " is not Database.");
  }

  protected static DatabaseHandler getDatabaseHandler() throws Exception {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());

    if (queryDatabaseContext.getDatabaseHandler() == null)
      queryDatabaseContext.setDatabaseHandler(getDatabaseHandlerLocator().getDatabaseHanlder());

    return queryDatabaseContext.getDatabaseHandler();
  }

  private static DatabaseHandlerLocator getDatabaseHandlerLocator() {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());

    if (queryDatabaseContext.getDatabaseHandlerLocator() == null)
      queryDatabaseContext.setDatabaseHandlerLocator(new DatabaseHandlerLocator());
      
    return queryDatabaseContext.getDatabaseHandlerLocator();
  }

  public static <T> T executeHandler(ExecuteHandler<T> executeHandler) throws Exception {
    return getDatabaseHandler().executeHandler(executeHandler);
  }

  protected static String getKey() throws Exception {
    AbstractDatabase abstractDatabase = getAbstractDatabase();
    Object configValue = null;
    if (abstractDatabase instanceof com.thingworx.things.database.DatabaseSystem) {
      configValue = abstractDatabase.getConfigurationSetting("ConnectionInfo", "jDBCConnectionURL");
    } else if (abstractDatabase instanceof com.thingworx.things.database.SQLThing) {
      ConfigurationTableCollection configurationData = abstractDatabase.getConfigurationData();
      ConfigurationTable persistenceProviderConfig = configurationData.getConfigurationTable("ConnectionProvider");
      String persistenceProviderName = persistenceProviderConfig.getRowValue("persistenceProviderName")
          .getStringValue();
      RootEntity persistenceProvider = EntityUtilities.findEntityDirect(persistenceProviderName,
          RelationshipTypes.ThingworxRelationshipTypes.PersistenceProvider);
      configValue = persistenceProvider.getConfigurationSetting("ConnectionInformation", "jdbcUrl");
    }
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

  protected static ConfigurationTable getConfigurationTable(String configurationTableName) throws Exception {
    return getAbstractDatabase().getConfigurationTable(configurationTableName);
  }

  protected static Connection getConnection() throws Exception {
    return getAbstractDatabase().getConnection();
  }
}
