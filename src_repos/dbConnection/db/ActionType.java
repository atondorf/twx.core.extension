package twx.core.db;

public enum ActionType {
  CREATE(1, "Create"),
  UPDATE(2, "Update"),
  DELETE(3, "Delete");
  
  private String valueString;
  
  private int value;
  
  ActionType(int value, String valueString) {
    this.value = value;
    this.valueString = valueString;
  }
  
  int getValue() {
    return this.value;
  }
  
  public String getValueString() {
    return this.valueString;
  }
}
