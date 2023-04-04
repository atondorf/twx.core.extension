package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;

import org.apache.commons.lang.Validate;

class DatabaseAddForeignKeyTableHandler extends AbstractDatabaseColumnTableHandler {

  private DataShape referenceDataShape;
  

  private FieldDefinition referenceFieldDefinition;
  
  private ForeignKey foreignKey;
  
  protected ForeignKey getForeignKey() {
    return this.foreignKey;
  }
  
  DatabaseAddForeignKeyTableHandler( DataShape dataShape,  String fieldName,  ForeignKey foreignKey,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    Validate.notNull(foreignKey);
    this.foreignKey = foreignKey;
    this.referenceDataShape = this.dataShapeUtils.getDataShape(foreignKey.getReferenceDataShapeName());
    Validate.notNull(this.referenceDataShape);
    this
      .referenceFieldDefinition = this.referenceDataShape.getFieldDefinition(foreignKey.getReferenceFieldName());
    Validate.notNull(this.referenceFieldDefinition);
  }
  
  protected String createStatement( DataShape dataShape,  FieldDefinition fieldDefinition) {
    StringBuilder sqlAlter = new StringBuilder(60);
    String foreignKeyName = getForeignKeyName(dataShape, fieldDefinition, this.foreignKey);
    sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape)).append(" ADD CONSTRAINT ")
      .append(foreignKeyName).append(" FOREIGN KEY (").append(getColumnName(fieldDefinition))
      .append(") REFERENCES ").append(getTableName(this.referenceDataShape)).append('(')
      .append(getColumnName(this.referenceFieldDefinition)).append(')');
    return sqlAlter.toString();
  }
}
