package twx.core.db.model;

import java.io.Serializable;
import java.util.Collection;

import org.json.JSONObject;

/*
 *  Base class for all of the members in the DB-Model Tree ... 
 */
public class DbObject<ParentType extends DbObject<?>> implements Serializable {

  private static final long serialVersionUID = 1L;

  transient protected ParentType parent;  // << not serialized ... 
  protected String name;
  protected String description;

  protected DbObject(ParentType parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesciption() {
    return this.description;
  }

  public void setDescription(String desc) {
    this.description = desc;
  }

  protected void takeOwnerShip(DbObject<?> parent) {
    this.parent = (ParentType)parent;
  }

  protected void addChild( DbObject<?> child ) {
    child.takeOwnerShip(this);
  }

  public void clear() {
    this.parent = null;
    this.name = null;
    this.description = null;
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

  public DbModel getModel() {
    return (DbModel) this.getRoot();
  }

  public String getFullName() {
    String name = getName();
    String prefix = (!(this.isRoot()) ? getParent().getFullName() : null);
    if (name == null) {
      name = prefix;
    } else if (prefix != null) {
      if (name.contains("."))
        name = prefix + ".[" + name + "]";
      else
        name = prefix + "." + name;
    }
    return name;
  }

  protected <T extends DbObject<?>> T checkOwnership(T obj) {
    if (obj.getParent() != this) {
      throw new IllegalArgumentException(
          "Given " + obj.getClass().getSimpleName() + " is not owned by this " +
              getClass().getSimpleName());
    }
    return obj;
  }

  protected static <T extends DbObject<?>> T findObject(Collection<T> objects, String name) {
    for (T obj : objects) {
      if ((name == obj.getName()) ||
          ((name != null) && name.equals(obj.getName()))) {
        return obj;
      }
    }
    return null;
  }

  // region Serialization ... 
  // --------------------------------------------------------------------------------
  public JSONObject toJSON() {
    var json = new JSONObject();
    json.put( DbConstants.MODEL_TAG_NAME, getName());
    if (description != null)
      json.put(DbConstants.MODEL_TAG_DESCRIPT, this.description);
    return json;
  }

  public DbObject<?> fromJSON(JSONObject json) {
    if (json.has(DbConstants.MODEL_TAG_NAME))
      this.name = json.getString(DbConstants.MODEL_TAG_NAME);
    if (json.has(DbConstants.MODEL_TAG_DESCRIPT))
      this.description = json.getString(DbConstants.MODEL_TAG_DESCRIPT);
    return this;
  }
  // endregion 
}
