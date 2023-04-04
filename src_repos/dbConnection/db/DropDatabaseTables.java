package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.datashape.DataShape;
import java.util.List;


public class DropDatabaseTables extends AbstractUpdateDatabaseSchema<Boolean> {
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private DatabaseInfo databaseInfo;
  
  DropDatabaseTables( DatabaseHandler databaseHandler, DatabaseInfo databaseInfo) {
    super(databaseHandler);
    this.databaseInfo = databaseInfo;
  }
  
  public Boolean execute() throws Exception {
    List<DatabaseTableHandler> tableDatabaseTableHandlers = Lists.newArrayList();
    List<DatabaseTableHandler> foreignKeyDatabaseTableHandlers = Lists.newArrayList();
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : this.databaseInfo.getDbInfo()) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      DatabaseTableHandler databaseTableHandler = getFactory().getDropTableHandler(dataShape);
      tableDatabaseTableHandlers.add(databaseTableHandler);
      foreignKeyDatabaseTableHandlers
        .addAll(removeForeignKeys(dataShape, dataShapeDatabaseInfo.getForeignKeys()));
    } 
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    databaseTableHandlers.addAll(foreignKeyDatabaseTableHandlers);
    databaseTableHandlers.addAll(tableDatabaseTableHandlers);
    getDatabaseHandler().executeList(databaseTableHandlers);
    return Boolean.valueOf(true);
  }
  
  private List<DatabaseTableHandler> removeForeignKeys(DataShape dataShape, List<ForeignKey> foreignKeys) {
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    if (foreignKeys != null)
      for (ForeignKey foreignKey : foreignKeys) {
        DatabaseTableHandler databaseTableHandler = getFactory().getRemoveForeignKeyHandler(dataShape, foreignKey.getName(), foreignKey);
        databaseTableHandlers.add(databaseTableHandler);
      }  
    return databaseTableHandlers;
  }
}
