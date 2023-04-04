package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.Thing;
import com.thingworx.types.InfoTable;
import com.thingworx.webservices.context.ThreadLocalContext;

public class ConfigurationUtility {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ConfigurationUtility.class);
  
  private static final String ENTRY_POINT_CONFIGURATION_THING_NAME = "PTC.DBConnection.EntryPoint";
  
  private static final String SERVICE_GET_CONFIGURATION_MANAGER_NAME = "GetConfiguredComponentManager";
  
  private static final String RESULT = "result";
  
  static String getConfigurationThingName() {
    try {
      Thing entryPointConfigurationThing = ThingUtility.findThingDirect("PTC.DBConnection.EntryPoint");
      InfoTable result = entryPointConfigurationThing.processAPIServiceRequest("GetConfiguredComponentManager", null);
      if (result != null && result.getRowCount().intValue() > 0)
        return result.getFirstRow().getStringValue("result"); 
    } catch (Exception e) {
      _logger.warn("Error retrieving configuration manager", e);
      throw new ThingworxRuntimeException(e);
    } 
    throw new ThingworxRuntimeException("No result for: PTC.DBConnection.EntryPoint.GetConfiguredComponentManager");
  }
  
  static Thing getConfiguration() {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());
    if (queryDatabaseContext.getCustomConfigurationThingName() == null)
      queryDatabaseContext
        .setCustomConfigurationThingName(getConfigurationThingName()); 
    return 
      ThingUtility.findThingDirect(queryDatabaseContext.getCustomConfigurationThingName());
  }
}
