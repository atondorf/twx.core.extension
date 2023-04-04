package twx.core.db;

import com.thingworx.datashape.DataShape;

public class DatabaseDropTableHandler extends AbstractDatabaseTableHandler {
  DatabaseDropTableHandler(DataShape dataShape, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape) {
    return "DROP TABLE " + getTableName(dataShape);
  }
}
