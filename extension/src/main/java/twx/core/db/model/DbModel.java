package twx.core.db.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbModel extends DbObject<DbObject<?>> {
    private final Set<DbSchema> schemas = new LinkedHashSet<>();

    public DbModel() {
        super(null, "");
        this.createSchema(DbSchema.DEFAULT_SCHEMA_NAME);
    }

    public DbModel(String name) {
        super(null, name);
        this.createSchema(DbSchema.DEFAULT_SCHEMA_NAME);
    };
    
    @Override
    public void clear() {
        super.clear();
        this.schemas.stream().forEach(c -> c.clear());
        this.schemas.clear();
        schemas.clear();
        this.createSchema(DbSchema.DEFAULT_SCHEMA_NAME);
    }
    // region Get/Set Properties
    // --------------------------------------------------------------------------------

    // endretion
    // region Get/Set Schemas
    // --------------------------------------------------------------------------------
    public Set<DbSchema> getSchemas() {
        return Collections.unmodifiableSet(schemas);
    }

    public Boolean hasSchema(String name) {
        return DbObject.hasObject(this.schemas, name);
    }

    public DbSchema getSchema(String name) {
        return DbObject.findObject(this.schemas, name);
    }

    public DbSchema createSchema(String name) {
        DbSchema schema = new DbSchema(this, name);
        return addSchema(schema);
    }

    public DbSchema getDefaultSchema() {
        return getSchema(DbSchema.DEFAULT_SCHEMA_NAME);
    }

    public DbSchema getOrCreateSchema(String name) {
        var schema = getSchema(name);
        if (schema == null)
            schema = createSchema(name);
        return schema;
    }

    public DbSchema removeSchema(String name) {
        var schema = getSchema(name);
        return removeSchema(schema);
    }

    public DbSchema addSchema(DbSchema schema) {
        schema.takeOwnerShip(this);
        this.schemas.add(schema);
        return schema;
    }

    public DbSchema removeSchema(DbSchema schema) {
        this.schemas.remove(schema);
        schema.parent = null;
        return schema;
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbModel mergeWith(DbModel other) throws DbModelException {
        return this;
    }

    public Boolean initialize() throws DbModelException {
        return true;
    }

    public Boolean validate() throws DbModelException {
        return true;
    }
    // endregion
    // region Compare and Hash ... used to keep Objects in Set<> ...
    // --------------------------------------------------------------------------------
    
    // Nothing to do here ... use DbObject implementation by getName only ...
    
    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (DbSchema schema : this.schemas) {
            array.put(schema.toJSON());
        }
        json.put(DbConstants.MODEL_TAG_SCHEMA_ARRAY, array);
        return json;
    }

    @Override
    public String toString() {
        return this.toJSON().toString(2);
    }
    // endregion
}
