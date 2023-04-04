package twx.core.db;

class ColumnInfo {
  private String name;
  
  private String type;
  
  private boolean notNull;
  
  private int length;
  
  ColumnInfo(String name, String type, boolean notNull, int length) {
    this.name = name;
    this.type = type;
    this.notNull = notNull;
    this.length = length;
  }
  
  String getName() {
    return this.name;
  }
  
  String getType() {
    return this.type;
  }
  
  public boolean isNotNull() {
    return this.notNull;
  }
  
  public int getLength() {
    return this.length;
  }
}
