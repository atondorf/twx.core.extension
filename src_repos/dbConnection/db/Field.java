package twx.core.db;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Field {
  private String name;
  
  private boolean isNull;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @JsonGetter("isNull")
  public boolean isNull() {
    return this.isNull;
  }
  
  public void setNull(boolean isNull) {
    this.isNull = isNull;
  }
}
