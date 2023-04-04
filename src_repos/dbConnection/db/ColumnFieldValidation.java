package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.collections.ValueCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public final class ColumnFieldValidation {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ColumnFieldValidation.class);
  
  protected static final String TYPE_COLUMN = "Column";
  
  private DatabaseHandler databaseHandler;
  
  public ColumnFieldValidation(DatabaseHandler databaseHandler) {
    this.databaseHandler = databaseHandler;
  }
  
  public List<ValueCollection> validateColumnField(DataShape dataShape, String tableName, FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo, DatabaseMetaDataManager databaseMetaDataManager) {
    List<ValueCollection> vc = new ArrayList<>();
    ColumnInfo columnInfoforDataShape = createColumnInfo(this.databaseHandler, dataShape, fieldDefinition, fieldDatabaseInfo);
    vc.addAll(validateColumn(this.databaseHandler, dataShape, tableName, fieldDefinition.getName(), columnInfoforDataShape, databaseMetaDataManager, 
          
          shouldCompareLength(fieldDefinition, fieldDatabaseInfo)));
    return vc;
  }
  
  public List<ValueCollection> validateColumn(DatabaseHandler databaseHandler, DataShape dataShape, String tableName, String fieldName, ColumnInfo fieldColumnInfo, DatabaseMetaDataManager databaseMetaDataManager, boolean compareLength) {
    List<ValueCollection> unMatchedColumnInfovc = new ArrayList<>();
    Optional<ColumnInfo> foundColumnInfo = databaseMetaDataManager.getColumnInfo(tableName, fieldColumnInfo.getName());
    if (foundColumnInfo.isPresent()) {
      List<String> unmatchedColumnInfoMessages = compareColumnInfo(fieldColumnInfo, foundColumnInfo.get(), compareLength);
      if (unmatchedColumnInfoMessages != null && !unmatchedColumnInfoMessages.isEmpty())
        for (String unmatchedColumnInfoMessage : unmatchedColumnInfoMessages)
          unMatchedColumnInfovc.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape
                .getName(), tableName, fieldName, fieldColumnInfo.getName(), "Column", fieldColumnInfo
                .getName(), unmatchedColumnInfoMessage));  
    } else {
      String columnNotFoundMessage = "Column not found in the database";
      unMatchedColumnInfovc.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape
            .getName(), tableName, fieldName, fieldColumnInfo.getName(), "Column", fieldColumnInfo
            .getName(), columnNotFoundMessage));
      _logger.warn("The column " + fieldColumnInfo.getName() + " for the table " + tableName + " is not found in the database");
    } 
    return unMatchedColumnInfovc;
  }
  
  public ColumnInfo createColumnInfo(DatabaseHandler databaseHandler, DataShape dataShape, FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    String columnName = databaseHandler.getColumnName(fieldDefinition);
    boolean notNull = false;
    String columnType = "";
    int columnLength = databaseHandler.getDefaultStringLength();
    if (fieldDatabaseInfo != null) {
      notNull = fieldDatabaseInfo.isNotNull();
      if (fieldDatabaseInfo.getLength() != 0)
        columnLength = fieldDatabaseInfo.getLength(); 
    } 
    BaseTypes baseTypes = getBaseType(fieldDefinition, fieldDatabaseInfo);
    if (fieldDefinition.isPrimaryKey()) {
      DataShapeUtils dataShapeUtils = new DataShapeUtils();
      boolean isAutoIncrement = ((baseTypes.equals(BaseTypes.LONG) || baseTypes.equals(BaseTypes.INTEGER)) && !dataShapeUtils.isAdditionalPropertiesDataShape(dataShape));
      columnType = databaseHandler.getPrimaryKeySqlType(baseTypes, isAutoIncrement);
      notNull = true;
    } else {
      columnType = (String)getSqlType(baseTypes).getValue();
    } 
    return buildColumnInfoInstance(columnName, columnType, notNull, columnLength);
  }
  
  private BaseTypes getBaseType(FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    BaseTypes baseType = getBaseType(fieldDatabaseInfo);
    if (baseType == null)
      baseType = fieldDefinition.getBaseType(); 
    return baseType;
  }
  
  private BaseTypes getBaseType(FieldDatabaseInfo fieldDatabaseInfo) {
    if (fieldDatabaseInfo != null) {
      String baseTypeString = fieldDatabaseInfo.getBaseType();
      if (baseTypeString != null && !baseTypeString.isEmpty())
        return BaseTypes.valueOf(baseTypeString); 
    } 
    return null;
  }
  
  private ColumnInfo buildColumnInfoInstance(String name, String type, boolean notNull, int length) {
    return new ColumnInfo(name, type, notNull, length);
  }
  
  private List<String> compareColumnInfo(ColumnInfo columnInfo, ColumnInfo dbFoundColumnInfo, boolean compareLength) {
    List<String> unMatchedColumnMessages = new ArrayList<>();
    String columnName = columnInfo.getName();
    if (!columnName.equals(dbFoundColumnInfo.getName()))
      unMatchedColumnMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Name", columnName, dbFoundColumnInfo
            .getName())); 
    if ((!columnInfo.isNotNull()) == dbFoundColumnInfo.isNotNull())
      unMatchedColumnMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("NotNull", 
            String.valueOf(columnInfo.isNotNull()), String.valueOf(dbFoundColumnInfo.isNotNull()))); 
    if (!columnInfo.getType().equals(dbFoundColumnInfo.getType()))
      unMatchedColumnMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Type", columnInfo
            .getType(), dbFoundColumnInfo.getType())); 
    if (compareLength && columnInfo.getLength() != dbFoundColumnInfo.getLength())
      unMatchedColumnMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Length", 
            String.valueOf(columnInfo.getLength()), String.valueOf(dbFoundColumnInfo.getLength()))); 
    if (unMatchedColumnMessages.size() > 0)
      _logger.warn("The property values for column " + columnName + " are not same as in the database"); 
    return unMatchedColumnMessages;
  }
  
  boolean shouldCompareLength(FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    int sqlTypeKey = ((Integer)getSqlType(getBaseType(fieldDefinition, fieldDatabaseInfo)).getKey()).intValue();
    return (12 == sqlTypeKey || -9 == sqlTypeKey);
  }
  
  Pair<Integer, String> getSqlType(BaseTypes baseType) {
    return this.databaseHandler.getSqlType(baseType).get();
  }
}
