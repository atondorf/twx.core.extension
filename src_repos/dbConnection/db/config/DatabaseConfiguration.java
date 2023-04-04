package twx.core.db.config;

import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinition;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinitions;
import com.thingworx.metadata.annotations.ThingworxDataShapeDefinition;
import com.thingworx.metadata.annotations.ThingworxFieldDefinition;
import com.thingworx.things.Thing;

@ThingworxConfigurationTableDefinitions(tables = {@ThingworxConfigurationTableDefinition(name = "DatabaseConfigurationSettings", description = "Database configuration settings", ordinal = 1, dataShape = @ThingworxDataShapeDefinition(fields = {@ThingworxFieldDefinition(name = "DBConnection", description = "DatabaseThing to determine database being used.", baseType = "THINGNAME", ordinal = 3)}))})
public class DatabaseConfiguration extends Thing {
  static final String DATABASE_CONFIGURATION_SETTINGS = "DatabaseConfigurationSettings";
  
  static final String DATABASE_THING_NAME = "DBConnection";
}
