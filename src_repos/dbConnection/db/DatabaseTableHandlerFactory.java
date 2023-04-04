package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import org.json.JSONObject;

public interface DatabaseTableHandlerFactory {
  DatabaseTableHandler getCreateTableHandler(DataShape paramDataShape, DataShapeDatabaseInfo paramDataShapeDatabaseInfo);
  
  DatabaseTableHandler getDropTableHandler(DataShape paramDataShape);
  
  DatabaseTableHandler getAddColumnHandler(DataShape paramDataShape, String paramString, FieldDatabaseInfo paramFieldDatabaseInfo);
  
  DatabaseTableHandler getRemoveColumnHandler(DataShape paramDataShape, String paramString);
  
  DatabaseTableHandler getCreateIndexHandler(DataShape paramDataShape, Index paramIndex);
  
  DatabaseTableHandler getRemoveIndexHandler(DataShape paramDataShape, Index paramIndex);
  
  DatabaseTableHandler getInsertHandler(DataShape paramDataShape, ValueCollection paramValueCollection);
  
  DatabaseTableHandler getUpdateHandler(DataShape paramDataShape, ValueCollection paramValueCollection);
  
  DatabaseTableHandler getDeleteHandler(DataShape paramDataShape, ValueCollection paramValueCollection);
  
  DatabaseTableHandler getQueryHandler(DataShape paramDataShape, JSONObject paramJSONObject, int paramInt1, int paramInt2) throws Exception;
  
  DatabaseTableHandler getAddForeignKeyHandler(DataShape paramDataShape, String paramString, ForeignKey paramForeignKey);
  
  DatabaseTableHandler getRemoveForeignKeyHandler(DataShape paramDataShape, String paramString, ForeignKey paramForeignKey);
  
  DatabaseTableHandler getUpdateColumnHandler(DataShape paramDataShape, String paramString, FieldDatabaseInfo paramFieldDatabaseInfo);
}
