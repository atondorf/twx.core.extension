package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;


public class ThingUtility {

  public static Thing findThing(String thingName) {
    Thing thing = (Thing)EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
    if (thing != null) {
      if (thing.isVisible())
        return thing; 
      throw new ThingworxRuntimeException("Thing:" + thing
          .getName() + " is not visible for current user.");
    } 
    throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
  }
  

  public static Thing findThingDirect(String thingName) {
    Thing thing = (Thing)EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
    if (thing != null)
      return thing; 
    throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
  }
}
