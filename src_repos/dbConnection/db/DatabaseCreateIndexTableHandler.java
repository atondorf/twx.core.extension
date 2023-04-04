package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.List;


public class DatabaseCreateIndexTableHandler extends AbstractDatabaseIndexTableHandler {
  DatabaseCreateIndexTableHandler( DataShape dataShape,  Index index,  DatabaseHandler databaseHandler) {
    super(dataShape, index, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape, Index index) {
    StringBuilder sqlCreateIndex = new StringBuilder(60);
    sqlCreateIndex.append("CREATE ");
    if (index.isUnique())
      sqlCreateIndex.append("UNIQUE "); 
    String columnsString = getColumnsString(this.fieldNames);
    String indexName = getIndexName(dataShape, index);
    sqlCreateIndex.append("INDEX ").append(indexName).append(" ON ").append(getTableName(dataShape))
      .append('(').append(columnsString).append(')');
    return sqlCreateIndex.toString();
  }
  
  private String getColumnsString(List<String> fieldNames) {
    StringBuilder columnsString = new StringBuilder(60);
    for (String fieldName : fieldNames) {
      String columnName = getColumnName(fieldName);
      columnsString.append(columnName).append(',');
    } 
    if (columnsString.toString().endsWith(","))
      columnsString = new StringBuilder(columnsString.substring(0, columnsString.length() - 1)); 
    return columnsString.toString();
  }
  
  private FieldDefinition getFieldDefinition(String filedName) {
    return getFieldDefinition(getDataShape(), filedName).get();
  }
  
  private String getColumnName(String filedName) {
    return getColumnName(getFieldDefinition(filedName));
  }
}
