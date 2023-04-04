package twx.core.db;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import org.joda.time.DateTime;

class SqlServerTimestampDataTypeConverter implements DataTypeConverter {
  public Object toSqlDataTypeValue(Object value) {
    if (value == null)
      return null; 
    if (value instanceof Timestamp)
      return getDateTimeOffset((Timestamp)value); 
    if (value instanceof DateTime)
      return getDateTimeOffset(new Timestamp(((DateTime)value).getMillis())); 
    if (value.getClass().getName().equals("microsoft.sql.DateTimeOffset"))
      return value; 
    Class<?> dataType = value.getClass();
    throw new RuntimeException("Type not supported:" + dataType);
  }
  
  private Object getDateTimeOffset(Timestamp timestamp) {
    return DateTimeOffset.valueOf(timestamp, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
  }
  
  public Object toJavaDataTypeValue(Object value) {
    if (value == null)
      return null; 
    if (value instanceof DateTime)
      return value; 
    if (value instanceof Timestamp)
      return new DateTime(((Timestamp)value).getTime()); 
    if (value.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      DateTimeOffset dateTimeOffset = new DateTimeOffset(value);
      return dateTimeOffset.getTimestamp();
    } 
    Class<?> dataType = value.getClass();
    throw new RuntimeException("Type not supported:" + dataType);
  }
}
