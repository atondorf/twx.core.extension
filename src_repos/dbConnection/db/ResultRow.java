package twx.core.db;

import com.google.common.collect.Maps;
import java.util.Map;

public class ResultRow {
  private Object key;
  
  private Map<String, Object> values = Maps.newHashMap();
  
  ResultRow(Object key) {
    this.key = key;
  }
  
  public Object getKey() {
    return this.key;
  }
  
  protected void setValue(String key, Object value) {
    this.values.put(key, value);
  }
  
  protected void setValues(Map<String, Object> values) {
    this.values.putAll(values);
  }
  
  protected Map<String, Object> getValues() {
    return this.values;
  }
  
  protected void merge(ResultRow resultRow) {
    if (getKey() == null || !getKey().equals(resultRow.getKey()))
      return; 
    for (Map.Entry<String, Object> entry : resultRow.getValues().entrySet()) {
      if (this.values.containsKey(entry.getKey()))
        continue; 
      setValue(entry.getKey(), entry.getValue());
    } 
  }
}
