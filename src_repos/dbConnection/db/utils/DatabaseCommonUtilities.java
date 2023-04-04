package twx.core.db.utils;

import com.thingworx.types.InfoTable;

public class DatabaseCommonUtilities {
  public static boolean isEmpty(InfoTable table) {
    boolean result = (table == null || table.getRowCount().intValue() == 0);
    return result;
  }
  
  public static boolean isNotEmpty(InfoTable table) {
    boolean result = (table != null && table.getRowCount().intValue() > 0);
    return result;
  }
}
