package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

public class PostgresDatabaseTableHandlerFactory implements DatabaseTableHandlerFactory {
  DatabaseHandler databaseHandler;
  
  PostgresDatabaseTableHandlerFactory(DatabaseHandler databaseHandler) {
    Validate.notNull(databaseHandler);
    this.databaseHandler = databaseHandler;
  }
  
  public DatabaseTableHandler getCreateTableHandler(DataShape dataShape, DataShapeDatabaseInfo dataShapeDatabaseInfo) {
    return new DatabaseCreateTableHandler(dataShape, dataShapeDatabaseInfo, this.databaseHandler);
  }
  
  public DatabaseTableHandler getDropTableHandler(DataShape dataShape) {
    return new DatabaseDropTableHandler(dataShape, this.databaseHandler);
  }
  
  public DatabaseTableHandler getAddColumnHandler(DataShape dataShape, String fieldName, FieldDatabaseInfo fieldDatabaseInfo) {
    return new AddColumnTableHandler(dataShape, fieldName, fieldDatabaseInfo, this.databaseHandler);
  }
  
  public DatabaseTableHandler getRemoveColumnHandler(DataShape dataShape, String fieldName) {
    return new DatabaseRemoveColumnTableHandler(dataShape, fieldName, this.databaseHandler);
  }
  
  public DatabaseTableHandler getCreateIndexHandler(DataShape dataShape, Index index) {
    return new DatabaseCreateIndexTableHandler(dataShape, index, this.databaseHandler);
  }
  
  public DatabaseTableHandler getRemoveIndexHandler(DataShape dataShape, Index index) {
    return new DatabaseRemoveIndexTableHandler(dataShape, index, this.databaseHandler);
  }
  
  public DatabaseTableHandler getInsertHandler(DataShape dataShape, ValueCollection valueCollection) {
    return new DatabaseInsertAPTableHandler(dataShape, valueCollection, this.databaseHandler);
  }
  
  public DatabaseTableHandler getUpdateHandler(DataShape dataShape, ValueCollection valueCollection) {
    return new DatabaseUpdateAPTableHandler(dataShape, valueCollection, this.databaseHandler);
  }
  
  public DatabaseTableHandler getDeleteHandler(DataShape dataShape, ValueCollection valueCollection) {
    return new DatabaseDeleteAPTableHandler(dataShape, valueCollection, this.databaseHandler);
  }
  
  public DatabaseTableHandler getQueryHandler(DataShape dataShape, JSONObject filter, int offset, int limit) {
    return new DatabaseQueryTableHandler(dataShape, filter, this.databaseHandler, offset, limit);
  }
  
  public DatabaseTableHandler getAddForeignKeyHandler(DataShape dataShape, String fieldName, ForeignKey foreignKey) {
    return new DatabaseAddForeignKeyTableHandler(dataShape, fieldName, foreignKey, this.databaseHandler);
  }
  
  public DatabaseTableHandler getRemoveForeignKeyHandler(DataShape dataShape, String fieldName, ForeignKey foreignKey) {
    return new DatabaseRemoveForeignKeyTableHandler(dataShape, fieldName, foreignKey, this.databaseHandler);
  }
  
  public DatabaseTableHandler getUpdateColumnHandler(DataShape dataShape, String fieldName, FieldDatabaseInfo fieldDatabaseInfo) {
    return new UpdateColumnTableHandler(dataShape, fieldName, fieldDatabaseInfo, this.databaseHandler);
  }
}
