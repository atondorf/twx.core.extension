package twx.core.db.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbEnum extends DbObject<DbSchema> {
    private final Set<DbEnumValue> values = new LinkedHashSet<>();

    public DbEnum(DbSchema parent, String name) {
        super(parent, name);
    }

    @Override
    public void clear() {
        super.clear();
        this.values.stream().forEach(c -> c.clear());
        this.values.clear();
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public DbSchema getSchema() {
        return (DbSchema) this.getParent();
    }

    public DbEnumValue addValue(final String name) {
        var value = new DbEnumValue(this, name);
        this.values.add(value);
        return value;
    }

    public Set<DbEnumValue> getValues() {
        return Collections.unmodifiableSet(values);
    }
    // endregion 
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbEnum mergeWith(DbEnum other) throws DbModelException {
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
        final DbEnum anEnum = (DbEnum) obj;
        return this.getSchema().equals(anEnum.getSchema()) && this.getName().equals(anEnum.getName());
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
    public DbEnum fromJSON(JSONObject json) {
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
