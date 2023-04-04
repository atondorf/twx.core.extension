package twx.core.db;

import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import java.util.List;
import java.util.Optional;

public class ValidateDatabaseSchema {
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  public static final String DATASHAPE_NAME_DATABASE_SCHEMA_VALIDATION = "PTC.DBConnection.DatabaseSchemaValidation";
  
  protected static final String TABLE_ENTITY_TYPE = "Table";
  
  protected InfoTable databaseSchemaValidation(DatabaseInfo databaseInfo, DatabaseMetaDataManager databaseMetaDataManager, DatabaseHandler databaseHandler) throws Exception {
    InfoTable schemaValidationInfoTable = InfoTableInstanceFactory.createInfoTableFromDataShape("PTC.DBConnection.DatabaseSchemaValidation");
    for (DataShapeDatabaseInfo dataShapeDatabaseInfo : databaseInfo.getDbInfo()) {
      DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeDatabaseInfo.getDataShapeName());
      String tableName = databaseHandler.getTableName(dataShape);
      if (!databaseHandler.tableExist(dataShape, databaseMetaDataManager))
        schemaValidationInfoTable.addRow(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape
              .getName(), tableName, "Table", "Table not found in the database")); 
      List<ForeignKey> listOfForeignKeys = dataShapeDatabaseInfo.getForeignKeys();
      if (listOfForeignKeys != null) {
        ValidateForeignKey validateForeignKey = new ValidateForeignKey();
        List<ValueCollection> foreignKeysValidationInfo = ValidateForeignKey.validateForeignKeys(databaseHandler, dataShape, tableName, listOfForeignKeys, databaseMetaDataManager);
        foreignKeysValidationInfo.forEach(vc -> schemaValidationInfoTable.addRow(vc));
      } 
      List<Index> listOfIndexes = dataShapeDatabaseInfo.getIndexedFields();
      if (listOfIndexes != null) {
        IndexValidation indexValidation = new IndexValidation();
        List<ValueCollection> indexesValidationInfo = indexValidation.validateIndexes(databaseHandler, dataShape, tableName, dataShapeDatabaseInfo
            .getIndexedFields(), databaseMetaDataManager);
        indexesValidationInfo.forEach(vc -> schemaValidationInfoTable.addRow(vc));
      } 
      for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
        ColumnFieldValidation columnFieldValidation = new ColumnFieldValidation(databaseHandler);
        Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = dataShapeDatabaseInfo.getFieldDatabaseInfo(fieldDefinition);
        List<ValueCollection> columnsValidationInfo = columnFieldValidation.validateColumnField(dataShape, tableName, fieldDefinition, foundFieldDatabaseInfo
            .orElse(null), databaseMetaDataManager);
        columnsValidationInfo.forEach(vc -> schemaValidationInfoTable.addRow(vc));
      } 
    } 
    return schemaValidationInfoTable;
  }
}
