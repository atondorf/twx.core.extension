package twx.core.db;

import com.thingworx.datashape.DataShape;

public class SqlServerDatabaseTableHandlerFactory extends PostgresDatabaseTableHandlerFactory {
  SqlServerDatabaseTableHandlerFactory(DatabaseHandler databaseHandler) {
    super(databaseHandler);
  }
  
  public DatabaseTableHandler getRemoveIndexHandler(DataShape dataShape, Index index) {
    return new DatabaseRemoveIndexOnTableHandler(dataShape, index, this.databaseHandler);
  }
  
  public DatabaseTableHandler getUpdateColumnHandler(DataShape dataShape, String fieldName, FieldDatabaseInfo fieldDatabaseInfo) {
    return new UpdateColumnTableHandlerSqlServer(dataShape, fieldName, fieldDatabaseInfo, this.databaseHandler);
  }
}
