package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import java.util.Map;

public class DatabaseHandlerLocator {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseHandlerLocator.class);
  
  private static final String POSTGRES = "postgresql";
  
  private static final String SQLSERVER = "sqlserver";
  
  private static Map<String, Class> databaseHandlerClassMap = Maps.newHashMap();
  
  private static Map<String, DatabaseHandler> databaseHandlerMap = Maps.newHashMap();
  
  static {
    databaseHandlerClassMap.put("postgresql", PostgresDatabaseHandler.class);
    databaseHandlerClassMap.put("sqlserver", SqlServerDatabaseHandler.class);
  }
  
  protected DatabaseHandler getDatabaseHanlder() throws Exception {
    String key = DatabaseUtility.getKey();
    DatabaseHandler databaseHandler = databaseHandlerMap.get(key);
    if (databaseHandler != null)
      return databaseHandler; 
    Class<DatabaseHandler> databaseHandlerClass = databaseHandlerClassMap.get(key);
    if (databaseHandlerClass != null)
      try {
        DatabaseHandler newDatabaseHandler = databaseHandlerClass.newInstance();
        databaseHandlerMap.put(key, newDatabaseHandler);
        return newDatabaseHandler;
      } catch (InstantiationException e) {
        _logger.error("InstantiationException:" + databaseHandlerClass.getName(), e);
      } catch (IllegalAccessException e) {
        _logger.error("IllegalAccessException:" + databaseHandlerClass.getName(), e);
      }  
    throw new ThingworxRuntimeException(key + " not supported.");
  }
  
  protected static void clearCache() {
    databaseHandlerMap = Maps.newHashMap();
  }
}
