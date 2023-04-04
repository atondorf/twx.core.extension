package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.Optional;


import org.apache.commons.lang3.tuple.Pair;

public class SetColumnLengthTableHandler extends AbstractDatabaseColumnTableHandler {
  protected FieldDatabaseInfo fieldDatabaseInfo;
  
  SetColumnLengthTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.fieldDatabaseInfo = fieldDatabaseInfo;
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    Optional<Pair<Integer, String>> found = getSqlType(fieldDefinition, this.fieldDatabaseInfo);
    if (found.isPresent()) {
      StringBuilder sqlAlter = new StringBuilder(50);
      sqlAlter.append("ALTER TABLE ").append(getTableName(dataShape)).append(" ALTER COLUMN ")
        .append(getColumnName(fieldDefinition)).append(" TYPE ").append((String)((Pair)found.get()).getValue());
      if (12 == ((Integer)((Pair)found.get()).getKey()).intValue() || -9 == ((Integer)((Pair)found.get()).getKey()).intValue())
        sqlAlter.append('(').append(getLength(this.fieldDatabaseInfo)).append(')'); 
      if (2 == ((Integer)((Pair)found.get()).getKey()).intValue())
        sqlAlter.append("(18,5)"); 
      return sqlAlter.toString();
    } 
    throw new ThingworxRuntimeException("Sql type not found: " + fieldDefinition.getName() + ":" + fieldDefinition
        .getBaseType().name());
  }
}
