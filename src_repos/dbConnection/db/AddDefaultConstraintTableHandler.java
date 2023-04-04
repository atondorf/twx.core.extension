package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.Optional;


import org.apache.commons.lang3.tuple.Pair;

class AddDefaultConstraintTableHandler extends AbstractDatabaseColumnTableHandler {
  
  private FieldDatabaseInfo fieldDatabaseInfo;
  
  AddDefaultConstraintTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.fieldDatabaseInfo = fieldDatabaseInfo;
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    Optional<Pair<Integer, String>> found = getSqlType(fieldDefinition, this.fieldDatabaseInfo);
    if (found.isPresent()) {
      StringBuilder sqlAlter = new StringBuilder(50);
      sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape));
      if (this.fieldDatabaseInfo != null) {
        String defaultValue = this.fieldDatabaseInfo.getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
          sqlAlter.append(" ADD CONSTRAINT ")
            .append(getDefaultConstraintName(dataShape, fieldDefinition)).append(" DEFAULT ");
          if (2 != ((Integer)((Pair)found.get()).getKey()).intValue() || -7 != ((Integer)((Pair)found.get()).getKey()).intValue()) {
            sqlAlter.append('\'').append(defaultValue).append('\'');
          } else {
            sqlAlter.append(defaultValue);
          } 
          sqlAlter.append(" FOR ").append(getColumnName(fieldDefinition));
        } 
      } 
      return sqlAlter.toString();
    } 
    throw new ThingworxRuntimeException("Sql type not found: " + fieldDefinition.getName() + ":" + fieldDefinition
        .getBaseType().name());
  }
}
