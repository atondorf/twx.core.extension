package twx.core.db;

public enum JoinType {
  INNER("INNER JOIN"),
  LEFT("LEFT OUTER JOIN"),
  RIGHT("RIGHT OUTER JOIN"),
  FULL("FULL OUTER JOIN");
  
  private String value;
  
  JoinType(String value) {
    this.value = value;
  }
  
  protected String getValue() {
    return this.value;
  }
}
