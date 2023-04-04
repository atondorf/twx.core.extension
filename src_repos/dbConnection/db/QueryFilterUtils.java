package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import com.thingworx.types.TagCollection;
import com.thingworx.types.TagLink;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryFilterUtils {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(QueryFilterUtils.class);
  
  private static String DataShapeName = "dataShapeName";
  
  private static String FilterFieldName = "fieldName";
  
  private static String FilterType = "type";
  
  private static String FilterValue = "value";
  
  private static String FilterTags = "tags";
  
  private static String Filters = "filters";
  
  private static String IsAscending = "isAscending";
  
  private static String Sorts = "sorts";
  
  private static String Joins = "joins";
  
  private static String Select = "select";
  
  private static String As = "as";
  
  private static String Alias = "alias";
  
  private static final String TYPE = "type";
  
  private static final String SOURCE_DATA_SHAPE_NAME = "sourceDataShapeName";
  
  private static final String SOURCE_FIELD_NAME = "sourceFieldName";
  
  private static final String SOURCE_ALIAS = "sourceAlias";
  
  private static final String TARGET_DATA_SHAPE_NAME = "targetDataShapeName";
  
  private static final String TARGET_FIELD_NAME = "targetFieldName";
  
  private static final String TARGET_ALIAS = "targetAlias";
  
  private static final String VALUE = "value";
  
  public static JSONObject createQuery(JSONObject filter) {
    JSONObject query = new JSONObject();
    try {
      query.put(Filters, filter);
    } catch (JSONException e) {
      _logger.error("Failed to create query", (Throwable)e);
      throw new RuntimeException(e);
    } 
    return query;
  }
  
  public static void addSort(JSONObject query, String dataShapeName, String fieldName, String alias) {
    addSort(query, dataShapeName, fieldName, alias, true);
  }
  
  public static void addSort(JSONObject query, String dataShapeName, String fieldName, String alias, boolean ascending) {
    try {
      JSONObject sort = new JSONObject();
      sort.put(DataShapeName, dataShapeName);
      sort.put(FilterFieldName, fieldName);
      sort.put(Alias, alias);
      sort.put(IsAscending, ascending);
      JSONArray sortArray = query.has(Sorts) ? (JSONArray)query.get(Sorts) : new JSONArray();
      sortArray.put(sort);
      query.put(Sorts, sortArray);
    } catch (JSONException e) {
      _logger.error("Failed to add sort ", (Throwable)e);
      throw new RuntimeException(e);
    } 
  }
  
  public static void addJoin(JSONObject query, String type, String sourceDataShapeName, String sourceFieldName, String sourceAlias, String targetDataShapeName, String targetFieldName, String targetAlias, String value, JSONArray conditions) {
    try {
      JSONObject join = new JSONObject();
      join.put("type", type);
      join.put("sourceDataShapeName", sourceDataShapeName);
      join.put("sourceFieldName", sourceFieldName);
      join.put("sourceAlias", sourceAlias);
      join.put("targetDataShapeName", targetDataShapeName);
      join.put("targetFieldName", targetFieldName);
      join.put("targetAlias", targetAlias);
      join.put("value", value);
      join.put("joinConditions", conditions);
      JSONArray joinArray = query.has(Joins) ? (JSONArray)query.get(Joins) : new JSONArray();
      joinArray.put(join);
      query.put(Joins, joinArray);
    } catch (JSONException e) {
      _logger.error("Failed to add join ", (Throwable)e);
      throw new RuntimeException(e);
    } 
  }
  
  public static void addSelect(JSONObject query, String dataShapeName, String fieldName, String as, String alias) {
    try {
      JSONObject select = new JSONObject();
      select.put(DataShapeName, dataShapeName);
      select.put(FilterFieldName, fieldName);
      select.put(As, as);
      select.put(Alias, alias);
      JSONArray selectArray = query.has(Select) ? (JSONArray)query.get(Select) : new JSONArray();
      selectArray.put(select);
      query.put(Select, selectArray);
    } catch (JSONException e) {
      _logger.error("Failed to add select ", (Throwable)e);
      throw new RuntimeException(e);
    } 
  }
  
  public static JSONObject andFilters(JSONObject... filters) {
    JSONArray filterArray = new JSONArray();
    for (JSONObject filter : filters)
      filterArray.put(filter); 
    return andFilters(filterArray);
  }
  
  public static JSONObject andFilters(JSONArray filters) {
    JSONObject andFilter = new JSONObject();
    try {
      JSONObject filter = new JSONObject();
      filter.put(FilterType, "AND");
      filter.put(Filters, filters);
      andFilter.put(Filters, filter);
    } catch (JSONException e) {
      _logger.error("Failed to AND filters ", (Throwable)e);
      throw new RuntimeException(e);
    } 
    return andFilter;
  }
  
  public static JSONObject orFilters(JSONObject... filters) {
    JSONArray filterArray = new JSONArray();
    for (JSONObject filter : filters)
      filterArray.put(filter); 
    return orFilters(filterArray);
  }
  
  public static JSONObject orFilters(JSONArray filters) {
    try {
      JSONObject orFilter = new JSONObject();
      orFilter.put(FilterType, "OR");
      orFilter.put(Filters, filters);
      return orFilter;
    } catch (JSONException e) {
      _logger.error("Failed to OR filters ", (Throwable)e);
      throw new RuntimeException(e);
    } 
  }
  
  public static void appendFilter(JSONObject filters, JSONObject filter) throws JSONException {
    filters.accumulate(Filters, filter);
  }
  
  public static JSONObject createEqFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "EQ", value);
  }
  
  public static JSONObject createNeFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "NE", value);
  }
  
  public static JSONObject createBetweenFilter(String dataShapeName, String fieldName, String alias, Object[] values) {
    return createFilter(dataShapeName, fieldName, alias, "BETWEEN", values);
  }
  
  public static JSONObject createMissingValueFilter(String dataShapeName, String alias, String fieldName) {
    return createFilter(dataShapeName, fieldName, alias, "MISSINGVALUE", null);
  }
  
  public static JSONObject createNotMissingValueFilter(String dataShapeName, String fieldName, String alias) {
    return createFilter(dataShapeName, fieldName, alias, "NOTMISSINGVALUE", null);
  }
  
  public static JSONObject createGtFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "GT", value);
  }
  
  public static JSONObject createGeFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "GE", value);
  }
  
  public static JSONObject createLtFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "LT", value);
  }
  
  public static JSONObject createLeFilter(String dataShapeName, String fieldName, String alias, Object value) {
    return createFilter(dataShapeName, fieldName, alias, "LE", value);
  }
  
  public static JSONObject createStartsWithFilter(String dataShapeName, String fieldName, String alias, String value) {
    return createFilter(dataShapeName, fieldName, alias, "LIKE", value + "%");
  }
  
  public static JSONObject createEndsWithFilter(String dataShapeName, String fieldName, String alias, String value) {
    return createFilter(dataShapeName, fieldName, alias, "LIKE", "%" + value);
  }
  
  public static JSONObject createContainsFilter(String dataShapeName, String fieldName, String alias, String value) {
    return createFilter(dataShapeName, fieldName, alias, "LIKE", "%" + value + "%");
  }
  
  public static JSONObject createTaggedFilter(String dataShapeName, String fieldName, String alias, TagCollection tagCollection) {
    List<TagLink> tagLinks = new ArrayList<>();
    for (TagLink tagLink : tagCollection)
      tagLinks.add(tagLink); 
    return createFilter(dataShapeName, fieldName, alias, "TAGGED", tagLinks);
  }
  
  public static JSONObject createOrFilter(String dataShapeName, String fieldName, String alias, TagCollection tagCollection) {
    JSONArray tagLinks = new JSONArray();
    for (TagLink tagLink : tagCollection) {
      String tagValue = tagLink.getVocabulary() + ":" + tagLink.getVocabulary();
      tagLinks.put(createEqFilter(dataShapeName, fieldName, alias, tagValue));
    } 
    return orFilters(tagLinks);
  }
  
  public static JSONObject createFilter(String dataShapeName, String fieldName, String alias, String type, Object value) {
    JSONObject filter = new JSONObject();
    try {
      if ("EQ".equals(type) && value == null)
        type = "MISSINGVALUE"; 
      String valueName = FilterValue;
      if ("TAGGED".equals(type) || "NOTTAGGED"
        .equals(type))
        valueName = FilterTags; 
      filter.put(DataShapeName, dataShapeName);
      filter.put(Alias, alias);
      filter.put(FilterFieldName, fieldName);
      filter.put(FilterType, type);
      if (!"MISSINGVALUE".equals(type))
        filter.put(valueName, value); 
    } catch (JSONException e) {
      _logger.error("Failed to create filter ", (Throwable)e);
      throw new RuntimeException(e);
    } 
    return filter;
  }
}
