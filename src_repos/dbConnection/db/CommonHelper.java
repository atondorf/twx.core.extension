package twx.core.db;

import org.apache.commons.lang3.Validate;

public class CommonHelper {
  public static String getLowerLastString(String value, Character characterDelimiter) {
    int length = value.length();
    int lastIndex = value.lastIndexOf(characterDelimiter.charValue());
    Validate.validState((lastIndex + 1 < length), String.format("Invalid value :%s", new Object[] { value }), new Object[0]);
    String result = value.substring(lastIndex + 1);
    Validate.notNull(result, "Invalid value :" + value, new Object[0]);
    Validate.notEmpty(result, "Invalid value :" + value, new Object[0]);
    Validate.notBlank(result, "Invalid value :" + value, new Object[0]);
    return result.toLowerCase();
  }
}
