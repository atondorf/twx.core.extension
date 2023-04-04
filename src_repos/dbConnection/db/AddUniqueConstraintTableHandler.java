package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.Optional;


import org.apache.commons.lang3.tuple.Pair;

class AddUniqueConstraintTableHandler extends AbstractDatabaseColumnTableHandler {
  
  private FieldDatabaseInfo fieldDatabaseInfo;
  
  AddUniqueConstraintTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.fieldDatabaseInfo = fieldDatabaseInfo;
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    Optional<Pair<Integer, String>> found = getSqlType(fieldDefinition, this.fieldDatabaseInfo);
    if (found.isPresent()) {
      StringBuilder sqlAlter = new StringBuilder(50);
      sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape));
      if (this.fieldDatabaseInfo != null && this.fieldDatabaseInfo.isUnique())
        sqlAlter.append(" ADD CONSTRAINT ")
          .append(getUniqueConstraintName(dataShape, fieldDefinition)).append(" UNIQUE (")
          .append(getColumnName(fieldDefinition)).append(')'); 
      return sqlAlter.toString();
    } 
    throw new ThingworxRuntimeException("Sql type not found: " + fieldDefinition.getName() + ":" + fieldDefinition
        .getBaseType().name());
  }
}
