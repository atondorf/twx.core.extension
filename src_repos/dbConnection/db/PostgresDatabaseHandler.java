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

public class PostgresDatabaseHandler extends AbstractDataBaseHandler {
  private Map<Integer, String> typeMap = Maps.newHashMap();
  
  private Map<BaseTypes, Integer> sqlTypeMap;
  
  private Set<String> supportedSqlType = Sets.newHashSet(new String[] { "varchar", "int4", "bool", "numeric", "timestamptz", "int8" });
  
  private static final int MAX_CHAR = 63;
  
  public DatabaseTableHandlerFactory createDatabaseTableHandlerFactory() {
    return new PostgresDatabaseTableHandlerFactory(this);
  }
  
  public JdbcQuery getJdbcQuery() {
    return new PostgresJdbcQuery();
  }
  
  protected String getAutoIncrementType(Integer sqlType) {
    if (sqlType.intValue() == -5)
      return "bigserial"; 
    if (sqlType.intValue() == 4)
      return "serial"; 
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
      this.sqlTypeMap.put(BaseTypes.STRING, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.NUMBER, Integer.valueOf(2));
      this.sqlTypeMap.put(BaseTypes.INTEGER, Integer.valueOf(4));
      this.sqlTypeMap.put(BaseTypes.BOOLEAN, Integer.valueOf(-7));
      this.sqlTypeMap.put(BaseTypes.LONG, Integer.valueOf(-5));
      this.sqlTypeMap.put(BaseTypes.TEXT, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.DATETIME, Integer.valueOf(93));
      this.sqlTypeMap.put(BaseTypes.THINGNAME, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.THINGSHAPENAME, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.THINGTEMPLATENAME, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.USERNAME, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.SCHEDULE, Integer.valueOf(12));
      this.sqlTypeMap.put(BaseTypes.IMAGELINK, Integer.valueOf(12));
    } 
    return this.sqlTypeMap;
  }
  
  int getNameMaxChar() {
    return 63;
  }
}
