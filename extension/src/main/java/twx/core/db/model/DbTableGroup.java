package twx.core.db.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;

public class DbTableGroup extends DbObject<DbSchema> {
	private final Set<DbTable> tables = new LinkedHashSet<>();

    public DbTableGroup(String name) {
        super(null, name);
    }

    protected DbTableGroup(DbSchema parent, String name) {
        super(parent, name);
    }

    @Override
    public void clear() {
        super.clear();
        // do not clean the tables ... 
        this.tables.clear();
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public DbSchema getSchema() {
        return (DbSchema) this.getParent();
    }
    // endregion 
    // region Get/Set Tables ... 
    // --------------------------------------------------------------------------------
    public boolean addTable(final DbTable table) {
		return tables.add(table);
	}
	
	public Set<DbTable> getTables() {
		return Collections.unmodifiableSet(tables);
	}
    
    // endregion 
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbTableGroup mergeWith(DbTableGroup other) throws DbModelException {
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
        final DbTableGroup that = (DbTableGroup) obj;
        return this.getSchema().equals(that.getSchema()) && this.getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSchema(), this.getName());
    }

    @Override
    public String toString() {
        return DbNameUtil.of(this.getSchema(), this.getName() );
    }

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public DbTableGroup fromJSON(JSONObject json) {
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
