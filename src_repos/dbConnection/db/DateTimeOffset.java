package twx.core.db;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.Validate;

public class DateTimeOffset {
  static final String DATE_TIME_OFFSET = "microsoft.sql.DateTimeOffset";
  
  private static final String VALUE_OF = "valueOf";
  
  private static final String GET_TIMESTAMP = "getTimestamp";
  
  private static Class dateTimeOffsetClass;
  

  private Object dateTimeOffset;
  
  DateTimeOffset( Object dateTimeOffset) {
    Validate.notNull(dateTimeOffset);
    Validate.isTrue(dateTimeOffset.getClass().getName().equals("microsoft.sql.DateTimeOffset"));
    this.dateTimeOffset = dateTimeOffset;
  }
  
  static Object valueOf( Timestamp timestamp,  Calendar calendar) {
    try {
      Method method = getDateTimeOffsetClass().getMethod("valueOf", new Class[] { Timestamp.class, Calendar.class });
      return method.invoke(null, new Object[] { timestamp, calendar });
    } catch (NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new RuntimeException(e);
    } 
  }
  
  Timestamp getTimestamp() {
    try {
      Method method = getDateTimeOffsetClass().getMethod("getTimestamp", new Class[0]);
      return (Timestamp)method.invoke(this.dateTimeOffset, new Object[0]);
    } catch (NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new RuntimeException(e);
    } 
  }
  
  private static Class<?> getDateTimeOffsetClass() {
    if (dateTimeOffsetClass == null)
      try {
        dateTimeOffsetClass = Class.forName("microsoft.sql.DateTimeOffset");
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }  
    return dateTimeOffsetClass;
  }
}
