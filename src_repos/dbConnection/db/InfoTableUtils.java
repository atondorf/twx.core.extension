package twx.core.db;

import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfoTableUtils {
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  protected InfoTable buildInfoTableFromQueryResult(DataShape dataShape, QueryResult queryResult, DatabaseHandler databaseHandler) throws Exception {
    FieldDefinitionCollection fieldDefinitionCollection = getFieldDefinitionCollection(dataShape);
    InfoTable infoTable = getInfoTable(fieldDefinitionCollection);
    for (ResultRow resultRow : queryResult.getRows()) {
      ValueCollection row = new ValueCollection();
      for (FieldDefinition fieldDefinition : fieldDefinitionCollection.values()) {
        String columnName = databaseHandler.getColumnName(fieldDefinition);
        Object value = resultRow.getValues().get(columnName);
        if (value != null) {
          value = getJavaValue(value, fieldDefinition.getBaseType(), databaseHandler);
          row.SetValue(fieldDefinition, value);
        } 
      } 
      infoTable.addRow(row);
    } 
    return infoTable;
  }
  
  private Object getJavaValue(Object value, BaseTypes baseTapes, DatabaseHandler databaseHandler) {
    Optional<DataTypeConverter> converter = databaseHandler.getDataTypeConverter(baseTapes);
    if (converter.isPresent())
      return ((DataTypeConverter)converter.get()).toJavaDataTypeValue(value); 
    return value;
  }
  
  protected InfoTable buildInfoTableFromDataChanges(DataShape dataShape, List<DataChange> dataChanges) throws Exception {
    InfoTable infoTable = getInfoTable(dataShape);
    for (DataChange dataChange : dataChanges)
      infoTable.addRow(dataChange.getValueCollection().clone()); 
    return infoTable;
  }
  
  public InfoTable getInfoTable(DataShape dataShape) throws Exception {
    return getInfoTable(getFieldDefinitionCollection(dataShape));
  }
  
  private InfoTable getInfoTable(FieldDefinitionCollection fieldDefinitionCollection) throws Exception {
    return InfoTableInstanceFactory.createInfoTableFromParameters(fieldDefinitionCollection);
  }
  
  private FieldDefinitionCollection getFieldDefinitionCollection(DataShape dataShape) {
    FieldDefinitionCollection resultFieldDefinitionCollection = new FieldDefinitionCollection();
    ArrayList<FieldDefinition> fieldDefinitions = dataShape.getFields().getOrderedFieldsByOrdinal();
    for (FieldDefinition fieldDefinition : fieldDefinitions)
      resultFieldDefinitionCollection.addFieldDefinition(fieldDefinition); 
    Optional<DataShape> foundDataShapeAp = this.dataShapeUtils.getDataShapeAp(dataShape);
    if (foundDataShapeAp.isPresent()) {
      DataShape dataShapeAp = foundDataShapeAp.get();
      ArrayList<FieldDefinition> fieldDefinitionArrayList = dataShapeAp.getFields().getOrderedFieldsByOrdinal();
      for (FieldDefinition fieldDefinition : fieldDefinitionArrayList)
        resultFieldDefinitionCollection.addFieldDefinition(fieldDefinition); 
    } 
    return resultFieldDefinitionCollection;
  }
  
  protected InfoTable buildInfoTableFromQueryWithSelect(Select select, QueryResult queryResult, DatabaseHandler databaseHandler) throws Exception {
    FieldDefinitionCollection fieldDefinitionCollection = new FieldDefinitionCollection();
    for (SelectElement selectElement : select.getSelectElements()) {
      FieldDefinition selectFieldDefinition = new FieldDefinition();
      selectFieldDefinition.setName(selectElement.getInfoTableColumnKey());
      selectFieldDefinition.setBaseType(selectElement.getFieldDefinition().getBaseType());
      fieldDefinitionCollection.addFieldDefinition(selectFieldDefinition);
    } 
    InfoTable infoTable = InfoTableInstanceFactory.createInfoTableFromParameters(fieldDefinitionCollection);
    for (ResultRow resultRow : queryResult.getRows()) {
      ValueCollection row = new ValueCollection();
      for (SelectElement selectElement : select.getSelectElements()) {
        Object value = resultRow.getValues().get(selectElement.getSelectName());
        if (value != null) {
          BaseTypes baseTypes = selectElement.getFieldDefinition().getBaseType();
          value = getJavaValue(value, baseTypes, databaseHandler);
          String key = selectElement.getInfoTableColumnKey();
          row.SetValue(key, value, baseTypes);
        } 
      } 
      infoTable.addRow(row);
    } 
    return infoTable;
  }
}
