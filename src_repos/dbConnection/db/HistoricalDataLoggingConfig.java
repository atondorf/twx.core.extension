package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import twx.core.db.utils.ValueCollectionBuilder;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.things.Thing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.webservices.context.ThreadLocalContext;
import java.util.List;
import java.util.Map;

class HistoricalDataLoggingConfig {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(HistoricalDataLoggingConfig.class);
  
  private static final String HISTORICAL_DATA_LOG_CONFIG_DATA_SHAPE_NAME = "PTC.DBConnection.HistoricalDataLogConfig";
  
  private static final String DATA_SHAPE_NAME = "DataShapeName";
  
  private static final String FIELD_NAME = "FieldName";
  
  private static final String ACTION = "Action";
  
  private static final String LOGGING_HISTORICAL_DATA_ENABLE = "LogHistoricalData";
  
  private static final String GET_HISTORICAL_DATA_LOG_CONFIG = "GetHistoricalDataLogConfig";
  
  private static final String SERVICE_GET_COMPONENT_MANAGER_FOR_IDENTIFIER = "GetConfiguredManagerForIdentifier";
  
  private static final String RESULT = "result";
  
  private static final DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  static boolean isLogEnable() {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());
    if (queryDatabaseContext.isHistoricalDataLogEnable() == null) {
      InfoTable infoTable = callService("LogHistoricalData", new ValueCollection());
      boolean logEnable = getResult(infoTable);
      queryDatabaseContext.setHistoricalDataLogEnable(Boolean.valueOf(logEnable));
    } 
    return queryDatabaseContext.isHistoricalDataLogEnable().booleanValue();
  }
  
  static boolean isLogEnable(DataShape dataShape, FieldDefinition fieldDefinition, ActionType actionType) {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());
    if (queryDatabaseContext.getHistoricalDataLogMap() == null) {
      InfoTable result = callService("GetHistoricalDataLogConfig", new ValueCollection());
      queryDatabaseContext.setHistoricalDataLogMap(buildHistoricalDataLogMap(result));
    } 
    return isLogEnable(dataShape, fieldDefinition, actionType, queryDatabaseContext
        .getHistoricalDataLogMap());
  }
  
  private static boolean isLogEnable(DataShape dataShape, FieldDefinition fieldDefinition, ActionType actionType, Map<String, Map<String, List<ActionType>>> map) {
    Map<String, List<ActionType>> actionTypeMap = map.get(dataShape.getName());
    if (actionTypeMap != null) {
      String fieldName = (fieldDefinition != null) ? fieldDefinition.getName() : "ALL";
      List<ActionType> fieldActionTypes = actionTypeMap.get(fieldName);
      if (fieldActionTypes != null)
        return fieldActionTypes.contains(actionType); 
    } 
    return false;
  }
  
  private static Map<String, Map<String, List<ActionType>>> buildHistoricalDataLogMap(InfoTable result) {
    Map<String, Map<String, List<ActionType>>> historicalDataLogMap = Maps.newHashMap();
    if (result != null) {
      DataShape historicalDataLogConfigDataShape = dataShapeUtils.getDataShape("PTC.DBConnection.HistoricalDataLogConfig");
      FieldDefinition dataShapeNameFieldDefinition = historicalDataLogConfigDataShape.getFieldDefinition("DataShapeName");
      FieldDefinition fieldNameFieldDefinition = historicalDataLogConfigDataShape.getFieldDefinition("FieldName");
      FieldDefinition actionFieldDefinition = historicalDataLogConfigDataShape.getFieldDefinition("Action");
      for (ValueCollection valueCollection : result.getRows()) {
        String dataShapeName = valueCollection.getStringValue(dataShapeNameFieldDefinition.getName());
        String fieldName = valueCollection.getStringValue(fieldNameFieldDefinition.getName());
        ActionType actionType = ActionType.valueOf(valueCollection.getStringValue(actionFieldDefinition.getName()));
        addHistoricalDataLog(dataShapeName, fieldName, actionType, historicalDataLogMap);
      } 
    } 
    return historicalDataLogMap;
  }
  
  private static void addHistoricalDataLog(String dataShapeName, String fieldName, ActionType actionType, Map<String, Map<String, List<ActionType>>> historicalDataLogMap) {
    Map<String, List<ActionType>> actionTypeMap = historicalDataLogMap.computeIfAbsent(dataShapeName, k -> Maps.newHashMap());
    List<ActionType> fieldActionTypes = actionTypeMap.computeIfAbsent(fieldName, k -> Lists.newArrayList());
    if (!fieldActionTypes.contains(actionType))
      fieldActionTypes.add(actionType); 
  }
  
  private static InfoTable callService(String serviceName, ValueCollection parameters) {
    Thing thing = getHistoricalDataManager();
    if (thing.hasServiceDefinition(serviceName))
      try {
        return thing.processAPIServiceRequest(serviceName, parameters);
      } catch (Exception e) {
        _logger.error("Error running service " + serviceName, e);
        throw new ThingworxRuntimeException(e);
      }  
    return null;
  }
  
  private static boolean getResult(InfoTable infoTable) {
    if (infoTable != null && !infoTable.isEmpty()) {
      ValueCollection row = infoTable.getFirstRow();
      if (row.containsKey("result")) {
        Object value = row.getValue("result");
        if (value instanceof Boolean)
          return ((Boolean)value).booleanValue(); 
      } 
    } 
    return true;
  }
  
  private static Thing getHistoricalDataManager() {
    QueryDatabaseContext queryDatabaseContext = QueryContextCache.getQueryDatabaseContext(ThreadLocalContext.getQueryContextObj());
    if (queryDatabaseContext.getHistoricalDataManagerName() == null)
      queryDatabaseContext.setHistoricalDataManagerName(getHistoricalDataManagerThingName()); 
    return ThingUtility.findThingDirect(queryDatabaseContext.getHistoricalDataManagerName());
  }
  
  private static String getHistoricalDataManagerThingName() {
    try {
      ValueCollection params = (new ValueCollectionBuilder()).put("identifier", "PTC.DBConnection.HistoricalDataManager").get();
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
