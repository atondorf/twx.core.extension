package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

public class ValidateForeignKey {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ValidateForeignKey.class);
  
  protected static final String TYPE_FOREIGN_KEY = "ForeignKey";
  
  public static List<ValueCollection> validateForeignKeys(DatabaseHandler databaseHandler, DataShape dataShape, String tableName, List<ForeignKey> foreignKeyList, DatabaseMetaDataManager databaseMetaDataManager) {
    List<ValueCollection> foreignKeyVcList = new ArrayList<>();
    foreignKeyList.forEach(foreignKey -> foreignKeyVcList.addAll(validateForeignKey(databaseHandler, dataShape, tableName, foreignKey, databaseMetaDataManager)));
    return foreignKeyVcList;
  }
  
  public static List<ValueCollection> validateForeignKey(DatabaseHandler databaseHandler, DataShape dataShape, String tableName, ForeignKey foreignKey, DatabaseMetaDataManager databaseMetaDataManager) {
    List<ValueCollection> foreignKeyErrorMsg = new ArrayList<>();
    try {
      ForeignKeyInfo foreignKeyInfo = createForeignKeyInfo(databaseHandler, dataShape, foreignKey);
      Optional<ForeignKeyInfo> foundForeignKey = databaseMetaDataManager.getForeignKeyInfo(tableName, foreignKeyInfo.getName());
      if (foundForeignKey.isPresent()) {
        ForeignKeyInfo dbForeignKeyInfo = foundForeignKey.get();
        List<String> unmatchedForeignKeyInfoMessages = compareForeignKeyInfo(foreignKeyInfo, dbForeignKeyInfo);
        if (CollectionUtils.isNotEmpty(unmatchedForeignKeyInfoMessages))
          unmatchedForeignKeyInfoMessages.forEach(unmatchedForeignKeyInfoMessage -> foreignKeyErrorMsg.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, foreignKey.getName(), foreignKeyInfo.getColumnName(), "ForeignKey", foreignKeyInfo.getName(), unmatchedForeignKeyInfoMessage))); 
      } else {
        foreignKeyErrorMsg.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape
              .getName(), tableName, foreignKey.getName(), foreignKeyInfo.getColumnName(), "ForeignKey", foreignKeyInfo
              .getName(), "Foreign key not found in the database"));
        _logger.warn("The foreign Key " + foreignKeyInfo.getName() + " for the table " + tableName + " which is referenced to table " + foreignKeyInfo
            .getReferencedTable() + " is not found in the database");
      } 
    } catch (Exception e) {
      String fieldName = foreignKey.getName();
      FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName);
      String identifierName = databaseHandler.getForeignKeyName(dataShape, fieldDefinition, foreignKey, false);
      String columnName = databaseHandler.getColumnName(fieldDefinition);
      foreignKeyErrorMsg
        .add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, fieldName, columnName, "ForeignKey", identifierName, e
            .getLocalizedMessage()));
    } 
    return foreignKeyErrorMsg;
  }
  
  private static ForeignKeyInfo createForeignKeyInfo(DatabaseHandler databaseHandler, DataShape dataShape, ForeignKey foreignKey) {
    FieldDefinition fieldDefinition = dataShape.getFieldDefinition(foreignKey.getName());
    String foreignKeyName = databaseHandler.getForeignKeyName(dataShape, fieldDefinition, foreignKey);
    DataShapeUtils dataShapeUtils = new DataShapeUtils();
    DataShape referenceDataShape = dataShapeUtils.getDataShape(foreignKey.getReferenceDataShapeName());
    String columnName = databaseHandler.getColumnName(fieldDefinition);
    String referenceTableName = databaseHandler.getTableName(referenceDataShape);
    FieldDefinition referenceFieldDefinition = referenceDataShape.getFieldDefinition(foreignKey.getReferenceFieldName());
    String referenceColumnName = databaseHandler.getColumnName(referenceFieldDefinition);
    return new ForeignKeyInfo(foreignKeyName, columnName, referenceTableName, referenceColumnName);
  }
  
  private static List<String> compareForeignKeyInfo(ForeignKeyInfo foreignKeyInfo, ForeignKeyInfo dbForeignKeyInfo) {
    List<String> unMatchedForeignKeyMessages = new ArrayList<>();
    String foreignKeyName = foreignKeyInfo.getName();
    if (!foreignKeyInfo.getName().equals(dbForeignKeyInfo.getName()))
      unMatchedForeignKeyMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Name", foreignKeyInfo
            .getName(), dbForeignKeyInfo.getName())); 
    if (!foreignKeyInfo.getColumnName().equals(dbForeignKeyInfo.getColumnName()))
      unMatchedForeignKeyMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("ColumnName", foreignKeyInfo
            .getColumnName(), dbForeignKeyInfo.getColumnName())); 
    if (!foreignKeyInfo.getReferencedColumn().equals(dbForeignKeyInfo.getReferencedColumn()))
      unMatchedForeignKeyMessages
        .add(DatabaseSchemaValidationHelper.getValidationMessage("ReferencedColumn", foreignKeyInfo
            .getReferencedColumn(), dbForeignKeyInfo.getReferencedColumn())); 
    if (!foreignKeyInfo.getReferencedTable().equals(dbForeignKeyInfo.getReferencedTable()))
      unMatchedForeignKeyMessages
        .add(DatabaseSchemaValidationHelper.getValidationMessage("ReferencedTable", foreignKeyInfo
            .getReferencedTable(), dbForeignKeyInfo.getReferencedTable())); 
    if (unMatchedForeignKeyMessages.size() > 0)
      _logger.warn("The property values for foreign Key  " + foreignKeyName + " are not same as in the database"); 
    return unMatchedForeignKeyMessages;
  }
}
