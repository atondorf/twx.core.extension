package twx.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class JdbcQuery {
  public static final String STAR = "*";
  
  public static final String COUNT = "COUNT(*)";
  
  protected static final String SELECT = "SELECT ";
  
  protected static final String FROM = " FROM ";
  
  protected static final String PARAM = "?";
  
  protected static final String PARAM_QUOTED = "'?'";
  
  protected static final String WHERE = " WHERE ";
  
  protected static final String ORDER_BY = " ORDER BY ";
  
  protected static final String GROUP_BY = " GROUP BY ";
  
  protected static final String AND = " AND ";
  
  protected static final String OR = " OR ";
  
  protected static final String COMMA = ", ";
  
  protected static final String DESC = "DESC";
  
  protected static final String ASC = "ASC";
  
  protected static final String INNER_JOIN = "INNER JOIN";
  
  protected static final String LEFT_OUTER_JOIN = "LEFT OUTER JOIN";
  
  protected static final String RIGHT_OUTER_JOIN = "RIGHT OUTER JOIN";
  
  protected static final String FULL_OUTER_JOIN = "FULL OUTER JOIN";
  
  protected static final String ON = "ON";
  
  protected final List<String> select = new ArrayList<>();
  
  protected final StringBuilder from = new StringBuilder();
  
  protected final StringBuilder where = new StringBuilder();
  
  protected final List<Order> orderBy = new ArrayList<>();
  
  protected final List<String> groupBy = new ArrayList<>();
  
  protected final List<Object> parameters = new ArrayList();
  
  protected int limit;
  
  protected int offset;
  
  public String build() {
    validate();
    StringBuilder sql = new StringBuilder(64);
    sql.append("SELECT ");
    if (!this.select.isEmpty())
      for (int i = 0; i < this.select.size(); i++) {
        sql.append(this.select.get(i));
        if (i + 1 < this.select.size())
          sql.append(", "); 
      }  
    sql.append(" FROM ").append(this.from);
    if (this.where.length() > 0)
      sql.append(" WHERE ").append(this.where); 
    if (!this.groupBy.isEmpty()) {
      StringBuilder groupByClause = new StringBuilder();
      for (String path : this.groupBy) {
        if (groupByClause.length() > 0)
          groupByClause.append(", "); 
        groupByClause.append(path);
      } 
      sql.append(" GROUP BY ").append(groupByClause);
    } 
    if (!this.orderBy.isEmpty()) {
      StringBuilder orderClause = new StringBuilder();
      for (Order entry : this.orderBy) {
        if (orderClause.length() > 0)
          orderClause.append(", "); 
        orderClause.append(entry.getPath()).append(' ').append(entry.isAscending() ? "ASC" : "DESC");
      } 
      sql.append(" ORDER BY ").append(orderClause);
    } 
    sql.append(buildPagination());
    return sql.toString();
  }
  
  protected abstract String buildPagination();
  
  public JdbcQuery select(String... columns) {
    this.select.clear();
    if (columns == null || columns.length == 0 || (columns.length == 1 && columns[0] == null)) {
      this.select.add("*");
    } else {
      this.select.addAll(Arrays.asList(columns));
    } 
    return this;
  }
  
  public JdbcQuery select(Select selectObject) {
    this.select.clear();
    if (selectObject == null || selectObject.getSelectElements().size() == 0) {
      this.select.add("*");
    } else {
      this.select.addAll(selectObject.getSelectList());
    } 
    return this;
  }
  
  public JdbcQuery count() {
    return select(new String[] { "COUNT(*)" });
  }
  
  public JdbcQuery where(Criteria where) {
    this.where.setLength(0);
    this.where.trimToSize();
    this.where.append(where.buffer);
    this.parameters.clear();
    this.parameters.addAll(where.params);
    return this;
  }
  
  public JdbcQuery and(Criteria and) {
    Criteria criteriaFragment = new Criteria();
    criteriaFragment.buffer = this.where;
    criteriaFragment.params = this.parameters;
    criteriaFragment.append(" AND ", and);
    return this;
  }
  
  public JdbcQuery or(Criteria or) {
    Criteria criteriaFragment = new Criteria();
    criteriaFragment.buffer = this.where;
    criteriaFragment.params = this.parameters;
    criteriaFragment.append(" OR ", or);
    return this;
  }
  
  public JdbcQuery from(String from) {
    this.from.append(from);
    return this;
  }
  
  public JdbcQuery join(Join join) {
    this.from.append(join.getQueryJoin());
    return this;
  }
  
  public JdbcQuery orderBy(String path, boolean isAscending) {
    this.orderBy.add(new Order(path, isAscending));
    return this;
  }
  
  public JdbcQuery groupBy(String path) {
    this.groupBy.add(path);
    return this;
  }
  
  public JdbcQuery limit(int limit) {
    this.limit = limit;
    return this;
  }
  
  public JdbcQuery offset(int offset) {
    this.offset = offset;
    return this;
  }
  
  public abstract List<Object> getParameters();
  
  public List<Order> getOrderBy() {
    return this.orderBy;
  }
  
  public List<String> getGroupBy() {
    return this.groupBy;
  }
  
  public PreparedStatement prepareStatement(Connection connection) throws SQLException {
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(build());
      int parameterCount = statement.getParameterMetaData().getParameterCount();
      List<Object> params = getParameters();
      for (int i = 0; i < parameterCount; i++)
        statement.setObject(i + 1, params.get(i)); 
    } catch (SQLException e) {
      if (statement != null)
        statement.close(); 
      throw e;
    } 
    return statement;
  }
  
  protected void validate() {
    assert this.select.size() > 0;
    assert this.from.length() > 0;
    int count = 0;
    int idx = 0;
    while ((idx = this.where.indexOf("?", idx)) != -1) {
      count++;
      idx += "?".length();
    } 
    if (count != this.parameters.size())
      throw new IllegalStateException("Query defines " + count + " parameters but " + this.parameters
          .size() + " were found."); 
  }
  
  public Criteria between(String column, Object from, Object to) {
    return createCriteria(SqlOperator.BETWEEN, column, false, new Object[] { from, to });
  }
  
  public Criteria notBetween(String column, Object from, Object to) {
    return createCriteria(SqlOperator.NOTBETWEEN, column, false, new Object[] { from, to });
  }
  
  public Criteria eq(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.EQ, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria like(String column, boolean isCaseSensitive, String value) {
    return createCriteria(SqlOperator.LIKE, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria notLike(String column, boolean isCaseSensitive, String value) {
    return createCriteria(SqlOperator.NOTLIKE, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria matches(String column, boolean isCaseSensitive, String value) {
    return createCriteria(SqlOperator.MATCHES, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria notMatches(String column, boolean isCaseSensitive, String value) {
    return createCriteria(SqlOperator.NOTMATCHES, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria ne(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.NE, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria gt(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.GT, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria ge(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.GE, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria lt(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.LT, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria le(String column, boolean isCaseSensitive, Object value) {
    return createCriteria(SqlOperator.LE, column, isCaseSensitive, new Object[] { value });
  }
  
  public Criteria in(String column, List<Object> values) {
    return createCriteria(SqlOperator.IN, column, true, new Object[] { values });
  }
  
  public Criteria isNotNull(String column) {
    return createCriteria(SqlOperator.NOT_NULL, column, true, new Object[0]);
  }
  
  public Criteria isNull(String column) {
    return createCriteria(SqlOperator.NULL, column, true, new Object[0]);
  }
  
  public Criteria notIn(String column, List<Object> values) {
    return createCriteria(SqlOperator.NOT_IN, column, true, new Object[] { values });
  }
  
  protected Criteria createCriteria(SqlOperator operator, String column, boolean isCaseSensitive, Object... value) {
    Criteria fragment = new Criteria();
    operator.append(this, fragment, column, isCaseSensitive, value);
    return fragment;
  }
  
  public String toString() {
    String sql = build();
    for (Object parameter : getParameters())
      sql = sql.replaceFirst("\\?", parameter.toString()); 
    return sql;
  }
  
  public Criteria getCriteria() {
    return new Criteria();
  }
  
  public static class Criteria {
    StringBuilder buffer = new StringBuilder();
    
    List<Object> params = new ArrayList();
    
    public Criteria() {}
    
    protected Criteria(String sql, Object... params) {
      this.buffer.append(sql);
      this.params.addAll(Arrays.asList(params));
    }
    
    void append(String conjunction, Criteria fragment) {
      if (this.buffer.length() > 1)
        this.buffer.append(conjunction); 
      this.buffer.append(fragment.buffer);
      this.params.addAll(fragment.params);
    }
    
    void append(String conjunction, List<Criteria> fragments) {
      if (fragments.size() == 1) {
        append(conjunction, fragments.get(0));
        return;
      } 
      if (this.buffer.length() > 1)
        this.buffer.append(conjunction); 
      int index = 0;
      this.buffer.append('(');
      for (Criteria fragment : fragments) {
        this.buffer.append(fragment.buffer);
        index++;
        if (index < fragments.size())
          this.buffer.append(' ').append(conjunction).append(' '); 
        this.params.addAll(fragment.params);
      } 
      this.buffer.append(')');
    }
  }
  
  public static class Order {
    private final String path;
    
    private final boolean ascending;
    
    public Order(String path) {
      this(path, true);
    }
    
    public Order(String path, boolean ascending) {
      this.path = path;
      this.ascending = ascending;
    }
    
    public String getPath() {
      return this.path;
    }
    
    public boolean isAscending() {
      return this.ascending;
    }
  }
  
  public String getCaseInSensitivePattern(SqlOperator operator) {
    return operator.getCaseInSensitivePattern();
  }
}
