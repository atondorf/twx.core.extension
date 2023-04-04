package twx.core.db;

import java.util.List;

public class Index {
  private String name;
  
  private boolean unique = false;
  
  private String identifier;
  
  private List<String> fieldNames;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public boolean isUnique() {
    return this.unique;
  }
  
  public void setUnique(boolean unique) {
    this.unique = unique;
  }
  
  public String getIdentifier() {
    return this.identifier;
  }
  
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  
  public List<String> getFieldNames() {
    return this.fieldNames;
  }
  
  public void setFieldNames(List<String> fieldNames) {
    this.fieldNames = fieldNames;
  }
}
