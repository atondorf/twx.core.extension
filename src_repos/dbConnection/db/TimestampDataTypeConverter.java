package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import java.sql.Timestamp;
import org.joda.time.DateTime;

public class TimestampDataTypeConverter implements DataTypeConverter<Timestamp, DateTime> {
  public Timestamp toSqlDataTypeValue(Object value) {
    if (value == null)
      return null; 
    if (value instanceof Timestamp)
      return (Timestamp)value; 
    if (value instanceof DateTime)
      return new Timestamp(((DateTime)value).getMillis()); 
    Class<?> dataType = value.getClass();
    throw new ThingworxRuntimeException("Type not supported:" + dataType);
  }
  
  public DateTime toJavaDataTypeValue(Object value) {
    if (value == null)
      return null; 
    if (value instanceof DateTime)
      return (DateTime)value; 
    if (value instanceof Timestamp)
      return new DateTime(((Timestamp)value).getTime()); 
    Class<?> dataType = value.getClass();
    throw new ThingworxRuntimeException("Type not supported:" + dataType);
  }
}
