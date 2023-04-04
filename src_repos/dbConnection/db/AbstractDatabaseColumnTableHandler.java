package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.Optional;


import org.apache.commons.lang3.Validate;

abstract class AbstractDatabaseColumnTableHandler extends AbstractDatabaseTableHandler {
  private String fieldName;
  
  protected String getFieldName() {
    return this.fieldName;
  }
  
  AbstractDatabaseColumnTableHandler( DataShape dataShape,  String fieldName,  DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    Validate.notNull(fieldName);
    Validate.notEmpty(fieldName);
    Validate.notBlank(fieldName);
    this.fieldName = fieldName;
    Optional<FieldDefinition> found = getFieldDefinition(dataShape, fieldName);
    if (!found.isPresent())
      throw new ThingworxRuntimeException("Field:" + fieldName + " does not exit in data shape:" + dataShape
          .getName()); 
  }
  
  protected String createStatement(DataShape dataShape) {
    Optional<FieldDefinition> found = getFieldDefinition(dataShape, this.fieldName);
    if (found.isPresent())
      return createStatement(dataShape, found.get()); 
    throw new ThingworxRuntimeException("Field:" + this.fieldName + " does not exit in data shape:" + dataShape
        .getName());
  }
  
  protected abstract String createStatement(DataShape paramDataShape, FieldDefinition paramFieldDefinition);
  
  protected int getLength( FieldDatabaseInfo fieldDatabaseInfo) {
    int length = getDefaultStringLength();
    if (fieldDatabaseInfo != null && fieldDatabaseInfo.getLength() > 0)
      length = fieldDatabaseInfo.getLength(); 
    return length;
  }
}
