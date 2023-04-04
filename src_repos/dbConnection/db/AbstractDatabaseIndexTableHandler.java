package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

abstract class AbstractDatabaseIndexTableHandler extends AbstractDatabaseTableHandler {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ColumnFieldValidation.class);
  
  protected Index index;
  
  protected List<String> fieldNames;
  
  protected Index getIndex() {
    return this.index;
  }
  
  protected List<String> getFieldNames() {
    return this.fieldNames;
  }
  
  AbstractDatabaseIndexTableHandler( DataShape dataShape,  Index index,  DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.index = index;
    this.fieldNames = getIndexFields(index);
    this.fieldNames.forEach(field -> {
          Validate.notNull(field);
          Validate.notEmpty(field);
          Validate.notBlank(field);
          Optional<FieldDefinition> found = getFieldDefinition(dataShape, field);
          if (!found.isPresent())
            throw new ThingworxRuntimeException("Field:" + field + " does not exit in data shape:" + dataShape.getName()); 
        });
  }
  
  protected List<String> getIndexFields( Index index) {
    List<String> fieldNames = index.getFieldNames();
    if (fieldNames == null || fieldNames.isEmpty()) {
      fieldNames = Lists.newArrayList(new String[] { index.getName() });
    } else if (!Strings.isNullOrEmpty(index.getName())) {
      _logger.warn("For the Indexes, either name or fieldNames should be defined for datashape: {} on fields {}", 
          
          getDataShape().getName(), fieldNames.toString());
    } 
    return fieldNames;
  }
  
  protected String createStatement(DataShape dataShape) {
    return createStatement(dataShape, this.index);
  }
  
  protected List<FieldDefinition> getFieldDefinitions(DataShape dataShape, List<String> fields) {
    ArrayList<FieldDefinition> fieldDefinitions = Lists.newArrayList();
    fields.forEach(field -> fieldDefinitions.add(getFieldDefinition(dataShape, field).get()));
    return fieldDefinitions;
  }
  
  List<String> getColumnNames(List<FieldDefinition> fieldDefinitions) {
    return (List<String>)fieldDefinitions.stream().map(fieldDefinition -> getColumnName(fieldDefinition))
      .collect(Collectors.toList());
  }
  
  protected abstract String createStatement(DataShape paramDataShape, Index paramIndex);
}
