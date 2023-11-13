package twx.core.db.model;

import java.util.Objects;

import org.json.JSONObject;

public class DbRelationColumn extends DbObject<DbRelation> {
    public Integer      ordinal = 0;
    public DbColumn     fromColumn = null;
    public String       toName = null;
    public DbColumn     toColumn = null;

    protected DbRelationColumn(DbRelation parent, String name) {
        super(parent, name);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    public void clear() {
        super.clear();
        this.ordinal = 0;
        this.fromColumn = null;
        this.toColumn = null;
    }

    public DbRelation getRelation() {
        return (DbRelation)this.getParent();
    }

    public DbSchema getSchema() {
       return this.getRelation().getSchema();
    }

    public DbTable getFromTable() {
        return null;
    }

    public DbTable getToTable() {
        return null;
    }

// region Get/Set Properties
    // --------------------------------------------------------------------------------
    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public String getFromColumnName() {
        return this.getName();
    }

    public void setFromColumnName(String fromColumnName) {
        this.setName(fromColumnName);
    }

    public String getToColumnName() {
        return this.toName;
    }

    public void setToColumnName(String toColumnName) {
        this.toName = toColumnName;
    }

    public DbColumn getFromColumn() {
        return this.fromColumn;
    }

    public void setFromColumn(DbColumn fromColumn) {
        this.fromColumn = fromColumn;
        this.name = (fromColumn == null ? name : fromColumn.getName());        
    }

    public DbColumn getToColumn() {
        return this.toColumn;
    }

    public void setToColumn(DbColumn toColumn) {
        this.toColumn = toColumn;
        this.toName = (toColumn == null ? name : toColumn.getName());
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbRelationColumn mergeWith(DbRelationColumn other) throws DbModelException {
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
        final DbRelationColumn that = (DbRelationColumn) obj;
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
    public JSONObject toJSON() {
        var json = super.toJSON();
        
        return json;
    }
    // endregion     
}
