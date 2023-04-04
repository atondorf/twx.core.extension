package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;

import org.apache.commons.lang3.Validate;

public class SelectElement {

  private DataShape dataShape;
  

  private FieldDefinition fieldDefinition;
  
  private String selectName;
  
  private String selectValue;
  
  private String infoTableColumnKey;
  
  private SqlFunction sqlFunction;
  
  SelectElement( DataShape dataShape,  FieldDefinition fieldDefinition, String selectValue, String selectName, String infoTableColumnKey, SqlFunction sqlFunction) {
    Validate.notNull(dataShape);
    this.dataShape = dataShape;
    Validate.notNull(fieldDefinition);
    this.fieldDefinition = fieldDefinition;
    this.selectValue = selectValue;
    this.selectName = selectName;
    this.infoTableColumnKey = infoTableColumnKey;
    this.sqlFunction = sqlFunction;
  }
  

  DataShape getDataShape() {
    return this.dataShape;
  }
  

  String getSelectName() {
    if (this.selectName == null || this.selectName.isEmpty())
      this.selectName = getFieldDefinition().getName(); 
    return this.selectName;
  }
  

  String getSelectValue() {
    if (this.selectValue == null || this.selectValue.isEmpty())
      this.selectValue = getFieldDefinition().getName(); 
    return this.selectValue;
  }
  

  FieldDefinition getFieldDefinition() {
    return this.fieldDefinition;
  }
  

  String getInfoTableColumnKey() {
    if (this.infoTableColumnKey == null || this.infoTableColumnKey.isEmpty())
      this.infoTableColumnKey = getFieldDefinition().getName(); 
    return this.infoTableColumnKey;
  }
  
  SqlFunction getFunction() {
    return this.sqlFunction;
  }
}
