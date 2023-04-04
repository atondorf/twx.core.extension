package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Maps;
import twx.core.db.utils.ValueCollectionBuilder;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.Thing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONObject;

public class DatabaseInfoManager {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseInfoManager.class);
  
  private static final String DATABASE_MANAGER_THING_NAME = "PTC.DBConnection.Manager";
  
  private static final String SERVICE_GET_COMPONENT_MANAGER_FOR_IDENTIFIER = "GetConfiguredManagerForIdentifier";
  
  private static final String GET_DBINFO_SERVICE_NAME = "GetDBInfo";
  
  private static final String RESULT = "result";
  
  private static final String FORCE = "force";
  
  private JsonUtility jsonUtility = new JsonUtility();
  
  private DatabaseInfo databaseInfo = null;
  
  public static DatabaseInfoManager getInstance() {
    return new DatabaseInfoManager();
  }
  
  protected DatabaseInfo getDatabaseInfo(JSONObject dbInfo) {
    try {
      return this.jsonUtility.<DatabaseInfo>getObjetFromJson(dbInfo, DatabaseInfo.class);
    } catch (IOException e) {
      _logger.error(e.getMessage(), e);
      return null;
    } 
  }
  
  private DatabaseInfo getDatabaseInfo() {
    if (this.databaseInfo == null)
      this.databaseInfo = getDatabaseInfo(getJsonDatabaseInfo()); 
    return this.databaseInfo;
  }
  
  public Map<String, Map<String, ForeignKey>> getForeignKeyCache() {
    Map<String, Map<String, ForeignKey>> foreignKeyCache = Maps.newHashMap();
    DatabaseInfo databaseInfo = getDatabaseInfo();
    if (databaseInfo != null)
      for (DataShapeDatabaseInfo dataShapeDatabaseInfo : databaseInfo.getDbInfo()) {
        List<ForeignKey> foreignKeys = dataShapeDatabaseInfo.getForeignKeys();
        if (foreignKeys != null)
          for (ForeignKey foreignKey : foreignKeys) {
            Map<String, ForeignKey> foreignKeyMap = foreignKeyCache.computeIfAbsent(dataShapeDatabaseInfo.getDataShapeName(), k -> Maps.newHashMap());
            foreignKeyMap.put(foreignKey.getName(), foreignKey);
          }  
      }  
    return foreignKeyCache;
  }
  
  protected Optional<DataShapeDatabaseInfo> getDataShapeDatabaseInfo(String dataShapeName) {
    DatabaseInfo databaseInfo = getDatabaseInfo();
    if (databaseInfo != null)
      return databaseInfo.getDataShapeDatabaseInfo(dataShapeName); 
    return Optional.empty();
  }
  
  private JSONObject getJsonDatabaseInfo() {
    try {
      Thing thing = ThingUtility.findThingDirect(getDatabaseManagerThingName());
      ValueCollection valueCollection = new ValueCollection();
      valueCollection.SetBooleanValue("force", Boolean.TRUE);
      InfoTable infoTable = thing.processAPIServiceRequest("GetDBInfo", valueCollection);
      if (infoTable.getRowCount().intValue() > 0) {
        ValueCollection row = infoTable.getFirstRow();
        return (JSONObject)row.getValue("result");
      } 
    } catch (Exception e) {
      _logger.error(e.getMessage(), e);
    } 
    return null;
  }
  
  private static String getDatabaseManagerThingName() {
    try {
      ValueCollection params = (new ValueCollectionBuilder()).put("identifier", "PTC.DBConnection.Manager").get();
      Thing configurationThing = ConfigurationUtility.getConfiguration();
      InfoTable result = configurationThing.processAPIServiceRequest("GetConfiguredManagerForIdentifier", params);
      if (result != null && result.getRowCount().intValue() > 0)
        return result.getFirstRow().getStringValue("result"); 
    } catch (Exception e) {
      _logger.warn("Error retrieving historical data manager", e);
      throw new ThingworxRuntimeException(e);
    } 
    throw new ThingworxRuntimeException("Historical data manager thing name can't be found");
  }
}
