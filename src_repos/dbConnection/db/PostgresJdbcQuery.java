package twx.core.db;

import java.util.ArrayList;
import java.util.List;

public class PostgresJdbcQuery extends JdbcQuery {
  public final String buildPagination() {
    StringBuilder sql = new StringBuilder(32);
    if (this.limit > 0)
      sql.append(" LIMIT ?"); 
    if (this.offset > 0)
      sql.append(" OFFSET ?"); 
    return sql.toString();
  }
  
  public List<Object> getParameters() {
    List<Object> params = new ArrayList(this.parameters);
    if (this.limit > 0)
      params.add(Integer.valueOf(this.limit)); 
    if (this.offset > 0)
      params.add(Integer.valueOf(this.offset)); 
    return params;
  }
}
