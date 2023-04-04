package twx.core.db;

import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class DatabaseUpdateTableHandler extends AbstractDatabaseTableHandler {
  private FieldInformationUtil fieldInformationUtil = new FieldInformationUtil();
  
  private Map<String, Pair<FieldDatabaseInfo, Integer>> fieldNames = Maps.newLinkedHashMap();
  
  private List<String> nullFieldNames;
  
  private ValueCollection valueCollection;
  
  private FieldDefinition primaryKey;
  
  DatabaseUpdateTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    Validate.notNull(valueCollection, "Row to update can't be null", new Object[0]);
    this.valueCollection = valueCollection;
    this.primaryKey = getPrimaryKey(dataShape);
    Validate.notNull(this.primaryKey);
    this.nullFieldNames = this.fieldInformationUtil.getNullFieldNames(valueCollection);
  }
  
  protected String createStatement(DataShape dataShape) {
    return generateUpdate(dataShape);
  }
  
  private String generateUpdate(DataShape dataShape) {
    StringBuilder sqlUpdate = new StringBuilder(50);
    sqlUpdate.append("UPDATE ").append(getTableName(dataShape)).append(" SET");
    for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
      if (((!this.valueCollection.containsKey(fieldDefinition.getName()) || fieldDefinition
        .equals(this.primaryKey)) && 
        !this.nullFieldNames.contains(fieldDefinition.getName())) || 
        this.fieldNames.containsKey(fieldDefinition.getName()))
        continue; 
      Optional<FieldDatabaseInfo> foundField = getFieldDatabaseInfo(fieldDefinition.getName());
      FieldDatabaseInfo fieldDatabaseInfo = foundField.isPresent() ? foundField.get() : null;
      Optional<Integer> foundType = getSqlTypeKey(fieldDefinition, fieldDatabaseInfo);
      if (foundType.isPresent()) {
        sqlUpdate.append(' ').append(getColumnName(fieldDefinition));
        sqlUpdate.append("= ?,");
        this.fieldNames.put(fieldDefinition.getName(), Pair.of(fieldDatabaseInfo, foundType.get()));
      } 
    } 
    if (this.fieldNames.isEmpty())
      return null; 
    if (sqlUpdate.toString().endsWith(","))
      sqlUpdate = new StringBuilder(sqlUpdate.substring(0, sqlUpdate.length() - 1)); 
    sqlUpdate.append(" WHERE ").append(getColumnName(this.primaryKey)).append(" = ?");
    return sqlUpdate.toString();
  }
  
  protected void setParameter(PreparedStatement preparedStatement, DataShape dataShape) throws SQLException {
    int index = 1;
    for (Map.Entry<String, Pair<FieldDatabaseInfo, Integer>> fieldName : this.fieldNames.entrySet()) {
      FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName.getKey());
      Object object = this.valueCollection.getValue(fieldDefinition.getName());
      if (object != null) {
        object = getSqlValue(object, fieldDefinition, (FieldDatabaseInfo)((Pair)fieldName.getValue()).getLeft());
        preparedStatement.setObject(index, object, ((Integer)((Pair)fieldName.getValue()).getRight()).intValue());
      } else {
        preparedStatement.setNull(index, ((Integer)((Pair)fieldName.getValue()).getRight()).intValue());
      } 
      index++;
    } 
    Optional<FieldDatabaseInfo> foundField = getFieldDatabaseInfo(this.primaryKey.getName());
    Object value = getPrimaryKeyValue(foundField.orElse(null));
    if (value == null)
      throw new ThingworxRuntimeException("Primary key can't be null"); 
    Optional<Integer> foundSqlType = getSqlTypeKey(this.primaryKey, foundField.orElse(null));
    if (foundSqlType.isPresent()) {
      Integer sqlType = foundSqlType.get();
      preparedStatement.setObject(index, value, sqlType.intValue());
    } 
  }
  
  protected Object getPrimaryKeyValue() {
    Optional<FieldDatabaseInfo> foundField = getFieldDatabaseInfo(this.primaryKey.getName());
    FieldDatabaseInfo fieldDatabaseInfo = foundField.orElse(null);
    return getPrimaryKeyValue(fieldDatabaseInfo);
  }
  
  private Object getPrimaryKeyValue(FieldDatabaseInfo fieldDatabaseInfo) {
    if (this.valueCollection != null)
      return getSqlValue(this.valueCollection.getValue(this.primaryKey.getName()), this.primaryKey, fieldDatabaseInfo); 
    return null;
  }
  
  protected ValueCollection getValueCollection() {
    return this.valueCollection;
  }
  
  public Optional<DataChange> getDataChange() {
    if (!isAdditionalPropertiesDataShape())
      return Optional.of(new DataChange(ActionType.UPDATE, getDataShape().getName(), 
            getValueCollection().clone())); 
    return Optional.empty();
  }
  
  String getErrorMessage() {
    return "Error on update data shape: " + getDataShape().getName() + " row: " + (
      (this.valueCollection != null) ? this.valueCollection.toString() : "null");
  }
}
