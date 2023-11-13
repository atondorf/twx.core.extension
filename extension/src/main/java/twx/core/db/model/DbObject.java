package twx.core.db.model;

import java.io.Serializable;
import java.util.*;

import org.json.JSONObject;

/*
 *  Base class for all of the members in the DB-Model Tree ... 
 */
public class DbObject<ParentType extends DbObject<?>> {
    public static final String DEFAULT_NAME = "";
    protected ParentType parent; // << not serialized ...
    protected String name;
    protected String note;

    protected DbObject(ParentType parent, String name) {
        this.parent = parent;
        this.name = (name != null) ? name : DEFAULT_NAME;
    }

    public void clear() {
        this.parent = null;
        this.name = null;
        this.note = null;
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public Boolean hasName() {
        return this.name != null;
    }

    public Boolean isDefaultName() {
        return this.name == null || this.name.equals(DEFAULT_NAME);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean hasNote() {
        return this.note != null;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    // endregion
    // region Tree Management ...
    // --------------------------------------------------------------------------------
    protected void takeOwnerShip(DbObject<?> parent) {
        this.parent = (ParentType) parent;
    }

    protected void addChild(DbObject<?> child) {
        child.takeOwnerShip(this);
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
            throw new IllegalArgumentException("Given " + obj.getClass().getSimpleName() + " is not owned by this " + getClass().getSimpleName());
        }
        return obj;
    }

    protected static <T extends DbObject<?>> Boolean hasObject(Collection<T> objects, String name) {
        return objects.stream().anyMatch(c -> c.getName().equals(name));
    }

    protected static <T extends DbObject<?>> T findObject(Collection<T> objects, String name) {
        return objects.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    // region Compare and Hash ... used to keep Objects in Set<> ...
    // --------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final DbObject<?> that = (DbObject<?>) obj;
        return this.getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    public JSONObject toJSON() {
        var json = new JSONObject();
        json.put(DbConstants.MODEL_TAG_NAME, getName());
        if (note != null)
            json.put(DbConstants.MODEL_TAG_NOTE, this.note);
        return json;
    }
    // endregion
}
