package twx.core.db;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocalizationUtils {
  private static final String TOKEN_DATASHAPE = "PTC.DBConnection.Token";
  
  private static final String LANGUAGE_DATASHAPE = "PTC.DBConnection.Language";
  
  private static final String TOKEN_LANGUAGE_LINK_DATASHAPE = "PTC.DBConnection.TokenLanguageLink";
  
  private static final String UID = "uid";
  
  private static final String CODE = "code";
  
  private static final String TOKEN = "token";
  
  private static final String LANGUAGE = "language";
  
  private static final String VALUE = "value";
  
  private static final String LOCALIZED = "localized";
  
  private static final String LOCALIZED_FIELD_NAME = "localizedFieldName";
  
  protected void localizedFilter(JSONObject filter) {
    if (filter != null) {
      String localeString = filter.optString("locale");
      if (localeString != null && !localeString.isEmpty()) {
        Locale locale = Locale.forLanguageTag(localeString);
        localizedFilter(filter, locale);
      } 
    } 
  }
  
  protected void localizedFilter(JSONObject filter, Locale locale) {
    JSONArray selectArray = filter.optJSONArray("select");
    int length = selectArray.length();
    for (int i = 0; i < length; i++) {
      JSONObject select = selectArray.getJSONObject(i);
      String localizedFieldName = select.optString("localizedFieldName");
      if (localizedFieldName != null && !localizedFieldName.isEmpty()) {
        String dataShapeName = select.getString("dataShapeName");
        String fieldName = select.getString("fieldName");
        String alias = select.optString("alias");
        String tokenAlias = getLocalizedName(dataShapeName, "PTC.DBConnection.Token", fieldName, alias);
        String languageAlias = getLocalizedName(dataShapeName, "PTC.DBConnection.Language", fieldName, alias);
        String tokenlanguageAlias = getLocalizedName(dataShapeName, "PTC.DBConnection.TokenLanguageLink", fieldName, alias);
        QueryFilterUtils.addSelect(filter, "PTC.DBConnection.TokenLanguageLink", "value", localizedFieldName, tokenlanguageAlias);
        QueryFilterUtils.addJoin(filter, JoinType.LEFT.name(), dataShapeName, fieldName, alias, "PTC.DBConnection.Token", "uid", tokenAlias, null, null);
        QueryFilterUtils.addJoin(filter, JoinType.LEFT.name(), null, null, null, "PTC.DBConnection.Language", "code", languageAlias, locale
            .toLanguageTag(), null);
        JSONObject condition = new JSONObject();
        condition.put("sourceDataShapeName", "PTC.DBConnection.Language");
        condition.put("sourceAlias", languageAlias);
        condition.put("sourceFieldName", "uid");
        condition.put("targetFieldName", "language");
        condition.put("type", JoinConditionType.EQUAL.name());
        JSONArray conditions = new JSONArray();
        conditions.put(condition);
        QueryFilterUtils.addJoin(filter, JoinType.LEFT.name(), "PTC.DBConnection.Token", "uid", tokenAlias, "PTC.DBConnection.TokenLanguageLink", "token", tokenlanguageAlias, null, conditions);
      } 
    } 
  }
  
  private String getLocalizedName(String dataShapeName, String localeDataShapeName, String fieldName, String alias) {
    String prefix = (alias != null && !alias.isEmpty()) ? (alias + "_" + alias) : (getName(dataShapeName) + "_" + getName(dataShapeName));
    return prefix + "_" + prefix + "_localized";
  }
  
  private String getName(String name) {
    if (name == null || name.isEmpty())
      throw new ThingworxRuntimeException("Name can't be null or empty"); 
    return CommonHelper.getLowerLastString(name, Character.valueOf('.'));
  }
}
