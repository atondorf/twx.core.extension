package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractDatabaseTableHandler implements DatabaseTableHandler {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(AbstractDatabaseTableHandler.class);
  
  private static final String GENERATED_KEYS = "GENERATED_KEYS";
  
  private static final String DATASHAPE_NAME_FIELD_NAME = "DataShapeName";
  
  private static final String DATASHAPE_NAME_COLUMN_NAME = "datashapename";
  
  protected DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private InfoTableUtils infoTableUtils = new InfoTableUtils();
  
  private DataShapeDatabaseInfo dataShapeDatabaseInfo;
  
  private DataShape dataShape;
  
  private DatabaseHandler databaseHandler;
  
  private Pair<String, DataShape> statement;
  
  private DataShape dataShapeAp;
  
  private boolean isAdditionalPropertiesDataShape;
  
  private boolean isPrimaryKeyAutoGenerated;
  
  private FieldDefinition primaryKeyFieldDefinition;
  
  protected AbstractDatabaseTableHandler( DataShape dataShape,  DatabaseHandler databaseHandler) {
    Validate.notNull(dataShape);
    this.dataShape = dataShape;
    Validate.notNull(databaseHandler);
    this.databaseHandler = databaseHandler;
    this.dataShapeDatabaseInfo = getDataShapeDatabaseInfo(dataShape.getName());
    Optional<DataShape> dataShapeApFound = this.dataShapeUtils.getDataShapeAp(dataShape);
    dataShapeApFound.ifPresent(dataShape1 -> this.dataShapeAp = dataShape1);
    this.isAdditionalPropertiesDataShape = this.dataShapeUtils.isAdditionalPropertiesDataShape(dataShape);
    this.primaryKeyFieldDefinition = getPrimaryKey(dataShape);
    if (!this.isAdditionalPropertiesDataShape) {
      BaseTypes baseTypes = getBaseType(this.primaryKeyFieldDefinition);
      this
        .isPrimaryKeyAutoGenerated = (baseTypes.equals(BaseTypes.LONG) || baseTypes.equals(BaseTypes.INTEGER));
    } 
  }
  
  DataShape getDataShape() {
    return this.dataShape;
  }
  
  Optional<DataShape> getDataShapeAP() {
    return Optional.ofNullable(this.dataShapeAp);
  }
  
  boolean isAdditionalPropertiesDataShape() {
    return this.isAdditionalPropertiesDataShape;
  }
  
  private Pair<String, DataShape> getStatement() {
    if (this.statement == null) {
      String statementString = createStatement(this.dataShape);
      if (statementString != null)
        this.statement = Pair.of(statementString, this.dataShape); 
    } 
    return this.statement;
  }
  
  JdbcQuery getJdbcQuery() {
    return createJdbcQuery(this.dataShape, this.databaseHandler);
  }
  
  protected JdbcQuery createJdbcQuery(DataShape dataShape, DatabaseHandler databaseHandler) {
    return null;
  }
  
  protected void setParameter(PreparedStatement preparedStatement, DataShape dataShape) throws SQLException {}
  
  public InfoTable buildInfoTable(QueryResult queryResult) throws Exception {
    return buildInfoTableFromQueryResult(queryResult);
  }
  
  protected InfoTable buildInfoTableFromQueryResult(QueryResult queryResult) throws Exception {
    return this.infoTableUtils.buildInfoTableFromQueryResult(this.dataShape, queryResult, this.databaseHandler);
  }
  
  protected InfoTable buildInfoTableFromQueryWithSelect(Select select, QueryResult queryResult) throws Exception {
    return this.infoTableUtils.buildInfoTableFromQueryWithSelect(select, queryResult, this.databaseHandler);
  }
  
  protected String getTableName(DataShape dataShape) {
    return this.databaseHandler.getTableName(dataShape);
  }
  
  String getColumnName(FieldDefinition fieldDefinition) {
    return this.databaseHandler.getColumnName(fieldDefinition);
  }
  
  String getIndexName(DataShape dataShape, Index index) {
    return this.databaseHandler.getIndexName(dataShape, index);
  }
  
  String getForeignKeyName(DataShape dataShape, FieldDefinition fieldDefinition, ForeignKey foreignKey) {
    return this.databaseHandler.getForeignKeyName(dataShape, fieldDefinition, foreignKey);
  }
  
  String getNotNullConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return this.databaseHandler.getNotNullConstraintName(dataShape, fieldDefinition);
  }
  
  String getUniqueConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return this.databaseHandler.getUniqueConstraintName(dataShape, fieldDefinition);
  }
  
  Optional<Pair<Integer, String>> getSqlType(FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    return this.databaseHandler.getSqlType(getBaseType(fieldDefinition, fieldDatabaseInfo));
  }
  
  private BaseTypes getBaseType(FieldDefinition fieldDefinition) {
    FieldDatabaseInfo fieldDatabaseInfo = null;
    Optional<FieldDatabaseInfo> foundField = getFieldDatabaseInfo(fieldDefinition.getName());
    if (foundField.isPresent())
      fieldDatabaseInfo = foundField.get(); 
    return getBaseType(fieldDefinition, fieldDatabaseInfo);
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
  
  Optional<Integer> getSqlTypeKey(FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    BaseTypes baseType = getBaseType(fieldDefinition, fieldDatabaseInfo);
    return this.databaseHandler.getSqlTypeKey(baseType);
  }
  
  String getDefaultConstraintName(DataShape dataShape, FieldDefinition fieldDefinition) {
    return this.databaseHandler.getDefaultConstraintName(dataShape, fieldDefinition);
  }
  
  String getPrimaryKeySqlType(FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    if (!fieldDefinition.isPrimaryKey())
      throw new ThingworxRuntimeException("Field " + fieldDefinition
          .getName() + " is not primary Key"); 
    BaseTypes baseType = getBaseType(fieldDefinition, fieldDatabaseInfo);
    return this.databaseHandler.getPrimaryKeySqlType(baseType, isPrimaryKeyAutoGenerated());
  }
  
  Optional<FieldDefinition> getFieldDefinition(DataShape dataShape, String fieldName) {
    return this.dataShapeUtils.getFieldDefinition(dataShape, fieldName);
  }
  
  FieldDefinition getPrimaryKey(DataShape dataShape) {
    if (this.primaryKeyFieldDefinition == null)
      this.primaryKeyFieldDefinition = this.dataShapeUtils.getPrimaryKeyField(dataShape); 
    return this.primaryKeyFieldDefinition;
  }
  
  Object getSqlValue(Object value, FieldDefinition fieldDefinition, FieldDatabaseInfo fieldDatabaseInfo) {
    BaseTypes baseType = getBaseType(fieldDefinition, fieldDatabaseInfo);
    Optional<DataTypeConverter> converter = this.databaseHandler.getDataTypeConverter(baseType);
    if (converter.isPresent())
      return ((DataTypeConverter)converter.get()).toSqlDataTypeValue(value); 
    return value;
  }
  
  Object getJavaValue(Object value, BaseTypes baseTapes) {
    Optional<DataTypeConverter> converter = this.databaseHandler.getDataTypeConverter(baseTapes);
    if (converter.isPresent())
      return ((DataTypeConverter)converter.get()).toJavaDataTypeValue(value); 
    return value;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      Pair<String, DataShape> statement = getStatement();
      if (statement != null && statement.getKey() != null && !((String)statement.getKey()).isEmpty()) {
        preparedStatement = connection.prepareStatement((String)statement.getKey(), 1);
        setParameter(preparedStatement, (DataShape)statement.getValue());
        int result = preparedStatement.executeUpdate();
        resultSet = preparedStatement.getGeneratedKeys();
        QueryResult queryResult = buildQueryResult(resultSet, result, this.dataShape, getColumnName(getPrimaryKey(this.dataShape)));
        postExecute(queryResult);
        return Optional.of(queryResult);
      } 
    } catch (Exception exception) {
      _logger
        .error("Error executing statement: " + ((this.statement != null) ? (String)this.statement.getKey() : "null"));
      _logger.error(getErrorMessage());
      throw exception;
    } finally {
      if (preparedStatement != null)
        preparedStatement.close(); 
      if (resultSet != null)
        resultSet.close(); 
    } 
    return Optional.empty();
  }
  
  String getErrorMessage() {
    return "Error executing for data shape:" + getDataShape().getName();
  }
  
  void postExecute(QueryResult queryResult) throws Exception {}
  
  QueryResult buildQueryResult(ResultSet resultSet, String primaryKeyColumnName) throws SQLException {
    return buildQueryResult(resultSet, 0, this.dataShape, primaryKeyColumnName);
  }
  
  private QueryResult buildQueryResult(ResultSet resultSet, int result, DataShape dataShape, String primaryKeyColumnName) throws SQLException {
    QueryResult queryResult = new QueryResult(result, true);
    if (resultSet != null) {
      ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
      int columnCount = resultSetMetaData.getColumnCount();
      while (resultSet.next()) {
        Map<String, Object> values = Maps.newHashMap();
        Object key = null;
        for (int count = 1; count <= columnCount; count++) {
          String columnName = resultSetMetaData.getColumnName(count);
          Object value = resultSet.getObject(columnName);
          if ("GENERATED_KEYS".equals(columnName)) {
            columnName = primaryKeyColumnName;
            if (value == null && result == 1) {
              value = getPrimaryKeyValue();
            } else if (result == 0) {
              continue;
            } 
          } 
          if (columnName.equals(primaryKeyColumnName))
            key = value; 
          values.put(columnName, value);
          continue;
        } 
        if (!values.isEmpty()) {
          if (!dataShape.getDataShape().hasField("DataShapeName") && 
            !values.containsKey("datashapename"))
            values.put("datashapename", dataShape.getName()); 
          ResultRow row = new ResultRow(key);
          row.setValues(values);
          queryResult.addRow(row);
        } 
      } 
    } 
    return queryResult;
  }
  
  protected Object getPrimaryKeyValue() {
    return null;
  }
  
  boolean isPrimaryKeyAutoGenerated() {
    return this.isPrimaryKeyAutoGenerated;
  }
  
  public Optional<DataChange> getDataChange() {
    return Optional.empty();
  }
  
  int getDefaultStringLength() {
    return this.databaseHandler.getDefaultStringLength();
  }
  
  protected Optional<FieldDatabaseInfo> getFieldDatabaseInfo(String fieldName) {
    Optional<DataShapeDatabaseInfo> found = getDataShapeDatabaseInfo();
    if (found.isPresent()) {
      DataShapeDatabaseInfo dataShapeDatabaseInfo = found.get();
      List<FieldDatabaseInfo> fieldDatabaseInfoList = dataShapeDatabaseInfo.getFields();
      if (fieldDatabaseInfoList != null)
        for (FieldDatabaseInfo fieldDatabaseInfo : fieldDatabaseInfoList) {
          if (fieldDatabaseInfo.getName().equals(fieldName))
            return Optional.of(fieldDatabaseInfo); 
        }  
    } 
    return Optional.empty();
  }
  
  private DataShapeDatabaseInfo getDataShapeDatabaseInfo(String dataShapeName) {
    Optional<DataShapeDatabaseInfo> found = this.databaseHandler.getDatabaseInfoManager().getDataShapeDatabaseInfo(dataShapeName);
    if (found.isPresent())
      return found.get(); 
    return null;
  }
  
  protected Optional<DataShapeDatabaseInfo> getDataShapeDatabaseInfo() {
    return Optional.ofNullable(this.dataShapeDatabaseInfo);
  }
  
  protected abstract String createStatement(DataShape paramDataShape);
}
