package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public class DatabaseDeleteTableHandler extends AbstractDatabaseTableHandler {
  private ValueCollection valueCollection;
  
  private FieldDefinition primaryKey;
  
  DatabaseDeleteTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.primaryKey = getPrimaryKey(dataShape);
    Validate.notNull(this.primaryKey);
    this.valueCollection = valueCollection;
    Validate.notNull(valueCollection, "Row to delete can't be null", new Object[0]);
    Validate.notNull(valueCollection.getValue(this.primaryKey.getName()), "Key:" + this.primaryKey
        .getName() + " can't be null", new Object[0]);
  }
  
  protected String createStatement(DataShape dataShape) {
    return generateDelete(dataShape);
  }
  
  private String generateDelete(DataShape dataShape) {
    return "DELETE FROM " + getTableName(dataShape) + " WHERE " + getColumnName(this.primaryKey) + " = ?";
  }
  
  protected void setParameter(PreparedStatement preparedStatement, DataShape dataShape) throws SQLException {
    Optional<FieldDatabaseInfo> foundField = getFieldDatabaseInfo(this.primaryKey.getName());
    Optional<Integer> found = getSqlTypeKey(this.primaryKey, foundField.orElse(null));
    if (found.isPresent()) {
      Integer sqlType = found.get();
      Object value = getPrimaryKeyValue(foundField.orElse(null));
      preparedStatement.setObject(1, value, sqlType.intValue());
    } 
  }
  
  private Object getPrimaryKeyValue(FieldDatabaseInfo fieldDatabaseInfo) {
    if (this.primaryKey != null)
      return getSqlValue(this.valueCollection.getValue(this.primaryKey.getName()), this.primaryKey, fieldDatabaseInfo); 
    return null;
  }
  
  public Optional<DataChange> getDataChange() {
    if (!isAdditionalPropertiesDataShape())
      return 
        Optional.of(new DataChange(ActionType.DELETE, getDataShape().getName(), this.valueCollection.clone())); 
    return Optional.empty();
  }
  
  String getErrorMessage() {
    return "Error on delete data shape: " + getDataShape().getName() + " row: " + (
      (this.valueCollection != null) ? this.valueCollection.toString() : "null");
  }
}
