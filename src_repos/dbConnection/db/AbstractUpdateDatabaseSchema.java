package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.InfoTable;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

abstract class AbstractUpdateDatabaseSchema<T> extends AbstractExecuteHandler<T> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(UpdateDatabaseSchema.class);
  
  private static final String CHECK_LOGS = "See the ApplicationLog for more details.";
  
  AbstractUpdateDatabaseSchema( DatabaseHandler databaseHandler) {
    super(databaseHandler);
  }
  
  void createTable(DataShape dataShape, DataShapeDatabaseInfo dataShapeDatabaseInfo, List<DatabaseTableHandler> tableDatabaseTableHandlers) {
    DatabaseTableHandler databaseTableHandler = getFactory().getCreateTableHandler(dataShape, dataShapeDatabaseInfo);
    tableDatabaseTableHandlers.add(databaseTableHandler);
    tableDatabaseTableHandlers
      .addAll(createIndexes(dataShape, dataShapeDatabaseInfo.getIndexedFields(), (DatabaseMetaDataManager)null));
  }
  
  List<DatabaseTableHandler> createIndexes(DataShape dataShape, List<Index> indexes, DatabaseMetaDataManager databaseMetaDataManager) {
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    if (indexes != null)
      for (Index index : indexes) {
        if (getDatabaseHandler().indexExist(dataShape, index, databaseMetaDataManager))
          continue; 
        databaseTableHandlers.add(getFactory().getCreateIndexHandler(dataShape, index));
      }  
    return databaseTableHandlers;
  }
  
  List<DatabaseTableHandler> createForeignKeys(DataShape dataShape, List<ForeignKey> foreignKeys, DatabaseMetaDataManager databaseMetaDataManager) {
    List<DatabaseTableHandler> databaseTableHandlers = Lists.newArrayList();
    if (foreignKeys != null)
      for (ForeignKey foreignKey : foreignKeys) {
        if (getDatabaseHandler().foreignKeyExist(dataShape, dataShape
            .getFieldDefinition(foreignKey.getName()), foreignKey, databaseMetaDataManager))
          continue; 
        Optional<DataShape> foundDataShape = this.dataShapeUtils.findDataShape(foreignKey.getReferenceDataShapeName());
        if (foundDataShape.isPresent()) {
          DataShape referenceDataShape = foundDataShape.get();
          if (getDatabaseHandler().tableExist(referenceDataShape, databaseMetaDataManager)) {
            if (getDatabaseHandler().columnExist(referenceDataShape, referenceDataShape
                .getFieldDefinition(foreignKey.getReferenceFieldName()), databaseMetaDataManager)) {
              databaseTableHandlers.add(getFactory().getAddForeignKeyHandler(dataShape, foreignKey
                    .getName(), foreignKey));
              continue;
            } 
            _logger.warn("Referenced Column does not exist on table {}: {}", 
                getDatabaseHandler().getTableName(referenceDataShape), foreignKey
                .getReferenceFieldName());
            continue;
          } 
          _logger.warn("Table does not exist:" + 
              getDatabaseHandler().getTableName(referenceDataShape));
          continue;
        } 
        _logger.warn("Reference datashape does not exist:" + foreignKey
            .getReferenceDataShapeName());
      }  
    return databaseTableHandlers;
  }
  
  protected void logResponse(List<Pair<DatabaseTableHandler, QueryResult>> queryResults, InfoTable updatedSchemaInfo) {
    for (Pair<DatabaseTableHandler, QueryResult> queryResult : queryResults)
      logResponseToInfoTable((DatabaseTableHandler)queryResult.getKey(), ((QueryResult)queryResult.getValue()).isSuccessful(), updatedSchemaInfo); 
  }
  
  private void logResponseToInfoTable(DatabaseTableHandler databaseTableHandler, boolean successful, InfoTable updatedSchemaInfo) {
    if (databaseTableHandler instanceof DatabaseCreateIndexTableHandler) {
      responseForCreatedIndex((DatabaseCreateIndexTableHandler)databaseTableHandler, successful, updatedSchemaInfo);
    } else if (databaseTableHandler instanceof AddColumnTableHandler) {
      responseForCreatedColumn((AddColumnTableHandler)databaseTableHandler, successful, updatedSchemaInfo);
    } else if (databaseTableHandler instanceof DatabaseCreateTableHandler) {
      responseForCreatedTable((DatabaseCreateTableHandler)databaseTableHandler, successful, updatedSchemaInfo);
    } else if (databaseTableHandler instanceof DatabaseAddForeignKeyTableHandler) {
      responseForAddedForeignKey((DatabaseAddForeignKeyTableHandler)databaseTableHandler, successful, updatedSchemaInfo);
    } else {
      _logger.info("Unknown handler for the update schema operation");
    } 
  }
  
  private void responseForCreatedTable(DatabaseCreateTableHandler databaseCreateTableHandler, boolean successful, InfoTable updatedSchemaInfo) {
    DataShape dataShape = databaseCreateTableHandler.getDataShape();
    String tableName = databaseCreateTableHandler.getTableName(dataShape);
    String datashapeName = dataShape.getName();
    if (successful) {
      addCreateTableResponse(tableName, datashapeName, "Table created.", updatedSchemaInfo);
      FieldDefinitionCollection fieldDefinitionCollection = dataShape.getFields();
      for (String fieldName : fieldDefinitionCollection.keySet()) {
        String columnName = databaseCreateTableHandler.getColumnName(databaseCreateTableHandler
            .getFieldDefinition(dataShape, fieldName).get());
        addColumnResponse(tableName, datashapeName, fieldName, columnName, "Column created.", updatedSchemaInfo);
      } 
    } else {
      addCreateTableResponse(tableName, datashapeName, "Table not created. See the ApplicationLog for more details.", updatedSchemaInfo);
    } 
  }
  
  private void responseForCreatedColumn(AddColumnTableHandler addColumnTableHandler, boolean successful, InfoTable updatedSchemaInfo) {
    DataShape dataShape = addColumnTableHandler.getDataShape();
    String fieldName = addColumnTableHandler.getFieldName();
    FieldDefinition fieldDefinition = addColumnTableHandler.getFieldDefinition(dataShape, fieldName).get();
    String columnName = addColumnTableHandler.getColumnName(fieldDefinition);
    String tableName = addColumnTableHandler.getTableName(dataShape);
    if (successful) {
      addColumnResponse(tableName, dataShape.getName(), fieldName, columnName, "Column created.", updatedSchemaInfo);
    } else {
      addColumnResponse(tableName, dataShape.getName(), fieldName, columnName, "Column not created. See the ApplicationLog for more details.", updatedSchemaInfo);
    } 
  }
  
  private void responseForAddedForeignKey(DatabaseAddForeignKeyTableHandler addForeignKeyTableHandler, boolean successful, InfoTable updatedSchemaInfo) {
    if (successful) {
      addForeignKeyResponse(addForeignKeyTableHandler, "ForeignKey created.", updatedSchemaInfo);
    } else {
      addForeignKeyResponse(addForeignKeyTableHandler, "ForeignKey not created. See the ApplicationLog for more details.", updatedSchemaInfo);
    } 
  }
  
  private void responseForCreatedIndex(DatabaseCreateIndexTableHandler createIndexTableHandler, boolean successful, InfoTable updatedSchemaInfo) {
    if (successful) {
      addIndexResponse(createIndexTableHandler, "Index created.", updatedSchemaInfo);
    } else {
      addIndexResponse(createIndexTableHandler, "Index not created. See the ApplicationLog for more details.", updatedSchemaInfo);
    } 
  }
  
  private void addForeignKeyResponse(DatabaseAddForeignKeyTableHandler addForeignKeyTableHandler, String message, InfoTable updatedSchemaInfo) {
    DataShape dataShape = addForeignKeyTableHandler.getDataShape();
    String fieldName = addForeignKeyTableHandler.getFieldName();
    FieldDefinition fieldDefinition = addForeignKeyTableHandler.getFieldDefinition(dataShape, fieldName).get();
    ForeignKey foreignKey = addForeignKeyTableHandler.getForeignKey();
    String foreignKeyName = getDatabaseHandler().getForeignKeyName(dataShape, fieldDefinition, foreignKey, false);
    updatedSchemaInfo.addRow(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), addForeignKeyTableHandler
          .getTableName(dataShape), fieldName, addForeignKeyTableHandler
          .getColumnName(fieldDefinition), "ForeignKey", foreignKeyName, message));
  }
  
  private void addIndexResponse(DatabaseCreateIndexTableHandler createIndexTableHandler, String message, InfoTable updatedSchemaInfo) {
    DataShape dataShape = createIndexTableHandler.getDataShape();
    List<String> fieldNames = createIndexTableHandler.getFieldNames();
    List<FieldDefinition> fieldDefinitions = createIndexTableHandler.getFieldDefinitions(dataShape, fieldNames);
    String indexName = getDatabaseHandler().getIndexName(dataShape, createIndexTableHandler.getIndex());
    String tableName = createIndexTableHandler.getTableName(dataShape);
    updatedSchemaInfo.addRow(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, fieldNames
          .toString(), createIndexTableHandler
          .getColumnNames(fieldDefinitions).toString(), "Index", indexName, message));
  }
  
  private void addColumnResponse(String tableName, String datashapeName, String fieldName, String columnName, String message, InfoTable updatedSchemaInfo) {
    updatedSchemaInfo.addRow(DatabaseSchemaValidationHelper.buildValidationInfo(datashapeName, tableName, fieldName, columnName, "Column", columnName, message));
  }
  
  private void addCreateTableResponse(String tableName, String datashapeName, String message, InfoTable updatedSchemaInfo) {
    updatedSchemaInfo
      .addRow(DatabaseSchemaValidationHelper.buildValidationInfo(datashapeName, tableName, "", "", "Table", "", message));
  }
}
