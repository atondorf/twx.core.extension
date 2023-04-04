package twx.core.db;

public interface DataTypeConverter<S, J> {
  S toSqlDataTypeValue(Object paramObject);
  
  J toJavaDataTypeValue(Object paramObject);
}
