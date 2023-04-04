package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.types.InfoTable;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class CreateDatabaseTables extends AbstractUpdateDatabaseSchema<InfoTable> {
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private DatabaseInfo databaseInfo;
  
  CreateDatabaseTables( DatabaseHandler databaseHandler, DatabaseInfo databaseInfo) {
    super(databaseHandler);
    this.databaseInfo = databaseInfo;
  }
  
  public InfoTable execute() throws Exception {
    List<DatabaseTableHandler> tableDatabaseTableHandlers = Lists.newArrayList();
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : this.databaseInfo.getDbInfo()) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      createTable(dataShape, dataShapeDatabaseInfo, tableDatabaseTableHandlers);
    } 
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    databaseTableHandlers.addAll(tableDatabaseTableHandlers);
    List<Pair<DatabaseTableHandler, QueryResult>> queryResults = getDatabaseHandler().executeList(databaseTableHandlers);
    List<DatabaseTableHandler> foreignKeyDatabaseTableHandlers = Lists.newArrayList();
    DatabaseMetaDataManager databaseMetaDataManager = getDatabaseHandler().getDatabaseMetaDataManager();
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : this.databaseInfo.getDbInfo()) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      foreignKeyDatabaseTableHandlers.addAll(createForeignKeys(dataShape, dataShapeDatabaseInfo
            .getForeignKeys(), databaseMetaDataManager));
    } 
    List<Pair<DatabaseTableHandler, QueryResult>> foreignKeyQueryResults = getDatabaseHandler().executeList(foreignKeyDatabaseTableHandlers);
    InfoTable result = InfoTableInstanceFactory.createInfoTableFromDataShape("PTC.DBConnection.DatabaseSchemaValidation");
    logResponse(queryResults, result);
    logResponse(foreignKeyQueryResults, result);
    return result;
  }
}
