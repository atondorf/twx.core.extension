package twx.core.db;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SqlServerDatabaseHandler extends AbstractDataBaseHandler {
  private Map<Integer, String> typeMap = Maps.newHashMap();
  
  private Map<BaseTypes, Integer> sqlTypeMap;
  
  private Set<String> supportedSqlType = Sets.newHashSet(new String[] { "nvarchar", "int", "bit", "numeric", "datetimeoffset", "bigint" });
  
  private static final int MAX_CHAR = 128;
  
  public SqlServerDatabaseHandler() {
    this.dataTypeConverterMap.put(BaseTypes.DATETIME, new SqlServerTimestampDataTypeConverter());
  }
  
  public DatabaseTableHandlerFactory createDatabaseTableHandlerFactory() {
    return new SqlServerDatabaseTableHandlerFactory(this);
  }
  
  public JdbcQuery getJdbcQuery() {
    return new SqlServerJdbcQuery();
  }
  
  protected String getAutoIncrementType(Integer sqlType) {
    if (sqlType.intValue() == -5)
      return "bigint identity"; 
    if (sqlType.intValue() == 4)
      return "int identity"; 
    throw new ThingworxRuntimeException("Type not supported for auto increment");
  }
  
  protected Map<Integer, String> getTypeMap() {
    if (this.typeMap.isEmpty())
      buildTypeMap(this.typeMap, this.supportedSqlType); 
    return this.typeMap;
  }
  
  protected Map<BaseTypes, Integer> getSqlTypeMap() {
    if (this.sqlTypeMap == null) {
      this.sqlTypeMap = Maps.newHashMap();
      this.sqlTypeMap.put(BaseTypes.STRING, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.NUMBER, Integer.valueOf(2));
      this.sqlTypeMap.put(BaseTypes.INTEGER, Integer.valueOf(4));
      this.sqlTypeMap.put(BaseTypes.BOOLEAN, Integer.valueOf(-7));
      this.sqlTypeMap.put(BaseTypes.LONG, Integer.valueOf(-5));
      this.sqlTypeMap.put(BaseTypes.TEXT, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.DATETIME, Integer.valueOf(-155));
      this.sqlTypeMap.put(BaseTypes.THINGNAME, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.THINGSHAPENAME, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.THINGTEMPLATENAME, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.USERNAME, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.SCHEDULE, Integer.valueOf(-9));
      this.sqlTypeMap.put(BaseTypes.IMAGELINK, Integer.valueOf(-9));
    } 
    return this.sqlTypeMap;
  }
  
  int getNameMaxChar() {
    return 128;
  }
}
