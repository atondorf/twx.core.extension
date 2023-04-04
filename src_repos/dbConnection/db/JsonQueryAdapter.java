package twx.core.db;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.TagCollection;
import com.thingworx.types.TagLink;
import com.thingworx.types.primitives.TagCollectionPrimitive;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonQueryAdapter {
  static final String TYPE = "type";
  
  private static final String DATA_SHAPE_NAME = "dataShapeName";
  
  private static final String FIELD_NAME = "fieldName";
  
  private static final String VALUE = "value";
  
  private static final String VALUES = "values";
  
  private static final String EXPRESSION = "expression";
  
  private static final String FROM = "from";
  
  private static final String TO = "to";
  
  private static final String TAGS = "tags";
  
  private static final String JOINS = "joins";
  
  private static final String JOIN_CONDITIONS = "joinConditions";
  
  static final String SELECT = "select";
  
  private static final String ALIAS = "alias";
  
  static final String LOCALE = "locale";
  
  static final String SOURCE_DATA_SHAPE_NAME = "sourceDataShapeName";
  
  static final String SOURCE_FIELD_NAME = "sourceFieldName";
  
  static final String SOURCE_ALIAS = "sourceAlias";
  
  static final String TARGET_DATA_SHAPE_NAME = "targetDataShapeName";
  
  static final String TARGET_FIELD_NAME = "targetFieldName";
  
  static final String TARGET_ALIAS = "targetAlias";
  
  private static final String FILTERS = "filters";
  
  private static final String SORTS = "sorts";
  
  private static final String GROUP_BY = "groupBy";
  
  private static final String ALIASES = "aliases";
  
  private static final String OR = "OR";
  
  private static final String AND = "AND";
  
  private static final String ASCENDING = "isAscending";
  
  private static final String NOTMISSINGVALUE = "NOTMISSINGVALUE";
  
  private static final String MISSINGVALUE = "MISSINGVALUE";
  
  private static final String NOTMATCHES = "NOTMATCHES";
  
  private static final String MATCHES = "MATCHES";
  
  private static final String NOTLIKE = "NOTLIKE";
  
  private static final String LIKE = "LIKE";
  
  private static final String IN = "IN";
  
  private static final String NOTIN = "NOTIN";
  
  private static final String NOTTAGGED = "NOTTAGGED";
  
  private static final String TAGGED = "TAGGED";
  
  private static final String NOTNEAR = "NOTNEAR";
  
  private static final String NEAR = "NEAR";
  
  private static final String BETWEEN = "BETWEEN";
  
  private static final String NOTBETWEEN = "NOTBETWEEN";
  
  private static final String NE = "NE";
  
  private static final String GE = "GE";
  
  private static final String GT = "GT";
  
  private static final String EQ = "EQ";
  
  private static final String LT = "LT";
  
  private static final String LE = "LE";
  
  private static final String AP_ALIAS_POSTFIX = "_ap";
  
  private final DataShape dataShape;
  
  private final String select;
  
  private final JSONObject jsonQuery;
  
  private final Map<String, String> fieldMap;
  
  private final JSONObject aliases;
  
  private Select jsonSelect;
  
  private JdbcQuery query;
  
  private Map<String, Pair<DataShape, String>> aliasesMap;
  
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private DatabaseHandler databaseHandler;
  
  private Map<String, Map<String, Pair<String, BaseTypes>>> pathInfoMap = Maps.newHashMap();
  
  JsonQueryAdapter(DatabaseHandler databaseHandler, DataShape dataShape, String select, JSONObject query, Map<String, String> fieldMap) {
    this.databaseHandler = databaseHandler;
    this.dataShape = dataShape;
    this.select = select;
    this.jsonQuery = query;
    if (query != null) {
      this.aliases = query.optJSONObject("aliases");
    } else {
      this.aliases = null;
    } 
    this.fieldMap = fieldMap;
  }
  
  public JdbcQuery createQuery(JdbcQuery jdbcQuery) throws JSONException {
    if (this.query == null) {
      String localeString = (this.jsonQuery != null) ? this.jsonQuery.optString("locale") : null;
      if (localeString != null && !localeString.isEmpty()) {
        Locale locale = Locale.forLanguageTag(localeString);
        (new LocalizationUtils()).localizedFilter(this.jsonQuery, locale);
      } 
      this.aliasesMap = Maps.newLinkedHashMap();
      String alias = (this.jsonQuery != null) ? this.jsonQuery.optString("alias") : null;
      String dataShapeAlias = getAlias(this.dataShape, alias);
      this.aliasesMap.put(dataShapeAlias, Pair.of(this.dataShape, null));
      this.query = jdbcQuery;
      from(this.dataShape, dataShapeAlias);
      if (this.jsonQuery != null) {
        transformJoins(this.jsonQuery.optJSONArray("joins"));
        this.query.where(transformFilters(this.jsonQuery.optJSONObject("filters"), jdbcQuery));
        transformGroupBys(this.jsonQuery.optJSONArray("groupBy"));
        transformSorts(this.jsonQuery.optJSONArray("sorts"));
      } 
      select(this.dataShape, dataShapeAlias, this.aliasesMap, 
          (this.jsonQuery != null) ? this.jsonQuery.optJSONArray("select") : null);
    } 
    return this.query;
  }
  
  private void select(DataShape dataShape, String dataShapeAlias, Map<String, Pair<DataShape, String>> aliasesMap, JSONArray selects) {
    this
      .jsonSelect = new Select(selects, dataShape, dataShapeAlias, Maps.newLinkedHashMap(aliasesMap), this);
    if (!this.jsonSelect.getSelectElements().isEmpty()) {
      this.query.select(this.jsonSelect);
    } else {
      this.query = this.query.select(new String[] { this.select });
    } 
  }
  
  private void from(DataShape dataShape, String alias) {
    addJoinWithAp(dataShape, null, null, null, null, null, alias, null);
  }
  
  private void addJoinWithAp(DataShape dataShape, JoinType joinType, String sourcePath, String targetPath, BaseTypes targetBaseType, String value, String alias, List<JoinCondition> joinConditions) {
    this.query.join(getJoin(dataShape, alias, joinType, sourcePath, targetPath, targetBaseType, value, joinConditions));
    Optional<DataShape> dataShapeApFound = this.dataShapeUtils.getDataShapeAp(dataShape);
    if (dataShapeApFound.isPresent()) {
      DataShape dataShapeAp = dataShapeApFound.get();
      String aliasAp = getAliasAp(dataShapeAp, alias, "_ap");
      String dataShapeKey = (String)((Pair)this.aliasesMap.get(alias)).getValue();
      this.aliasesMap.put(aliasAp, Pair.of(dataShapeAp, dataShapeKey));
      FieldDefinition primaryKey = this.dataShapeUtils.getPrimaryKeyField(dataShape);
      FieldDefinition primaryKeyAp = this.dataShapeUtils.getPrimaryKeyField(dataShapeAp);
      Pair<String, BaseTypes> sourcePathInfoAp = getFieldInfo(dataShape, primaryKey, alias);
      Pair<String, BaseTypes> targetPathInfoAp = getFieldInfo(dataShapeAp, primaryKeyAp, aliasAp);
      this.query.join(getJoin(dataShapeAp, aliasAp, JoinType.LEFT, (String)sourcePathInfoAp.getKey(), (String)targetPathInfoAp
            .getKey(), (BaseTypes)targetPathInfoAp.getValue(), null, null));
    } 
  }
  
  private Join getJoin(DataShape dataShape, String alias, JoinType joinType, String sourcePath, String targetPath, BaseTypes targetBaseType, String value, List<JoinCondition> conditions) {
    return new Join(this.databaseHandler.getTableName(dataShape), alias, sourcePath, targetPath, targetBaseType, value, joinType, conditions);
  }
  
  private void transformJoins(JSONArray joins) {
    if (joins != null) {
      int length = joins.length();
      for (int i = 0; i < length; i++) {
        JSONObject join = joins.getJSONObject(i);
        String sourceDataShapeName = join.optString("sourceDataShapeName");
        String sourceFieldName = join.optString("sourceFieldName");
        String sourceAlias = join.optString("sourceAlias");
        String targetDataShapeName = join.getString("targetDataShapeName");
        String targetFieldName = join.getString("targetFieldName");
        String targetAlias = join.optString("targetAlias");
        String value = join.optString("value");
        JoinType joinType = JoinType.valueOf(join.getString("type"));
        JSONArray conditions = join.optJSONArray("joinConditions");
        Pair<String, BaseTypes> sourcePathInfo = getInfo(sourceDataShapeName, sourceFieldName, sourceAlias);
        DataShape targetDataShape = this.dataShapeUtils.getDataShape(targetDataShapeName);
        String targetAliasDataShape = getAlias(targetDataShape, targetAlias);
        this.aliasesMap.put(targetAliasDataShape, 
            Pair.of(targetDataShape, (
              targetAlias == null || targetAlias.isEmpty()) ? targetDataShape.getName() : 
              targetAlias));
        Pair<String, BaseTypes> targetPathInfo = getPathInfo(targetDataShape, targetFieldName, targetAliasDataShape);
        List<JoinCondition> joinConditions = getConditions(conditions, targetDataShape, targetAliasDataShape);
        BaseTypes targetBaseType = (BaseTypes)targetPathInfo.getValue();
        String source = (sourcePathInfo != null) ? (String)sourcePathInfo.getKey() : null;
        String target = (String)targetPathInfo.getKey();
        addJoinWithAp(targetDataShape, joinType, source, target, targetBaseType, value, targetAliasDataShape, joinConditions);
      } 
    } 
  }
  
  private Pair<String, BaseTypes> getInfo(String dataShapeName, String fieldName, String alias) {
    if (dataShapeName != null && !dataShapeName.isEmpty()) {
      DataShape sourceDataShape = this.dataShapeUtils.getDataShape(dataShapeName);
      alias = getAlias(sourceDataShape, alias);
      if (fieldName != null && !fieldName.isEmpty())
        return getPathInfo(sourceDataShape, fieldName, alias); 
    } 
    return null;
  }
  
  private List<JoinCondition> getConditions(JSONArray conditions, DataShape targetDataShape, String targetAliasDataShape) {
    List<JoinCondition> joinConditions = null;
    if (conditions != null && conditions.length() > 0) {
      joinConditions = Lists.newArrayList();
      for (int c = 0; c < conditions.length(); c++) {
        JSONObject condition = conditions.getJSONObject(c);
        joinConditions.add(getJoinCondition(condition, targetDataShape, targetAliasDataShape));
      } 
    } 
    return joinConditions;
  }
  
  private JoinCondition getJoinCondition(JSONObject condition, DataShape targetDataShape, String targetAliasDataShape) {
    String sourceDataShapeName = condition.optString("sourceDataShapeName");
    String sourceFieldName = condition.optString("sourceFieldName");
    String sourceAlias = condition.optString("sourceAlias");
    String targetFieldName = condition.optString("targetFieldName");
    String value = condition.optString("value");
    String type = condition.getString("type");
    Pair<String, BaseTypes> sourcePathInfo = getInfo(sourceDataShapeName, sourceFieldName, sourceAlias);
    String source = (sourcePathInfo != null) ? (String)sourcePathInfo.getKey() : null;
    Pair<String, BaseTypes> targetPathInfo = getPathInfo(targetDataShape, targetFieldName, targetAliasDataShape);
    String target = (String)targetPathInfo.getKey();
    return new JoinCondition(source, target, (BaseTypes)targetPathInfo.getValue(), value, 
        JoinConditionType.valueOf(type));
  }
  
  private JdbcQuery.Criteria transformFilters(JSONObject jsonFilter, JdbcQuery jdbcQuery) throws JSONException {
    JSONArray jsonFilters;
    JdbcQuery.Criteria restriction;
    if (jsonFilter == null)
      return jdbcQuery.getCriteria(); 
    String filterType = jsonFilter.getString("type").toUpperCase();
    switch (filterType) {
      case "AND":
      case "OR":
        restriction = jdbcQuery.getCriteria();
        jsonFilters = jsonFilter.optJSONArray("filters");
        if (jsonFilters == null && jsonFilter.has("filters")) {
          restriction = transformFilter(jsonFilter.getJSONObject("filters"));
        } else if (jsonFilters != null) {
          int numFilters = jsonFilters.length();
          List<JdbcQuery.Criteria> restrictions = new ArrayList<>(numFilters);
          for (int ii = 0; ii < numFilters; ii++) {
            JSONObject jsonChildFilter = jsonFilters.getJSONObject(ii);
            restrictions.add(transformFilters(jsonChildFilter, jdbcQuery));
          } 
          restriction.append(filterType, restrictions);
        } 
        return restriction;
    } 
    restriction = transformFilter(jsonFilter);
    return restriction;
  }
  
  private JdbcQuery.Criteria transformFilter(JSONObject jsonFilter) throws JSONException {
    String fieldName = jsonFilter.getString("fieldName");
    String dataShapeName = jsonFilter.optString("dataShapeName");
    DataShape dataShape = this.dataShape;
    if (dataShapeName != null && !dataShapeName.isEmpty())
      dataShape = this.dataShapeUtils.getDataShape(dataShapeName); 
    String alias = getAlias(dataShape, jsonFilter.optString("alias"));
    Pair<String, BaseTypes> pathInfo = getPathInfo(dataShape, fieldName, alias);
    String path = (String)pathInfo.getKey();
    BaseTypes baseType = (BaseTypes)pathInfo.getValue();
    String filterType = jsonFilter.getString("type");
    boolean isCaseSensitive = true;
    switch (filterType) {
      case "BETWEEN":
        return this.query.between(path, convert(dataShapeName, fieldName, baseType, jsonFilter, "from"), 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "to"));
      case "NOTBETWEEN":
        return this.query.notBetween(path, convert(dataShapeName, fieldName, baseType, jsonFilter, "from"), 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "to"));
      case "GT":
        return this.query.gt(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "GE":
        return this.query.ge(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "LT":
        return this.query.lt(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "LE":
        return this.query.le(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "EQ":
        return this.query.eq(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "NE":
        return this.query.ne(path, isCaseSensitive, 
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"));
      case "LIKE":
        return this.query.like(path, isCaseSensitive, ((String)
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"))
            .replaceAll("\\*", "%"));
      case "NOTLIKE":
        return this.query.notLike(path, isCaseSensitive, ((String)
            convert(dataShapeName, fieldName, baseType, jsonFilter, "value"))
            .replaceAll("\\*", "%"));
      case "MATCHES":
        return this.query.matches(path, isCaseSensitive, (String)
            convert(dataShapeName, fieldName, baseType, jsonFilter, "expression"));
      case "NOTMATCHES":
        return this.query.notMatches(path, isCaseSensitive, (String)
            convert(dataShapeName, fieldName, baseType, jsonFilter, "expression"));
      case "MISSINGVALUE":
        return this.query.isNull(path);
      case "NOTMISSINGVALUE":
        return this.query.isNotNull(path);
      case "IN":
        return this.query.in(path, convert(fieldName, baseType, jsonFilter.optJSONArray("values")));
      case "NOTIN":
        return this.query.notIn(path, convert(fieldName, baseType, jsonFilter.optJSONArray("values")));
      case "TAGGED":
        return this.query.in(path, convert(fieldName, baseType, jsonFilter.optJSONArray("tags")));
      case "NOTTAGGED":
        return this.query.notIn(path, convert(fieldName, baseType, jsonFilter.optJSONArray("tags")));
      case "NEAR":
      case "NOTNEAR":
        throw new NotImplementedException("NotImplemented filterType: " + filterType);
    } 
    throw new IllegalArgumentException("Unsupported filterType: " + filterType);
  }
  
  private void transformSorts(JSONArray jsonSorts) throws JSONException {
    if (jsonSorts == null || jsonSorts.length() == 0)
      return; 
    int numSorts = jsonSorts.length();
    for (int rowNo = 0; rowNo < numSorts; rowNo++) {
      JSONObject jsonSort = jsonSorts.getJSONObject(rowNo);
      String fieldName = jsonSort.getString("fieldName");
      String dataShapeName = jsonSort.optString("dataShapeName");
      DataShape dataShape = this.dataShape;
      if (dataShapeName != null && !dataShapeName.isEmpty())
        dataShape = this.dataShapeUtils.getDataShape(dataShapeName); 
      String alias = getAlias(dataShape, jsonSort.optString("alias"));
      Pair<String, BaseTypes> pathInfo = getPathInfo(dataShape, fieldName, alias);
      boolean isAscending = jsonSort.optBoolean("isAscending", true);
      boolean isCaseSensitive = jsonSort.optBoolean("isCaseSensitive", true);
      BaseTypes type = (BaseTypes)pathInfo.getValue();
      if (!isCaseSensitive && BaseTypes.isStringBaseType(type)) {
        this.query.orderBy("lower(" + (String)pathInfo.getKey() + ")", isAscending);
      } else {
        this.query.orderBy((String)pathInfo.getKey(), isAscending);
      } 
    } 
  }
  
  private void transformGroupBys(JSONArray jsonGroupBys) throws JSONException {
    if (jsonGroupBys == null || jsonGroupBys.length() == 0)
      return; 
    int numGroupBys = jsonGroupBys.length();
    for (int rowNo = 0; rowNo < numGroupBys; rowNo++) {
      JSONObject jsonGroupBy = jsonGroupBys.getJSONObject(rowNo);
      String fieldName = jsonGroupBy.getString("fieldName");
      String dataShapeName = jsonGroupBy.optString("dataShapeName");
      DataShape dataShape = this.dataShape;
      if (dataShapeName != null && !dataShapeName.isEmpty())
        dataShape = this.dataShapeUtils.getDataShape(dataShapeName); 
      String alias = getAlias(dataShape, jsonGroupBy.optString("alias"));
      Pair<String, BaseTypes> pathInfo = getPathInfo(dataShape, fieldName, alias);
      boolean isCaseSensitive = jsonGroupBy.optBoolean("isCaseSensitive", true);
      BaseTypes type = (BaseTypes)pathInfo.getValue();
      if (!isCaseSensitive && BaseTypes.isStringBaseType(type)) {
        this.query.groupBy("lower(" + (String)pathInfo.getKey() + ")");
      } else {
        this.query.groupBy((String)pathInfo.getKey());
      } 
    } 
  }
  
  private Comparable convert(String dataShapeName, String fieldName, BaseTypes baseType, JSONObject json, String key) {
    Comparable object = (Comparable)json.opt(key);
    if (this.dataShape != null)
      try {
        if (BaseTypes.DATETIME.equals(baseType)) {
          Long longValue = Long.valueOf(json.optLong(key));
          object = (Comparable)getSqlValue(new Timestamp(longValue.longValue()), BaseTypes.DATETIME);
        } else {
          object = (Comparable)BaseTypes.ConvertToObject(object, baseType);
        } 
      } catch (Exception e) {
        throw new IllegalArgumentException("Cannot convert: " + dataShapeName + "-" + fieldName + " " + object + " to " + baseType
            .name(), e);
      }  
    return object;
  }
  
  private List<Object> convert(String fieldName, BaseTypes baseType, JSONArray json) throws JSONException {
    List<Object> list = new ArrayList();
    if (json == null || json.length() == 0)
      return list; 
    if (this.dataShape != null) {
      try {
        if (null != baseType) {
          switch (baseType) {
            case TAGS:
              list.addAll(convertTagValues(json));
              return list;
            case DATETIME:
              for (var i = 0; i < json.length(); i++) {
                Long longValue = Long.valueOf(json.getLong(i));
                list.add(getSqlValue(new Timestamp(longValue.longValue()), BaseTypes.DATETIME));
              } 
              return list;
          } 
          for (int i = 0; i < json.length(); i++) {
            Object object = json.get(i);
            object = BaseTypes.ConvertToObject(object, baseType);
            if (object instanceof DateTime)
              object = ((DateTime)object).toDate(); 
            list.add(i, object);
          } 
        } 
      } catch (Exception e) {
        throw new IllegalArgumentException("Cannot convert: " + fieldName, e);
      } 
    } else {
      for (int i = 0; i < json.length(); i++) {
        Object object = json.get(i);
        list.add(object);
      } 
    } 
    return list;
  }
  
  Object getSqlValue(Object value, BaseTypes baseTypes) {
    Optional<DataTypeConverter> converter = this.databaseHandler.getDataTypeConverter(baseTypes);
    if (converter.isPresent())
      return ((DataTypeConverter)converter.get()).toSqlDataTypeValue(value); 
    return value;
  }
  
  private List<String> convertTagValues(JSONArray tagArray) throws Exception {
    List<String> tags = new ArrayList<>();
    if (tagArray != null) {
      TagCollection tagCollection = TagCollectionPrimitive.convertFromObject(tagArray).getValue();
      for (TagLink tagLink : tagCollection)
        tags.add(tagLink.toString()); 
    } 
    return tags;
  }
  
  private Pair<String, BaseTypes> getPathInfo(DataShape dataShape, String fieldName, String alias) {
    String pathAlias = getAlias(dataShape, alias);
    Optional<Pair<String, BaseTypes>> foundPathInfo = getCachePathInfo(dataShape, fieldName, pathAlias);
    if (foundPathInfo.isPresent())
      return foundPathInfo.get(); 
    String path = null;
    if (this.aliases != null)
      fieldName = this.aliases.optString(fieldName, fieldName); 
    if (this.fieldMap != null)
      path = this.fieldMap.getOrDefault(fieldName, fieldName); 
    Pair<String, BaseTypes> fieldInfo = getFieldInfo(dataShape, fieldName, pathAlias);
    path = (path != null) ? path : (String)fieldInfo.getKey();
    BaseTypes baseTypes = (BaseTypes)fieldInfo.getValue();
    Pair<String, BaseTypes> pathInfo = Pair.of(path, baseTypes);
    cachePathInfo(dataShape, fieldName, pathAlias, pathInfo);
    return pathInfo;
  }
  
  private Pair<String, BaseTypes> getFieldInfo(DataShape dataShape, String fieldName, String alias) {
    if (dataShape != null) {
      Optional<FieldDefinition> foundFieldDefinition = getFieldDefinition(dataShape, fieldName);
      if (foundFieldDefinition.isPresent())
        return getFieldInfo(dataShape, foundFieldDefinition.get(), alias); 
      Optional<DataShape> dataShapeApFound = this.dataShapeUtils.getDataShapeAp(dataShape);
      if (dataShapeApFound.isPresent()) {
        Optional<FieldDefinition> foundFieldDefinitionAp = getFieldDefinition(dataShapeApFound.get(), fieldName);
        if (foundFieldDefinitionAp.isPresent()) {
          String aliasAp = getAliasAp(dataShapeApFound.get(), alias, "_ap");
          return getFieldInfo(dataShapeApFound.get(), foundFieldDefinitionAp.get(), aliasAp);
        } 
      } 
      throw new RuntimeException("Field not found:" + dataShape.getName() + "-" + fieldName);
    } 
    throw new RuntimeException("Data shape is null");
  }
  
  private Pair<String, BaseTypes> getFieldInfo(DataShape dataShape, FieldDefinition fieldDefinition, String alias) {
    String path = getAlias(dataShape, alias) + "." + getAlias(dataShape, alias);
    return Pair.of(path, getFieldBaseType(dataShape, fieldDefinition));
  }
  
  private String getAliasAp(DataShape dataShape, String alias, String postFix) {
    String dataShapeAlias = getAlias(dataShape, alias);
    if (postFix != null && !postFix.isEmpty())
      dataShapeAlias = dataShapeAlias + dataShapeAlias; 
    return dataShapeAlias;
  }
  
  private String getAlias(DataShape dataShape, String alias) {
    if (alias != null && !alias.isEmpty())
      return alias; 
    return this.databaseHandler.getTableName(dataShape);
  }
  
  private Optional<FieldDefinition> getFieldDefinition(DataShape dataShape, String fieldName) {
    if (dataShape != null) {
      FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName);
      if (fieldDefinition != null)
        return Optional.of(fieldDefinition); 
    } 
    return Optional.empty();
  }
  
  private Optional<Pair<String, BaseTypes>> getCachePathInfo(DataShape dataShape, String fieldName, String alias) {
    if (this.pathInfoMap.containsKey(alias)) {
      Map<String, Pair<String, BaseTypes>> map = this.pathInfoMap.get(alias);
      if (map != null && map.containsKey(fieldName)) {
        Pair<String, BaseTypes> pathInfo = map.get(fieldName);
        if (pathInfo != null)
          return Optional.of(pathInfo); 
      } 
    } 
    return Optional.empty();
  }
  
  private void cachePathInfo(DataShape dataShape, String fieldName, String alias, Pair<String, BaseTypes> pathInfo) {
    Map<String, Pair<String, BaseTypes>> map = this.pathInfoMap.computeIfAbsent(alias, k -> Maps.newHashMap());
    map.putIfAbsent(fieldName, pathInfo);
  }
  
  protected Pair<String, String> getFieldPath(DataShape dataShape, String fieldName, String alias) {
    String pathInfo = (String)getPathInfo(dataShape, fieldName, alias).getKey();
    String selectName = pathInfo.replace('.', '_');
    return Pair.of(pathInfo, selectName);
  }
  
  public Select getSelect() {
    return this.jsonSelect;
  }
  
  private BaseTypes getFieldBaseType(DataShape dataShape, FieldDefinition fieldDefinition) {
    BaseTypes baseType = fieldDefinition.getBaseType();
    Optional<DataShapeDatabaseInfo> foundDataShapeDatabaseInfo = this.databaseHandler.getDatabaseInfoManager().getDataShapeDatabaseInfo(dataShape.getName());
    if (foundDataShapeDatabaseInfo.isPresent()) {
      DataShapeDatabaseInfo dataShapeDatabaseInfo = foundDataShapeDatabaseInfo.get();
      Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = dataShapeDatabaseInfo.getFieldDatabaseInfo(fieldDefinition);
      if (foundFieldDatabaseInfo.isPresent()) {
        FieldDatabaseInfo fieldDatabaseInfo = foundFieldDatabaseInfo.get();
        String baseTypeString = fieldDatabaseInfo.getBaseType();
        if (baseTypeString != null && !baseTypeString.isEmpty())
          baseType = BaseTypes.valueOf(baseTypeString); 
      } 
    } 
    return baseType;
  }
}
