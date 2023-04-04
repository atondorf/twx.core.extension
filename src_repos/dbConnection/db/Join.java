package twx.core.db;

import com.thingworx.types.BaseTypes;
import java.util.List;


import org.apache.commons.lang3.Validate;

class Join {
  
  private JoinType joinType;
  

  private String tableName;
  
  
  private String sourcePath;
  
  
  private String targetPath;
  
  
  private BaseTypes targetBaseType;
  
  
  private String value;
  
  
  private String alias;
  
  
  private List<JoinCondition> conditions;
  
  Join( String tableName,  String alias,  String sourcePath,  String targetPath, BaseTypes targetBaseType,  String value,  JoinType joinType,  List<JoinCondition> conditions) {
    Validate.notNull(tableName);
    Validate.notBlank(tableName);
    Validate.notEmpty(tableName);
    this.tableName = tableName;
    this.sourcePath = sourcePath;
    this.targetPath = targetPath;
    this.targetBaseType = targetBaseType;
    this.value = value;
    this.joinType = joinType;
    this.alias = alias;
    this.conditions = conditions;
  }
  
  String getQueryJoin() {
    StringBuilder join = new StringBuilder();
    if (this.joinType != null)
      join.append(' ').append(this.joinType.getValue()); 
    join.append(' ').append(this.tableName);
    if (this.alias != null && !this.alias.isEmpty())
      join.append(' ').append(this.alias); 
    String source = (this.sourcePath != null && !this.sourcePath.isEmpty()) ? this.sourcePath : ((this.targetBaseType != null && BaseTypes.isStringBaseType(this.targetBaseType)) ? ("'" + this.value + "'") : this.value);
    if (source != null && this.targetPath != null)
      join.append(" ON ").append(source).append(' ')
        .append(JoinConditionType.EQUAL.getSqlString()).append(' ').append(this.targetPath); 
    if (this.conditions != null && !this.conditions.isEmpty())
      for (JoinCondition joinCondition : this.conditions)
        join.append("  AND  ").append(joinCondition.getQueryJoinCondition());  
    return join.toString();
  }
}
