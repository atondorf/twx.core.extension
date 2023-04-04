package twx.core.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.thingworx.metadata.FieldDefinition;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class DataShapeDatabaseInfo {
  private String dataShapeName;
  
  private List<FieldDatabaseInfo> fields;
  
  private List<Index> indexedFields;
  
  private List<ForeignKey> foreignKeys;
  
  @JsonIgnore
  private Map<String, FieldDatabaseInfo> fieldMap;
  
  public String getDataShapeName() {
    return this.dataShapeName;
  }
  
  public void setDataShapeName(String dataShapeName) {
    this.dataShapeName = dataShapeName;
  }
  
  public List<FieldDatabaseInfo> getFields() {
    return this.fields;
  }
  
  public void setFields(List<FieldDatabaseInfo> fieldDatabaseInfos) {
    this.fields = fieldDatabaseInfos;
  }
  
  public List<Index> getIndexedFields() {
    return this.indexedFields;
  }
  
  public void setIndexedFields(List<Index> indexedFields) {
    this.indexedFields = indexedFields;
  }
  
  public List<ForeignKey> getForeignKeys() {
    return this.foreignKeys;
  }
  
  public void setForeignKeys(List<ForeignKey> foreignKeys) {
    this.foreignKeys = foreignKeys;
  }
  
  protected Optional<FieldDatabaseInfo> getFieldDatabaseInfo( FieldDefinition fieldDefinition) {
    if (this.fieldMap == null) {
      this.fieldMap = Maps.newHashMap();
      List<FieldDatabaseInfo> fieldDatabaseInfoList = getFields();
      if (fieldDatabaseInfoList != null)
        for (FieldDatabaseInfo fieldDatabaseInfo : fieldDatabaseInfoList)
          this.fieldMap.put(fieldDatabaseInfo.getName(), fieldDatabaseInfo);  
    } 
    return Optional.ofNullable(this.fieldMap.get(fieldDefinition.getName()));
  }
}
