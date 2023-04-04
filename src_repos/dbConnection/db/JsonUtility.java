package twx.core.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.json.JSONObject;

public class JsonUtility {
  protected <T> T getObjetFromJson(JSONObject json, Class<T> objectClass) throws IOException {
    if (json == null)
      return null; 
    ObjectMapper objectMapper = new ObjectMapper();
    return (T)objectMapper.readValue(json.toString(), objectClass);
  }
}
