package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.common.utils.JSONUtilities;
import com.thingworx.common.utils.SerializationOption;
import com.thingworx.common.utils.SerializationOptions;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.ServiceDefinition;
import com.thingworx.things.Thing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.InfoTablePrimitive;
import org.json.JSONObject;

class ExecuteServiceHandler implements ExecuteHandler<InfoTable> {
  private String thingName;
  
  private String serviceName;
  
  private JSONObject jsonObject;
  
  ExecuteServiceHandler(String thingName, String serviceName, JSONObject jsonObject) {
    this.thingName = thingName;
    this.serviceName = serviceName;
    this.jsonObject = jsonObject;
  }
  
  public InfoTable execute() throws Exception {
    Thing thing = ThingUtility.findThing(this.thingName);
    ServiceDefinition serviceDefinition = thing.getInstanceServiceDefinition(this.serviceName);
    ValueCollection params = getParams(this.jsonObject, serviceDefinition);
    return thing.processAPIServiceRequest(this.serviceName, params);
  }
  
  private ValueCollection getParams(JSONObject jsonObject, ServiceDefinition serviceDefinition) {
    ValueCollection values = new ValueCollection();
    try {
      if (serviceDefinition == null)
        return values; 
      for (FieldDefinition field : serviceDefinition.getParameters().values()) {
        if (jsonObject.opt(field.getName()) != null) {
          if (field.isQueryParameter() && BaseTypes.INFOTABLE.equals(field.getBaseType())) {
            Object data = jsonObject.get(field.getName());
            SerializationOptions options = new SerializationOptions(new SerializationOption[] { (SerializationOption)SerializationOptions.ImportOption.IGNORE_DEFAULT_VALUES });
            if (data instanceof String) {
              values.put(field.getName(), new InfoTablePrimitive(
                    InfoTable.fromJSON(JSONUtilities.readJSON((String)data), options)));
              continue;
            } 
            values.put(field.getName(), new InfoTablePrimitive(
                  InfoTable.fromJSON((JSONObject)data, options)));
            continue;
          } 
          values.put(field.getName(), 
              BaseTypes.ConvertToPrimitive(jsonObject.get(field.getName()), field.getBaseType()));
          continue;
        } 
        if (field.getAspects().getDefaultValueAspect() != null)
          values.put(field.getName(), field.getAspects().getDefaultValueAspect()); 
      } 
    } catch (Exception ex) {
      throw new ThingworxRuntimeException("Unable To Parse JSON", ex);
    } 
    return values;
  }
}
