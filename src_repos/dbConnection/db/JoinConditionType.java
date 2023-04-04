package twx.core.db;

public enum JoinConditionType {
  EQUAL("EQUAL", "=");
  
  private String sqlString;
  
  private String joinConditionType;
  
  JoinConditionType(String joinConditionType, String sqlString) {
    this.joinConditionType = joinConditionType;
    this.sqlString = sqlString;
  }
  
  public String getJoinConditionType() {
    return this.joinConditionType;
  }
  
  public String getSqlString() {
    return this.sqlString;
  }
}
