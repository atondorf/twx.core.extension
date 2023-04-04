package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.InfoTable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.tuple.Pair;

public class UpdateDatabaseSchema extends AbstractUpdateDatabaseSchema<InfoTable> {
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private DatabaseInfo databaseInfo;
  
  UpdateDatabaseSchema( DatabaseHandler databaseHandler, DatabaseInfo databaseInfo) {
    super(databaseHandler);
    this.databaseInfo = databaseInfo;
  }
  
  public InfoTable execute() throws Exception {
    List<DatabaseTableHandler> tableDatabaseTableHandlers = Lists.newArrayList();
    DatabaseMetaDataManager databaseMetaDataManager = getDatabaseHandler().getDatabaseMetaDataManager();
    List<DataShapeDatabaseInfo> dataShapeDatabaseInfoList = this.databaseInfo.getDbInfo();
    if (dataShapeDatabaseInfoList == null)
      return 
        InfoTableInstanceFactory.createInfoTableFromDataShape("PTC.DBConnection.DatabaseSchemaValidation"); 
    Set<String> tableToCreate = Sets.newHashSet(new String[0]);
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : dataShapeDatabaseInfoList) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      String tableName = getDatabaseHandler().getTableName(dataShape);
      if (getDatabaseHandler().tableExist(dataShape, databaseMetaDataManager) || tableToCreate
        .contains(tableName)) {
        updateTable(dataShape, dataShapeDatabaseInfo, databaseMetaDataManager, tableDatabaseTableHandlers);
        continue;
      } 
      createTable(dataShape, dataShapeDatabaseInfo, tableDatabaseTableHandlers);
      tableToCreate.add(tableName);
    } 
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    databaseTableHandlers.addAll(tableDatabaseTableHandlers);
    List<Pair<DatabaseTableHandler, QueryResult>> queryResults = getDatabaseHandler().executeList(databaseTableHandlers);
    databaseMetaDataManager = getDatabaseHandler().getDatabaseMetaDataManager();
    List<DatabaseTableHandler> foreignKeyDatabaseTableHandlers = Lists.newArrayList();
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : dataShapeDatabaseInfoList) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      foreignKeyDatabaseTableHandlers.addAll(createForeignKeys(dataShape, dataShapeDatabaseInfo
            .getForeignKeys(), databaseMetaDataManager));
    } 
    List<Pair<DatabaseTableHandler, QueryResult>> foreignKeyQueryResults = getDatabaseHandler().executeList(foreignKeyDatabaseTableHandlers);
    InfoTable updatedSchemaInfo = InfoTableInstanceFactory.createInfoTableFromDataShape("PTC.DBConnection.DatabaseSchemaValidation");
    logResponse(queryResults, updatedSchemaInfo);
    logResponse(foreignKeyQueryResults, updatedSchemaInfo);
    return updatedSchemaInfo;
  }
  
  private void updateTable( DataShape dataShape,  DataShapeDatabaseInfo dataShapeDatabaseInfo,  DatabaseMetaDataManager databaseMetaDataManager,  List<DatabaseTableHandler> tableDatabaseTableHandlers) {
    for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
      if (fieldDefinition.isPrimaryKey() || getDatabaseHandler().columnExist(dataShape, fieldDefinition, databaseMetaDataManager))
        continue; 
      Optional<FieldDatabaseInfo> found = dataShapeDatabaseInfo.getFieldDatabaseInfo(fieldDefinition);
      DatabaseTableHandler databaseTableHandler = getFactory().getAddColumnHandler(dataShape, fieldDefinition
          .getName(), found.orElse(null));
      tableDatabaseTableHandlers.add(databaseTableHandler);
    } 
    tableDatabaseTableHandlers.addAll(createIndexes(dataShape, dataShapeDatabaseInfo
          .getIndexedFields(), databaseMetaDataManager));
  }
}
