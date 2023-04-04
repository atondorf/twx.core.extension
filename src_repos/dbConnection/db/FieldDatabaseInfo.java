package twx.core.db;

public class FieldDatabaseInfo {
  private String name;
  
  private int length;
  
  private boolean unique;
  
  private boolean notNull;
  
  private String baseType;
  
  private String defaultValue;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getLength() {
    return this.length;
  }
  
  public void setLength(int length) {
    this.length = length;
  }
  
  public boolean isUnique() {
    return this.unique;
  }
  
  public void setUnique(boolean unique) {
    this.unique = unique;
  }
  
  public boolean isNotNull() {
    return this.notNull;
  }
  
  public void setNotNull(boolean notNull) {
    this.notNull = notNull;
  }
  
  public String getBaseType() {
    return this.baseType;
  }
  
  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }
  
  public String getDefaultValue() {
    return this.defaultValue;
  }
  
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
