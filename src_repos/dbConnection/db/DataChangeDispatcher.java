package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.collections.EntityReferenceCollection;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.ServiceDefinition;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.ConfigurationTable;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import java.util.List;
import java.util.Map;

public class DataChangeDispatcher {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DataChangeDispatcher.class);
  
  private static final String DATA_EVENT_DATASHAPE_NAME = "PTC.DBConnection.DataChange";
  
  private static final String PRE_ACTION_VALIDATOR_THING_SHAPE_NAME = "PTC.DBConnection.PreActionValidator_TS";
  
  private static final String ON_ACTION_PROCESSOR_THING_SHAPE_NAME = "PTC.DBConnection.OnActionProcessor_TS";
  
  private static final String POST_ACTION_HANDLER_THING_SHAPE_NAME = "PTC.DBConnection.PostActionHandler_TS";
  
  private static final String DATABASE_CONFIGURATION_TABLE_NAME = "DatabaseValidationConfigurationTable";
  
  private static final String PRE_ACTION_VALIDATOR_STRING = "PreActionValidator";
  
  private static final String ON_ACTION_PROCESSOR_STRING = "OnActionProcessor";
  
  private static final String POST_ACTION_HANDLER_STRING = "PostActionHandler";
  
  private static final String ACTION = "Action";
  
  private static final String PRE_ACTION = "Pre";
  
  private static final String ON_ACTION = "On";
  
  private static final String POST_ACTION = "Post";
  
  private static final String DATA_CHANGES = "DataChanges";
  
  private static final String ACTION_TYPE = "ActionType";
  
  private static final String DATA_SHAPE_NAME = "DataShapeName";
  
  private static final String VALUES = "Values";
  
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private InfoTableUtils infoTableUtils = new InfoTableUtils();
  
  protected void preDispatchDataChange(List<DataChange> dataChanges) throws Exception {
    if (dataChanges != null && dataChanges.size() > 0) {
      getThings(ActionService.POST);
      dispatchDataChange(ActionService.PRE, dataChanges);
    } 
  }
  
  protected void onDispatchDataChange(List<DataChange> dataChanges) throws Exception {
    dispatchDataChange(ActionService.ON, dataChanges);
  }
  
  protected void postDispatchDataChange(List<DataChange> dataChanges) {
    try {
      dispatchDataChange(ActionService.POST, dataChanges);
    } catch (Exception e) {
      _logger.error(e.getMessage(), e);
    } 
  }
  
  private void dispatchDataChange(ActionService actionService, List<DataChange> dataChanges) throws Exception {
    if (dataChanges != null && dataChanges.size() > 0) {
      List<Thing> things = getThings(actionService);
      if (things.size() > 0) {
        Map<ActionType, Map<String, InfoTable>> map = getEventMap(dataChanges);
        for (Thing thing : things)
          dispatchChange(thing, map, actionService); 
      } 
    } 
  }
  
  private List<Thing> getThings(ActionService actionService) throws Exception {
    List<Thing> things = Lists.newArrayList();
    ConfigurationTable configurationTable = DatabaseUtility.getConfigurationTable("DatabaseValidationConfigurationTable");
    if (configurationTable != null)
      for (ValueCollection row : configurationTable.getRows()) {
        if (row != null) {
          String thingName = row.getStringValue(actionService.getConfigName());
          if (thingName != null && !thingName.isEmpty()) {
            Thing thing = ThingUtility.findThing(thingName);
            EntityReferenceCollection implementedThingShapes = thing.getAllImplementedThingShapes();
            if (implementedThingShapes.containsMember(actionService.getThingShapeName(), RelationshipTypes.ThingworxRelationshipTypes.ThingShape))
              things.add(thing); 
          } 
        } 
      }  
    return things;
  }
  
  private void dispatchChange(Thing thing, Map<ActionType, Map<String, InfoTable>> map, ActionService actionService) throws Exception {
    if (thing != null)
      for (Map.Entry<ActionType, Map<String, InfoTable>> entry : map.entrySet()) {
        String serviceName = actionService.getServiceName(entry.getKey());
        if (thing.hasServiceDefinition(serviceName)) {
          ValueCollection params = buildParams(entry.getKey(), entry.getValue());
          if (actionService.isAsync()) {
            ServiceDefinition serviceDefinition = thing.getInstanceServiceDefinition(serviceName);
            if (!serviceDefinition.isAsync()) {
              _logger.error("Service:" + thing.getName() + "." + serviceName + " need to be an asynchronous service.");
              continue;
            } 
          } 
          thing.processAPIServiceRequest(serviceName, params);
        } 
      }  
  }
  
  private Map<ActionType, Map<String, InfoTable>> getEventMap(List<DataChange> dataChanges) throws Exception {
    Map<ActionType, Map<String, InfoTable>> map = Maps.newHashMap();
    for (DataChange dataChange : dataChanges) {
      Map<String, InfoTable> infoTableMap = map.computeIfAbsent(dataChange.getActionType(), k -> Maps.newHashMap());
      InfoTable infoTable = infoTableMap.get(dataChange.getDataShapeName());
      if (infoTable == null) {
        infoTable = this.infoTableUtils.getInfoTable(this.dataShapeUtils.getDataShape(dataChange.getDataShapeName()));
        infoTable.addField(new FieldDefinition("__Fields", "Field to pass additional information on any field as JSON", BaseTypes.JSON));
        infoTable.addField(new FieldDefinition("__TimeStamp", "Timestamp of when the action occured", BaseTypes.DATETIME));
        infoTableMap.put(dataChange.getDataShapeName(), infoTable);
      } 
      infoTable.addRow(dataChange.getValueCollection());
    } 
    return map;
  }
  
  private ValueCollection buildParams(ActionType actionType, Map<String, InfoTable> infoTableMap) throws Exception {
    ValueCollection params = new ValueCollection();
    DataShape dataChangeDataShape = this.dataShapeUtils.getDataShape("PTC.DBConnection.DataChange");
    params.SetValue("DataChanges", getInfoTable(dataChangeDataShape, actionType, infoTableMap), BaseTypes.INFOTABLE);
    return params;
  }
  
  private InfoTable getInfoTable(DataShape dataShape, ActionType actionType, Map<String, InfoTable> infoTableMap) throws Exception {
    FieldDefinitionCollection fieldDefinitionCollection = dataShape.getFields().clone();
    InfoTable infoTable = InfoTableInstanceFactory.createInfoTableFromParameters(fieldDefinitionCollection);
    for (Map.Entry<String, InfoTable> entry : infoTableMap.entrySet()) {
      ValueCollection row = new ValueCollection();
      row.SetValue(dataShape.getFieldDefinition("ActionType"), actionType.getValueString());
      row.SetValue(dataShape.getFieldDefinition("DataShapeName"), entry.getKey());
      if (entry.getValue() != null)
        row.SetValue(dataShape.getFieldDefinition("Values"), entry.getValue()); 
      infoTable.addRow(row);
    } 
    return infoTable;
  }
  
  enum ActionService {
    PRE("Pre", "Action", false, "PTC.DBConnection.PreActionValidator_TS", "PreActionValidator"),
    ON("On", "Action", false, "PTC.DBConnection.OnActionProcessor_TS", "OnActionProcessor"),
    POST("Post", "Action", true, "PTC.DBConnection.PostActionHandler_TS", "PostActionHandler");
    
    private String prefix;
    
    private String postfix;
    
    private boolean async;
    
    private String thingShapeName;
    
    private String configName;
    
    ActionService(String prefix, String postfix, boolean async, String thingName, String configName) {
      this.prefix = prefix;
      this.postfix = postfix;
      this.async = async;
      this.thingShapeName = thingName;
      this.configName = configName;
    }
    
    String getPrefix() {
      return this.prefix;
    }
    
    String getPostfix() {
      return this.postfix;
    }
    
    boolean isAsync() {
      return this.async;
    }
    
    String getThingShapeName() {
      return this.thingShapeName;
    }
    
    String getServiceName(ActionType actionType) {
      return getPrefix() + getPrefix() + actionType.getValueString();
    }
    
    String getConfigName() {
      return this.configName;
    }
  }
}
