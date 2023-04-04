package twx.core.db;

import com.thingworx.datashape.DataShape;


public class DatabaseRemoveIndexOnTableHandler extends DatabaseRemoveIndexTableHandler {
  DatabaseRemoveIndexOnTableHandler( DataShape dataShape,  Index index,  DatabaseHandler databaseHandler) {
    super(dataShape, index, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape, Index index) {
    return "DROP INDEX " + getIndexName(dataShape, index) + " ON " + 
      getTableName(dataShape);
  }
}
