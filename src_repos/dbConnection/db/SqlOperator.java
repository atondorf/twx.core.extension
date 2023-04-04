package twx.core.db;

import java.util.Formatter;
import java.util.List;

public enum SqlOperator {
  EQ("%1s = ?", "LOWER(%1s) = LOWER(?)", 1) {
    public void append(JdbcQuery query, JdbcQuery.Criteria fragment, String path, boolean isCaseSensitive, Object... args) {
      if (args == null || args.length == 0 || args[0] == null || args[0].equals("")) {
        SqlOperator.NULL.append(query, fragment, path, isCaseSensitive, args);
      } else {
        super.append(query, fragment, path, isCaseSensitive, args);
      } 
    }
  },
  LT("%1$s < ?", "%1$s < ?", 1),
  LE("%1$s <= ?", "%1$s <= ?", 1),
  GT("%1$s > ?", "%1$s > ?", 1),
  GE("%1$s >= ?", "%1$s >= ?", 1),
  NE("%1$s <> ?","%1$s <> ?", 1),
  MATCHES("%1$s ~ ?", "%1$s ~* ?", 1),
  NOTMATCHES("%1$s !~ ?", "%1$s !~* ?", 1),
  BETWEEN("(%1$s >= ? AND %1$s <= ?)", null, 2),
  NOTBETWEEN("(%1$s < ? OR %1$s > ?)", null, 2),
  NULL("%1$s IS NULL", null, 0),
  NOT_NULL("%1$s IS NOT NULL", null, 0),
  IN("%1$s IN(?%2$s)", "%1$s IN(?%2$s)", 0) {
    public void append(JdbcQuery query, JdbcQuery.Criteria fragment, String path, boolean isCaseSensitive, Object... args) {
      append(query, fragment, path, isCaseSensitive, (List<Object>)args[0]);
    }
  },
  NOT_IN("%1$s NOT IN(?%2$s)", "%1$s NOT IN(?%2$s)", 0) {
    public void append(JdbcQuery query, JdbcQuery.Criteria fragment, String path, boolean isCaseSensitive, Object... args) {
      append(query, fragment, path, isCaseSensitive, (List<Object>)args[0]);
    }
  },
  LIKE("%1$s LIKE ?", "%1$s ILIKE ?", 1),
  NOTLIKE("%1$s NOT LIKE ?", "%1$s NOT ILIKE ?", 1);
  
  private String pattern;
  
  private String caseInSensitivePattern;
  
  private int requiredArgCount;
  
  protected JdbcQuery jdbcQuery;
  
  public String getPattern() {
    return this.pattern;
  }
  
  public String getCaseInSensitivePattern() {
    return this.caseInSensitivePattern;
  }
  
  public String getCaseInSensitivePattern(JdbcQuery jdbcQuery) {
    return (jdbcQuery != null) ? jdbcQuery.getCaseInSensitivePattern(this) : 
      getCaseInSensitivePattern();
  }
  
  public int getRequiredArgCount() {
    return this.requiredArgCount;
  }
  
  SqlOperator(String caseSensitivePattern, String caseInSensitivePattern, int requiredArgCount) {
    this.pattern = caseSensitivePattern;
    this.caseInSensitivePattern = caseInSensitivePattern;
    this.requiredArgCount = requiredArgCount;
  }
  
  public void append(JdbcQuery query, JdbcQuery.Criteria fragment, String path, boolean isCaseSensitive, Object... params) {
    validateParams(params);
    if (!isCaseSensitive && getCaseInSensitivePattern(query) != null && params[0] instanceof String) {
      (new Formatter(fragment.buffer)).format(getCaseInSensitivePattern(query), new Object[] { path });
    } else {
      (new Formatter(fragment.buffer)).format(getPattern(), new Object[] { path });
    } 
    for (int i = 0; i < getRequiredArgCount(); i++)
      fragment.params.add(params[i]); 
  }
  
  private void append(JdbcQuery query, JdbcQuery.Criteria fragment, String path, boolean isCaseSensitive, List<Object> values) {
    if (values.isEmpty())
      return; 
    if (values.size() == 1) {
      switch (this) {
        case IN:
          EQ.append(query, fragment, path, isCaseSensitive, new Object[] { values.get(0) });
          return;
        case NOT_IN:
          NE.append(query, fragment, path, isCaseSensitive, new Object[] { values.get(0) });
          return;
      } 
      throw new UnsupportedOperationException(name() + " not supported by Set handler.");
    } 
    String paramPlaces = (new String(new char[values.size() - 1])).replace("\000", ",?");
    (new Formatter(fragment.buffer)).format(this.pattern, new Object[] { path, paramPlaces });
    fragment.params.addAll(values);
  }
  
  private void validateParams(Object... args) {
    if (getRequiredArgCount() > 0) {
      if (args == null || args.length != getRequiredArgCount())
        throw new IllegalArgumentException(
            name() + ": Invalid Parameter Count: requires [" + name() + "]"); 
      for (int i = 0; i < args.length; i++) {
        if (args[i] == null)
          throw new IllegalArgumentException(name() + ": Null Parameter at index: " + name()); 
      } 
    } 
  }
}
