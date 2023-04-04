package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;

public class DatabaseRemoveColumnTableHandler extends AbstractDatabaseColumnTableHandler {
  DatabaseRemoveColumnTableHandler(DataShape dataShape, String fieldName, DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    return "ALTER TABLE " + getTableName(dataShape) + " DROP COLUMN " + 
      getColumnName(fieldDefinition);
  }
}
