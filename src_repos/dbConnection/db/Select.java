package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

public class Select {
  protected static final String DATA_SHAPE_NAME = "dataShapeName";
  
  protected static final String FIELD_NAME = "fieldName";
  
  protected static final String FUNCTION = "function";
  
  protected static final String AS = "as";
  
  protected static final String ALIAS = "alias";
  
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private List<SelectElement> selectElements = null;
  
  private String primaryColumnName;
  
  Select( JSONArray selects,  DataShape dataShape, String dataShapeAlias,  Map<String, Pair<DataShape, String>> aliasesMap,  JsonQueryAdapter jsonQueryAdapter) {
    Validate.notNull(jsonQueryAdapter);
    Validate.notNull(dataShape);
    if (selects != null) {
      this
        .selectElements = buildSelect(selects, aliasesMap, jsonQueryAdapter, dataShape, dataShapeAlias);
    } else {
      Validate.notNull(aliasesMap);
      this.selectElements = buildSelect(aliasesMap, jsonQueryAdapter, dataShape, dataShapeAlias);
    } 
  }
  
  private List<SelectElement> buildSelect( JSONArray selects,  Map<String, Pair<DataShape, String>> aliasesMap,  JsonQueryAdapter jsonQueryAdapter,  DataShape dataShape, String dataShapeAlias) {
    List<SelectElement> selectElements = Lists.newArrayList();
    int count = 1;
    int length = selects.length();
    for (int i = 0; i < length; i++) {
      JSONObject select = selects.getJSONObject(i);
      String dataShapeName = select.getString("dataShapeName");
      String fieldName = select.getString("fieldName");
      String function = select.optString("function");
      String as = select.optString("as");
      String alias = select.optString("alias");
      DataShape selectDataShape = this.dataShapeUtils.getDataShape(dataShapeName);
      FieldDefinition fieldDefinition = getFieldDefinition(selectDataShape, fieldName);
      if (isFieldValid(dataShape, fieldDefinition)) {
        Pair<String, String> selectValue = jsonQueryAdapter.getFieldPath(selectDataShape, fieldDefinition.getName(), alias);
        String dataShapeKey = (alias != null && !alias.isEmpty()) ? ((aliasesMap.get(alias) != null) ? (String)((Pair)aliasesMap.get(alias)).getValue() : null) : null;
        String infoTableColumnKey = getInfoTableColumnKey(dataShape, selectDataShape, fieldDefinition, dataShapeKey, alias, as);
        as = (as != null && !as.isEmpty()) ? as : ("col" + count);
        SqlFunction sqlFunction = null;
        if (function != null && !function.isEmpty())
          sqlFunction = SqlFunction.valueOf(function); 
        selectElements.add(new SelectElement(selectDataShape, fieldDefinition, (String)selectValue.getKey(), as, infoTableColumnKey, sqlFunction));
        count++;
      } 
    } 
    return selectElements;
  }
  
  private List<SelectElement> buildSelect( Map<String, Pair<DataShape, String>> aliasesMap,  JsonQueryAdapter jsonQueryAdapter,  DataShape dataShape, String dataShapeAlias) {
    List<SelectElement> selectElements = Lists.newArrayList();
    int count = 1;
    for (Map.Entry<String, Pair<DataShape, String>> entry : aliasesMap.entrySet()) {
      DataShape selectDataShape = (DataShape)((Pair)entry.getValue()).getKey();
      String dataShapeKey = (String)((Pair)entry.getValue()).getValue();
      String alias = entry.getKey();
      ArrayList<FieldDefinition> fieldDefinitionArrayList = selectDataShape.getFields().getOrderedFieldsByOrdinal();
      for (FieldDefinition fieldDefinition : fieldDefinitionArrayList) {
        if (isFieldValid(selectDataShape, fieldDefinition)) {
          Pair<String, String> selectValue = jsonQueryAdapter.getFieldPath(selectDataShape, fieldDefinition.getName(), alias);
          String infoTableColumnKey = getInfoTableColumnKey(dataShape, selectDataShape, fieldDefinition, dataShapeKey, alias, null);
          String selectName = "col" + count;
          SelectElement selectElement = new SelectElement(selectDataShape, fieldDefinition, (String)selectValue.getKey(), selectName, infoTableColumnKey, (SqlFunction)null);
          selectElements.add(selectElement);
          count++;
        } 
      } 
    } 
    return selectElements;
  }
  
  private boolean isFieldValid( DataShape dataShape,  FieldDefinition fieldDefinition) {
    if (this.dataShapeUtils.isAdditionalPropertiesDataShape(dataShape) && fieldDefinition
      .isPrimaryKey())
      return false; 
    return true;
  }
  

  List<SelectElement> getSelectElements() {
    return this.selectElements;
  }
  
  String getPrimaryColumnName() {
    return this.primaryColumnName;
  }
  

  List<String> getSelectList() {
    List<String> selectList = Lists.newArrayList();
    for (SelectElement selectElement : getSelectElements()) {
      SqlFunction sqlFunction = selectElement.getFunction();
      if (sqlFunction != null) {
        selectList.add(sqlFunction.name() + "(" + sqlFunction.name() + ") " + selectElement.getSelectValue());
        continue;
      } 
      selectList.add(selectElement.getSelectValue() + " " + selectElement.getSelectValue());
    } 
    return selectList;
  }
  

  private String getInfoTableColumnKey( DataShape dataShape,  DataShape selectDataShape,  FieldDefinition fieldDefinition) {
    if (dataShape.equals(selectDataShape))
      return fieldDefinition.getName(); 
    Optional<DataShape> foundDataShapeAp = this.dataShapeUtils.getDataShapeAp(dataShape);
    if (foundDataShapeAp.isPresent() && ((DataShape)foundDataShapeAp.get()).equals(selectDataShape))
      return fieldDefinition.getName(); 
    DataShape primaryDataShape = this.dataShapeUtils.getPrimaryDataShape(selectDataShape);
    String infoTableColumnKey = primaryDataShape.getName() + "." + primaryDataShape.getName();
    return getInfoTableColumnKey(infoTableColumnKey);
  }
  
  private String getInfoTableColumnKey( DataShape dataShape,  DataShape selectDataShape,  FieldDefinition fieldDefinition,  String dataShapeKey, String alias, String as) {
    if (as != null && !as.isEmpty())
      return as; 
    if (dataShapeKey != null && !dataShapeKey.isEmpty())
      return getInfoTableColumnKey(dataShapeKey + "." + dataShapeKey); 
    return getInfoTableColumnKey(dataShape, selectDataShape, fieldDefinition);
  }
  
  private String getInfoTableColumnKey( String infoTableColumnKey) {
    return infoTableColumnKey.replace('.', '_');
  }
  
  FieldDefinition getFieldDefinition( DataShape dataShape,  String fieldName) {
    FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName);
    if (fieldDefinition != null)
      return fieldDefinition; 
    Optional<DataShape> foundDataShapeAp = this.dataShapeUtils.getDataShapeAp(dataShape);
    if (foundDataShapeAp.isPresent()) {
      fieldDefinition = ((DataShape)foundDataShapeAp.get()).getFieldDefinition(fieldName);
      if (fieldDefinition != null)
        return fieldDefinition; 
    } 
    throw new RuntimeException("Field not found " + dataShape.getName() + ":" + fieldName);
  }
}
