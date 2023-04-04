package twx.core.db;

import com.google.common.collect.Lists;
import java.util.List;

public class FieldInformation {
  private List<Field> fields = Lists.newArrayList();
  
  public List<Field> getFields() {
    return this.fields;
  }
  
  public void setFields(List<Field> fields) {
    this.fields = fields;
  }
}
