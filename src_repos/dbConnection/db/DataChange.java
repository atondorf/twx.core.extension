package twx.core.db;

import com.thingworx.types.collections.ValueCollection;
import org.apache.commons.lang3.Validate;

class DataChange {
  private ActionType actionType;
  
  private String dataShapeName;
  
  private ValueCollection valueCollection;
  
  DataChange(ActionType actionType, String dataShapeName, ValueCollection valueCollection) {
    Validate.notNull(actionType);
    Validate.notNull(dataShapeName);
    Validate.notBlank(dataShapeName);
    Validate.notEmpty(dataShapeName);
    this.actionType = actionType;
    this.dataShapeName = dataShapeName;
    this.valueCollection = valueCollection;
  }
  
  public ActionType getActionType() {
    return this.actionType;
  }
  
  public String getDataShapeName() {
    return this.dataShapeName;
  }
  
  public ValueCollection getValueCollection() {
    return this.valueCollection;
  }
}
