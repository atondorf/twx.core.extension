package twx.core.db.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.json.JSONObject;
import org.json.JSONArray;

public class DbObject<ParentType extends DbObject<?>> {
  protected ParentType parent;

  protected final String name;
  protected String description;

  protected DbObject(ParentType parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  protected void takeOwnerShip(DbObject<?> parent) {
    this.parent = (ParentType) parent;
  }

  public boolean isRoot() {
    return this.parent == null;
  }

  public DbObject<?> getParent() {
    return this.parent;
  }

  public DbObject<?> getRoot() {
    if (this.isRoot())
      return this;
    else
      return this.getParent().getRoot();
  }

  public DbModel getSpec() {
    return (DbModel) this.getRoot();
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    String name = getName();
    String prefix = (!(this.isRoot()) ? getParent().getFullName() : null);
    if (name == null) {
      name = prefix;
    } else if (prefix != null) {
      name = prefix + "." + name;
    }
    return name;
  }

  public String getDesciption() {
    return this.description;
  }

  public void setDescription(String desc) {
    this.description = desc;
  }

  public JSONObject toJSON() {
    var json = new JSONObject();
    json.put("name", getName());
    if (description != null)
      json.put("description", this.description);
    return json;
  }
}
