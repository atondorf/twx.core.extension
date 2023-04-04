package twx.core.db.config;

import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinition;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinitions;
import com.thingworx.metadata.annotations.ThingworxDataShapeDefinition;
import com.thingworx.metadata.annotations.ThingworxFieldDefinition;
import com.thingworx.things.Thing;

@ThingworxConfigurationTableDefinitions(tables = {@ThingworxConfigurationTableDefinition(name = "ActionConfigurationSettings", description = "Action configuration settings", isMultiRow = true, ordinal = 0, dataShape = @ThingworxDataShapeDefinition(fields = {@ThingworxFieldDefinition(name = "DataShapeName", description = "The data shape name.", baseType = "DATASHAPENAME", ordinal = 0), @ThingworxFieldDefinition(name = "Action", description = "The action to be executed: Create, Update, or Delete.", baseType = "STRING", ordinal = 1), @ThingworxFieldDefinition(name = "ThingName", description = "The thing where the service to be executed resides.", baseType = "THINGNAME", ordinal = 2), @ThingworxFieldDefinition(name = "ServiceName", description = "The name of the service to be executed.", baseType = "STRING", ordinal = 3)}))})
public class ActionConfiguration extends Thing {
  static final String ACTION_CONFIGURATION_SETTINGS = "ActionConfigurationSettings";
  
  static final String DATA_SHAPE_NAME = "DataShapeName";
  
  static final String ACTION = "Action";
  
  static final String THING_NAME = "ThingName";
  
  static final String SERVICE_NAME = "ServiceName";
}
