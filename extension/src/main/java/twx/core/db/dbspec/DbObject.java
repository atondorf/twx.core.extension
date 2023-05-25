package twx.core.db.dbspec;

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
  protected List<DbObject<?>> children;

  protected final String name;
  protected String  description;

  protected DbObject(ParentType parent, String name) {
    this.parent = parent;
    this.name = name;
    this.children = new LinkedList<DbObject<?>>();
  }

  protected void takeOwnerShip(DbObject<?> parent) {
    this.parent = (ParentType) parent;
  }

  public boolean isRoot() {
    return this.parent == null;
  }

  public boolean isLeaf() {
    return this.children.size() == 0;
  }

  public int getChildCount() {
    return this.children.size();
  }

  DbObject<?> getChild(String name) {
    return this.children.stream().filter(child -> name.equals(child.getName())).findFirst().orElse(null);
  }

  public int getLevel() {
    if (this.isRoot())
      return 0;
    else
      return this.parent.getLevel() + 1;
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

  public DbSpec getSpec() {
    return (DbSpec)this.getRoot();
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    String name   = getName();
    String prefix = (!(this.isRoot()) ? getParent().getFullName() : null);
    if(name == null) {
      name = prefix;
    } else if(prefix != null) {
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

  protected void addChild(DbObject<?> child) {
    child.takeOwnerShip(this);
		this.children.add(child);
	}

  public JSONObject toJSON() {
    var json = new JSONObject();
    json.put("name", getName());
    if( description != null )
      json.put("description",this.description);
    return json;
  }
}
