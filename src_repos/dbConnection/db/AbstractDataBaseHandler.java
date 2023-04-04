package twx.core.db;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

abstract class AbstractDataBaseHandler implements DatabaseHandler {
  Map<BaseTypes, DataTypeConverter> dataTypeConverterMap = Maps.newHashMap();
  
  private DatabaseTableHandlerFactory databaseTableHandlerFactory = null;
  
  private TransactionManager transactionManager = new TransactionManager();
  
  private DatabaseInfoManager databaseInfoManager = DatabaseInfoManager.getInstance();
  
  AbstractDataBaseHandler() {
    this.dataTypeConverterMap.put(BaseTypes.DATETIME, new TimestampDataTypeConverter());
  }
  
  private static final DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  public Optional<DataTypeConverter> getDataTypeConverter(BaseTypes baseTypes) {
    DataTypeConverter dataTypeConverter = this.dataTypeConverterMap.get(baseTypes);
    return Optional.ofNullable(dataTypeConverter);
  }
  
  public Optional<Integer> getSqlTypeKey(BaseTypes baseType) {
    if (getSqlTypeMap() == null)
      return Optional.empty(); 
    return Optional.ofNullable(getSqlTypeMap().get(baseType));
  }
  
  public DatabaseTableHandlerFactory getDatabaseTableHandlerFactory() {
    if (this.databaseTableHandlerFactory == null)
      this.databaseTableHandlerFactory = createDatabaseTableHandlerFactory(); 
    return this.databaseTableHandlerFactory;
  }
  
  public String getPrimaryKeySqlType(BaseTypes baseType, boolean autoIncrement) {
    Optional<Integer> sqlTypeFound = getSqlTypeKey(baseType);
    if (sqlTypeFound.isPresent()) {
      Integer sqlType = sqlTypeFound.get();
      if ((sqlType.intValue() == -5 || sqlType.intValue() == 4) && autoIncrement)
        return getAutoIncrementType(sqlType); 
    } 
    Optional<Pair<Integer, String>> foundSqlType = getSqlType(baseType);
    if (foundSqlType.isPresent())
      return (String)((Pair)foundSqlType.get()).getValue(); 
    throw new ThingworxRuntimeException("Primary key type not supported:" + baseType);
  }
  
  public Optional<Pair<Integer, String>> getSqlType(BaseTypes baseType) {
    Optional<Integer> found = getSqlTypeKey(baseType);
    if (found.isPresent()) {
      Map<Integer, String> typeMap = getTypeMap();
      Integer sqlType = found.get();
      String sqlTypeString = typeMap.get(sqlType);
      if (sqlTypeString != null)
        return Optional.of(Pair.of(sqlType, typeMap.get(sqlType))); 
    } 
    return Optional.empty();
  }
  
  void buildTypeMap(Map<Integer, String> typeMap, Set<String> supportedSqlType) {
    typeMap.putAll(getDatabaseMetaDataManager().buildTypeMap(supportedSqlType, this.transactionManager
          .getDatabaseConnectionManager()));
  }
  
  public String getTableName(DataShape dataShape) {
    return CommonHelper.getLowerLastString(dataShape.getName(), Character.valueOf('.'));
  }
  
  public String getColumnName(FieldDefinition fieldDefinition) {
    return fieldDefinition.getName().toLowerCase();
  }
  
  public int getDefaultStringLength() {
    return 255;
  }
  
  public String getIndexName(DataShape dataShape, Index index, boolean validate) {
    String indexName = populateIndexName(dataShape, index);
    if (validate)
      validateName("Index", indexName, getNameMaxChar()); 
    return indexName.toLowerCase();
  }
  

  private String populateIndexName(DataShape dataShape, Index index) {
    if (StringUtils.isNotEmpty(index.getIdentifier()))
      return index.getIdentifier(); 
    List<String> fieldNames = index.getFieldNames();
    if (CollectionUtils.isEmpty(fieldNames))
      fieldNames = Lists.newArrayList(new String[] { index.getName() }); 
    List<FieldDefinition> fieldDefinitions = (List<FieldDefinition>)fieldNames.stream().map(fieldName -> (FieldDefinition)getFieldDefinition(dataShape, fieldName).get()).collect(Collectors.toList());
    return getName(dataShape, fieldDefinitions, "idx");
  }
  
  public String getForeignKeyName(DataShape dataShape, FieldDefinition fieldDefinition, ForeignKey foreignKey, boolean validate) {
    String foreignKeyName = foreignKey.getIdentifier();
    if (foreignKeyName == null)
      foreignKeyName = getForeignKeyName(dataShape, fieldDefinition); 
    if (validate)
      validateName("Foreign Key", foreignKeyName, getNameMaxChar()); 
    return foreignKeyName.toLowerCase();
  }
  

  private String getForeignKeyName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return getName(dataShape, fieldDefinition, "fk");
  }
  
  private void validateName(String constraintType, String name, int maxChar) {
    if (name.startsWith(" ") || name.endsWith(" "))
      throw new ThingworxRuntimeException(
          String.format("%s [%s] cannot have leading or trailing spaces.", new Object[] { constraintType, name })); 
    if (name.length() > maxChar)
      throw new ThingworxRuntimeException(String.format("%s [%s] is too long. The maximum is %d characters.", new Object[] { constraintType, name, 
              Integer.valueOf(maxChar) })); 
  }
  
  public String getNotNullConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return getName(fieldDefinition, "ck");
  }
  
  public String getUniqueConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return getName(fieldDefinition, "uc");
  }
  
  public String getDefaultConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return getName(dataShape, fieldDefinition, "dc");
  }
  

  private String getName(DataShape dataShape, FieldDefinition fieldDefinition, String postFix) {
    return getName(dataShape, Arrays.asList( new FieldDefinition[] { fieldDefinition } ), postFix);
  }
  

  private String getName(DataShape dataShape, List<FieldDefinition> fieldDefinitions, String postFix) {
    StringBuilder sb = new StringBuilder();
    sb.append(getTableName(dataShape)).append('_');
    String columnString = fieldDefinitions.stream().map(fd -> getColumnName(fd)).collect(Collectors.joining("_"));
    sb.append(columnString).append('_').append(postFix);
    return sb.toString().toLowerCase();
  }
  

  private String getName(FieldDefinition fieldDefinition, String postFix) {
    String name = getColumnName(fieldDefinition) + "_" + getColumnName(fieldDefinition);
    return name.toLowerCase();
  }
  
  public DatabaseMetaDataManager getDatabaseMetaDataManager() {
    return DatabaseMetaDataManager.getInstance();
  }
  
  public DatabaseInfoManager getDatabaseInfoManager() {
    return this.databaseInfoManager;
  }
  
  public QueryResult execute(DatabaseTableHandler databaseTableHandler) {
    return this.transactionManager.execute(databaseTableHandler);
  }
  
  public List<Pair<DatabaseTableHandler, QueryResult>> executeList(List<DatabaseTableHandler> databaseTableHandlers) {
    return this.transactionManager.executeList(databaseTableHandlers);
  }
  
  public boolean tableExist(DataShape dataShape, DatabaseMetaDataManager databaseMetaDataManager) {
    return (databaseMetaDataManager != null && databaseMetaDataManager
      .tableExist(getTableName(dataShape)));
  }
  
  public boolean columnExist(DataShape dataShape, FieldDefinition fieldDefinition, DatabaseMetaDataManager databaseMetaDataManager) {
    return (databaseMetaDataManager != null && databaseMetaDataManager
      .columnExist(getTableName(dataShape), getColumnName(fieldDefinition)));
  }
  
  public boolean indexExist(DataShape dataShape, Index index, DatabaseMetaDataManager databaseMetaDataManager) {
    return (databaseMetaDataManager != null && databaseMetaDataManager
      .indexExist(getTableName(dataShape), getIndexName(dataShape, index, false)));
  }
  
  public boolean foreignKeyExist(DataShape dataShape, FieldDefinition fieldDefinition, ForeignKey foreignKey, DatabaseMetaDataManager databaseMetaDataManager) {
    return (databaseMetaDataManager != null && databaseMetaDataManager.foreignKeyExist(
        getTableName(dataShape), getForeignKeyName(dataShape, fieldDefinition, foreignKey, false)));
  }
  
  private Optional<FieldDefinition> getFieldDefinition(DataShape dataShape, String fieldName) {
    return dataShapeUtils.getFieldDefinition(dataShape, fieldName);
  }
  
  public boolean isNotNull(DataShape dataShape, FieldDefinition fieldDefinition, DatabaseMetaDataManager databaseMetaDataManager) {
    Optional<ColumnInfo> columnInfo = databaseMetaDataManager.getColumnInfo(getTableName(dataShape), 
        getColumnName(fieldDefinition));
    return ((Boolean)columnInfo.<Boolean>map(ColumnInfo::isNotNull).orElse(Boolean.valueOf(false))).booleanValue();
  }
  
  public boolean isUnique(DataShape dataShape, FieldDefinition fieldDefinition, DatabaseMetaDataManager databaseMetaDataManager) {
    return (databaseMetaDataManager != null && databaseMetaDataManager
      .indexExist(getTableName(dataShape), getUniqueConstraintName(dataShape, fieldDefinition)));
  }
  
  public void startTransaction() throws Exception {
    this.transactionManager.startTransaction();
  }
  
  public void commitTransaction() {
    this.transactionManager.commitTransaction();
  }
  
  public void rollbackTransaction() {
    this.transactionManager.rollbackTransaction();
  }
  
  public void closeTransaction() {
    this.transactionManager.closeTransaction();
  }
  
  public void startTransactionLookUp() {
    this.transactionManager.startTransactionLookUp();
  }
  
  public void stopTransactionLookup() {
    this.transactionManager.stopTransactionLookUp();
  }
  
  public <T> T executeHandler(ExecuteHandler<T> executeHandler) {
    return this.transactionManager.executeHandler(executeHandler);
  }
  
  protected abstract DatabaseTableHandlerFactory createDatabaseTableHandlerFactory();
  
  protected abstract String getAutoIncrementType(Integer paramInteger);
  
  protected abstract Map<Integer, String> getTypeMap();
  
  protected abstract Map<BaseTypes, Integer> getSqlTypeMap();
  
  abstract int getNameMaxChar();
}
