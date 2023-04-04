package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.Optional;


import org.apache.commons.lang3.tuple.Pair;

public class SetNotNullColumnTableHandler extends AbstractDatabaseColumnTableHandler {
  protected FieldDatabaseInfo fieldDatabaseInfo;
  
  SetNotNullColumnTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.fieldDatabaseInfo = fieldDatabaseInfo;
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    Optional<Pair<Integer, String>> found = getSqlType(fieldDefinition, this.fieldDatabaseInfo);
    if (found.isPresent()) {
      StringBuilder sqlAlter = new StringBuilder(50);
      sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape)).append(" ALTER COLUMN ")
        .append(getColumnName(fieldDefinition));
      if (this.fieldDatabaseInfo != null && this.fieldDatabaseInfo.isNotNull()) {
        sqlAlter.append(" SET NOT NULL");
      } else {
        sqlAlter.append(" DROP NOT NULL");
      } 
      return sqlAlter.toString();
    } 
    throw new ThingworxRuntimeException("Sql type not found: " + fieldDefinition.getName() + ":" + fieldDefinition
        .getBaseType().name());
  }
}
