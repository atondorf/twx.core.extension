package twx.core.db;

import com.thingworx.types.BaseTypes;



public class JoinCondition {
  
  private String sourcePath;
  

  private String targetPath;
  

  private BaseTypes targetBaseType;
  
  
  private String value;
  

  private JoinConditionType joinConditionType;
  
  JoinCondition( String sourcePath,  String targetPath,  BaseTypes targetBaseType,  String value,  JoinConditionType joinConditionType) {
    this.sourcePath = sourcePath;
    this.targetPath = targetPath;
    this.targetBaseType = targetBaseType;
    this.value = value;
    this.joinConditionType = joinConditionType;
  }
  
  String getQueryJoinCondition() {
    StringBuilder joinCondition = new StringBuilder();
    String source = (this.sourcePath != null && !this.sourcePath.isEmpty()) ? this.sourcePath : (BaseTypes.isStringBaseType(this.targetBaseType) ? ("'" + this.value + "'") : this.value);
    joinCondition.append(source).append(' ').append(this.joinConditionType.getSqlString()).append(' ')
      .append(this.targetPath);
    return joinCondition.toString();
  }
}
