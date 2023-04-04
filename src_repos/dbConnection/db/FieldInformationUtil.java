package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.types.collections.ValueCollection;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

public class FieldInformationUtil {
  protected static final String FIELDS = "__Fields";
  
  private JsonUtility jsonUtility = new JsonUtility();
  
  private Optional<FieldInformation> getFieldInformation(JSONObject jsonObject) {
    try {
      return Optional.ofNullable(this.jsonUtility.<FieldInformation>getObjetFromJson(jsonObject, FieldInformation.class));
    } catch (IOException e) {
      throw new RuntimeException("Error parsing json", e);
    } 
  }
  
  private Optional<FieldInformation> getFieldInformation(ValueCollection valueCollection) {
    if (valueCollection != null && valueCollection.containsKey("__Fields")) {
      Object value = valueCollection.getValue("__Fields");
      if (value instanceof JSONObject)
        return getFieldInformation((JSONObject)value); 
    } 
    return Optional.empty();
  }
  

  private List<String> getNullFieldNames( FieldInformation fieldInformation) {
    List<String> nullFieldNames = Lists.newArrayList();
    if (fieldInformation.getFields() != null)
      for (Field field : fieldInformation.getFields()) {
        if (field.isNull() && !nullFieldNames.contains(field.getName()))
          nullFieldNames.add(field.getName()); 
      }  
    return nullFieldNames;
  }
  

  protected List<String> getNullFieldNames(ValueCollection valueCollection) {
    Optional<FieldInformation> found = getFieldInformation(valueCollection);
    return found.<List<String>>map(this::getNullFieldNames).orElseGet(Lists::newArrayList);
  }
}
