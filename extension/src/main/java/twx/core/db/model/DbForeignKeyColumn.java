package twx.core.db.model;

import java.util.Objects;

import org.json.JSONObject;

public class DbForeignKeyColumn extends DbObject<DbForeignKey> {
    protected Integer   ordinal = 0;
    protected DbColumn  column;
    protected String    foreignColumnName = "";

    public DbForeignKeyColumn(String name) {
        super(null, name);
    }

    protected DbForeignKeyColumn(DbForeignKey parent, String name) {
        super(parent, name);
    }
 
    @Override
    public void clear() {
        super.clear();
        this.ordinal = 0;
        this.column = null;
    }

    public DbForeignKey getForeignKey() {
        return (DbForeignKey)this.getParent();
    }

    public DbTable getTable() {
        return this.getForeignKey().getTable();
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

    public String getForeignColumnName() {
        return this.foreignColumnName;
    }

    public void setForeignColumnName(String columnName) {
        this.foreignColumnName = columnName;
    }

    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------

    // endregion 
    // region Compare and Hash ...
    // --------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final DbForeignKeyColumn that = (DbForeignKeyColumn) obj;
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
        json.put(DbConstants.MODEL_TAG_ORDINAL, this.ordinal );
        json.put(DbConstants.MODEL_TAG_FOREIGN_COLUMN, this.foreignColumnName);
        return json;
    }
    // endregion     
}
