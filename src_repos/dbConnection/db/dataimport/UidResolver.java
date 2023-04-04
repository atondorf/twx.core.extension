package twx.core.db.dataimport;

import twx.core.db.DatabaseInfoManager;
import twx.core.db.ForeignKey;
import java.util.HashMap;
import java.util.Map;

public class UidResolver {
  private final Map<String, Map<String, String>> internalToExternalUids = new HashMap<>();
  
  private Map<String, Map<String, ForeignKey>> foreignKeysDsAndField;
  
  public static final String INTERNAL_REFERENCE_PREFIX = "BUNDLE:";
  
  public UidResolver() throws Exception {
    DatabaseInfoManager databaseInfoManager = DatabaseInfoManager.getInstance();
    this.foreignKeysDsAndField = databaseInfoManager.getForeignKeyCache();
  }
  
  public String getExternalUid(String dataShapeName, String internalUid) {
    Map<String, String> internalToExternalUidForDataShape = this.internalToExternalUids.get(dataShapeName);
    if (internalToExternalUidForDataShape == null)
      return null; 
    return internalToExternalUidForDataShape.get(internalUid);
  }
  
  public void setExternalUid(String dataShapeName, String internalUid, String externalUid) {
    Map<String, String> internalToExternalUidForDataShape = this.internalToExternalUids.computeIfAbsent(dataShapeName, k -> new HashMap<>());
    internalToExternalUidForDataShape.put(internalUid, externalUid);
  }
  
  public ForeignKey getForeignKey(String dataShapeName, String fieldName) {
    Map<String, ForeignKey> foreignKeyDsAndFieldForDataShape = this.foreignKeysDsAndField.get(dataShapeName);
    if (foreignKeyDsAndFieldForDataShape == null)
      return null; 
    return foreignKeyDsAndFieldForDataShape.get(fieldName);
  }
  
  public String getReferenceDataShapeName(String dataShapeName, String fieldName) {
    ForeignKey foreignKey = getForeignKey(dataShapeName, fieldName);
    if (foreignKey == null)
      return null; 
    return foreignKey.getReferenceDataShapeName();
  }
  
  public String getInternalUid(String dataShapeName, String fieldName, String internalUid) {
    String foreignKeyDataShapeName = getReferenceDataShapeName(dataShapeName, fieldName);
    if (foreignKeyDataShapeName == null)
      return null; 
    return getExternalUid(foreignKeyDataShapeName, internalUid);
  }
  
  public boolean isReferenceField(String dataShapeName, String fieldName) {
    ForeignKey foreignKey = getForeignKey(dataShapeName, fieldName);
    return (foreignKey != null);
  }
  
  public boolean isBundleReference(String fieldValue) {
    if (fieldValue.startsWith("BUNDLE:"))
      return true; 
    return false;
  }
}
