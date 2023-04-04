package twx.core.db;

import com.google.common.collect.Maps;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.apache.commons.lang3.tuple.Pair;

public class DatabaseCreateTableHandler extends AbstractDatabaseTableHandler {
  
  private DataShapeDatabaseInfo dataShapeDatabaseInfo;
  
  private Map<String, FieldDatabaseInfo> fieldMap;
  
  DatabaseCreateTableHandler(DataShape dataShape,  DataShapeDatabaseInfo dataShapeDatabaseInfo, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.dataShapeDatabaseInfo = dataShapeDatabaseInfo;
  }
  
  protected String createStatement(DataShape dataShape) {
    FieldDefinition fieldDefinitionPrimaryKey = getPrimaryKey(dataShape);
    StringBuilder sqlCreate = new StringBuilder(60);
    sqlCreate.append("CREATE TABLE ").append(getTableName(dataShape)).append('(');
    for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
      if (fieldDefinition.isPrimaryKey())
        continue; 
      Optional<Pair<Integer, String>> found = getSqlType(this.dataShapeDatabaseInfo, fieldDefinition);
      if (found.isPresent()) {
        sqlCreate.append(' ').append(getColumnName(fieldDefinition)).append(' ')
          .append((String)((Pair)found.get()).getValue());
        if (12 == ((Integer)((Pair)found.get()).getKey()).intValue() || -9 == ((Integer)((Pair)found.get()).getKey()).intValue()) {
          int length = getLength(this.dataShapeDatabaseInfo, fieldDefinition);
          sqlCreate.append('(').append(length).append(')');
        } 
        if (2 == ((Integer)((Pair)found.get()).getKey()).intValue())
          sqlCreate.append("(18,5)"); 
        if (isUnique(this.dataShapeDatabaseInfo, fieldDefinition))
          sqlCreate.append(" UNIQUE"); 
        if (isNotNull(this.dataShapeDatabaseInfo, fieldDefinition))
          sqlCreate.append(" NOT NULL"); 
        String defaultValue = getDefaultValue(this.dataShapeDatabaseInfo, fieldDefinition);
        if (defaultValue != null) {
          sqlCreate.append(" CONSTRAINT ")
            .append(getDefaultConstraintName(dataShape, fieldDefinition)).append(" DEFAULT ");
          if (2 != ((Integer)((Pair)found.get()).getKey()).intValue() || -7 != ((Integer)((Pair)found.get()).getKey()).intValue()) {
            sqlCreate.append('\'').append(defaultValue).append('\'');
          } else {
            sqlCreate.append(defaultValue);
          } 
        } 
        sqlCreate.append(',');
      } 
    } 
    sqlCreate.append(' ').append(getColumnName(fieldDefinitionPrimaryKey)).append(' ')
      .append(getPrimaryKeySqlType(this.dataShapeDatabaseInfo, fieldDefinitionPrimaryKey))
      .append(" PRIMARY KEY)");
    return sqlCreate.toString();
  }
  
  private boolean isNotNull(DataShapeDatabaseInfo dataShapeDatabaseInfo, FieldDefinition fieldDefinition) {
    boolean isNotNull = false;
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    if (foundFieldDatabaseInfo.isPresent())
      isNotNull = ((FieldDatabaseInfo)foundFieldDatabaseInfo.get()).isNotNull(); 
    return isNotNull;
  }
  
  private boolean isUnique(DataShapeDatabaseInfo dataShapeDatabaseInfo, FieldDefinition fieldDefinition) {
    boolean isUnique = false;
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    if (foundFieldDatabaseInfo.isPresent())
      isUnique = ((FieldDatabaseInfo)foundFieldDatabaseInfo.get()).isUnique(); 
    return isUnique;
  }
  
  private String getDefaultValue(DataShapeDatabaseInfo dataShapeDatabaseInfo, FieldDefinition fieldDefinition) {
    String defaultValue = null;
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    if (foundFieldDatabaseInfo.isPresent())
      defaultValue = ((FieldDatabaseInfo)foundFieldDatabaseInfo.get()).getDefaultValue(); 
    return defaultValue;
  }
  
  private int getLength( DataShapeDatabaseInfo dataShapeDatabaseInfo,  FieldDefinition fieldDefinition) {
    int length = getDefaultStringLength();
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    if (foundFieldDatabaseInfo.isPresent())
      length = (((FieldDatabaseInfo)foundFieldDatabaseInfo.get()).getLength() > 0) ? ((FieldDatabaseInfo)foundFieldDatabaseInfo.get()).getLength() : length; 
    return length;
  }
  
  private Optional<Pair<Integer, String>> getSqlType(DataShapeDatabaseInfo dataShapeDatabaseInfo, FieldDefinition fieldDefinition) {
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    return getSqlType(fieldDefinition, foundFieldDatabaseInfo.orElse(null));
  }
  
  private String getPrimaryKeySqlType(DataShapeDatabaseInfo dataShapeDatabaseInfo, FieldDefinition fieldDefinition) {
    Optional<FieldDatabaseInfo> foundFieldDatabaseInfo = getFieldDatabaseInfo(dataShapeDatabaseInfo, fieldDefinition);
    return getPrimaryKeySqlType(fieldDefinition, foundFieldDatabaseInfo.orElse(null));
  }
  
  private Optional<FieldDatabaseInfo> getFieldDatabaseInfo( DataShapeDatabaseInfo dataShapeDatabaseInfo,  FieldDefinition fieldDefinition) {
    if (dataShapeDatabaseInfo != null) {
      if (this.fieldMap == null) {
        this.fieldMap = Maps.newHashMap();
        List<FieldDatabaseInfo> fieldDatabaseInfoList = dataShapeDatabaseInfo.getFields();
        if (fieldDatabaseInfoList != null)
          for (FieldDatabaseInfo fieldDatabaseInfo1 : fieldDatabaseInfoList)
            this.fieldMap.put(fieldDatabaseInfo1.getName(), fieldDatabaseInfo1);  
      } 
      FieldDatabaseInfo fieldDatabaseInfo = this.fieldMap.get(fieldDefinition.getName());
      if (fieldDatabaseInfo != null)
        return Optional.of(fieldDatabaseInfo); 
    } 
    return Optional.empty();
  }
}
