package twx.core.db;

import java.util.ArrayList;
import java.util.List;

public class SqlServerJdbcQuery extends JdbcQuery {
  public String buildPagination() {
    StringBuilder sql = new StringBuilder(64);
    if (this.orderBy.isEmpty())
      sql.append(" ORDER BY ").append(" (SELECT NULL)"); 
    if (this.offset > -1)
      sql.append(" OFFSET ? ROWS"); 
    if (this.limit > 0)
      sql.append(" FETCH NEXT ? ROWS ONLY"); 
    return sql.toString();
  }
  
  public List<Object> getParameters() {
    List<Object> params = new ArrayList(this.parameters);
    if (this.offset > -1)
      params.add(Integer.valueOf(this.offset)); 
    if (this.limit > 0)
      params.add(Integer.valueOf(this.limit)); 
    return params;
  }
  
  public String getCaseInSensitivePattern(SqlOperator operator) {
    if (SqlOperator.LIKE.equals(operator))
      return "LOWER(%1s) LIKE LOWER(?)"; 
    if (SqlOperator.NOTLIKE.equals(operator))
      return "LOWER(%1$s) NOT LIKE LOWER(?)"; 
    return operator.getCaseInSensitivePattern();
  }
}
