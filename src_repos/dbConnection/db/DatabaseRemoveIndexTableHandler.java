package twx.core.db;

import com.thingworx.datashape.DataShape;


public class DatabaseRemoveIndexTableHandler extends AbstractDatabaseIndexTableHandler {
  DatabaseRemoveIndexTableHandler( DataShape dataShape,  Index index,  DatabaseHandler databaseHandler) {
    super(dataShape, index, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape, Index index) {
    return "DROP INDEX " + getIndexName(dataShape, index);
  }
}
