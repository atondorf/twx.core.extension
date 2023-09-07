package twx.core.db.model;

import java.util.Objects;

import org.json.JSONObject;

public class DbIndexColumn extends DbObject<DbIndex> {
    public Integer  ordinal = 0;
    public DbColumn column;

    public DbIndexColumn(String name) {
        super(null, name);
    }

    protected DbIndexColumn(DbIndex parent, String name) {
        super(parent, name);
    }
 
    @Override
    public void clear() {
        super.clear();
        this.ordinal = 0;
        this.column = null;
    }

    public DbIndex getIndex() {
        return (DbIndex)this.getParent();
    }

    public DbTable getTable() {
        return this.getIndex().getTable();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public DbColumn getColumn() {
        return this.column;
    }

    public void setColumn(DbColumn column) {
        this.column = column;
        this.name = (column == null ? name : column.getName());        
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbIndexColumn mergeWith(DbIndexColumn other) throws DbModelException {
        return this;
    }

    public Boolean initialize() throws DbModelException {
        return true;
    }

    public Boolean validate() throws DbModelException {
        return true;
    }

    // endregion 
    // region Compare and Hash ...
    // --------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final DbIndexColumn that = (DbIndexColumn) obj;
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

    @Override
    public DbIndexColumn fromJSON(JSONObject json) {
        super.fromJSON(json);

        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        
        return json;
    }
    // endregion     
}
