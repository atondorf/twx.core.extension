package twx.core.db;

import twx.core.db.utils.ValueCollectionBuilder;
import com.thingworx.types.collections.ValueCollection;

public class DatabaseSchemaValidationHelper {
  private static String ENTITY_TYPE = "EntityType";
  
  private static String DATASHAPE_NAME = "DataShapeName";
  
  private static String TABLE_NAME = "TableName";
  
  private static String FIELD_NAME = "FieldName";
  
  private static String COLUMN_NAME = "ColumnName";
  
  private static String ENTITY_NAME = "EntityName";
  
  private static String MESSAGE = "Message";
  
  public static ValueCollection buildValidationInfo(String dataShapeName, String tableName, String fieldName, String columnName, String entityType, String entityName, String message) {
    return (new ValueCollectionBuilder()).put(DATASHAPE_NAME, dataShapeName)
      .put(TABLE_NAME, tableName).put(FIELD_NAME, fieldName).put(COLUMN_NAME, columnName)
      .put(ENTITY_TYPE, entityType).put(ENTITY_NAME, entityName).put(MESSAGE, message).get();
  }
  
  public static ValueCollection buildValidationInfo(String dataShapeName, String tableName, String entityType, String message) {
    return buildValidationInfo(dataShapeName, tableName, "", "", entityType, "", message);
  }
  
  public static String getValidationMessage(String propertyNames, String value, String dbValue) {
    return propertyNames + ": " + propertyNames + " does not match the database value: " + value;
  }
}
