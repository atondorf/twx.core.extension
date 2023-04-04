package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;

import org.apache.commons.lang.Validate;

class DatabaseRemoveForeignKeyTableHandler extends AbstractDatabaseColumnTableHandler {

  private ForeignKey foreignKey;
  
  DatabaseRemoveForeignKeyTableHandler( DataShape dataShape,  String fieldName,  ForeignKey foreignKey,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    Validate.notNull(foreignKey);
    this.foreignKey = foreignKey;
  }
  
  protected String createStatement( DataShape dataShape,  FieldDefinition fieldDefinition) {
    StringBuilder sqlAlter = new StringBuilder(50);
    sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape)).append(" DROP CONSTRAINT ")
      .append(getForeignKeyName(dataShape, fieldDefinition, this.foreignKey));
    return sqlAlter.toString();
  }
}
