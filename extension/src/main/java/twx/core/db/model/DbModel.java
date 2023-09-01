package twx.core.db.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.validation.Schema;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.projects.Project;
import com.thingworx.things.database.DBConstants;

public class DbModel extends DbObject<DbObject<?>> {
    private final Set<DbSchema> schemas = new LinkedHashSet<>();
    private final Set<DbRelation> relations = new LinkedHashSet<>();

    private DbProject dbProject = null;

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
        this.relations.stream().forEach(c -> c.clear());
        this.relations.clear();
        this.schemas.stream().forEach(c -> c.clear());
        this.schemas.clear();
        this.dbProject = null;
        schemas.clear();
        this.createSchema(DbSchema.DEFAULT_SCHEMA_NAME);
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public DbProject getProject() {
		return this.dbProject;
	}
	
	public void setProject(final DbProject dbProject) {
		this.dbProject = dbProject;
	}

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
    // region Get/Set Relations ...
    // --------------------------------------------------------------------------------
    public Set<DbRelation> getRelations() {
        return Collections.unmodifiableSet(this.relations);
    }

    public Boolean hasRelation(String name) {
        return this.relations.stream().anyMatch(c -> c.getName().equals(name));
    }

    public DbRelation getRelation(final String name) {
        return this.relations.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    public DbRelation createRelation(String name) {
        DbRelation Relation = new DbRelation(this, name);
        return addRelation(Relation);
    }

    public DbRelation getDefaultRelation() {
        return getRelation(null);
    }

    public DbRelation getOrCreateRelation(String name) {
        var Relation = getRelation(name);
        if (Relation == null)
            Relation = createRelation(name);
        return Relation;
    }

    public DbRelation removeRelation(String name) {
        var Relation = getRelation(name);
        return removeRelation(Relation);
    }

    public DbRelation addRelation(DbRelation relation) {
        relation.takeOwnerShip(this);
        this.relations.add(relation);
        return relation;
    }

    public DbRelation removeRelation(DbRelation relation) {
        this.relations.remove(relation);
        relation.parent = null;
        return relation;
    }

    // endregion
    // region Get/Set Tables ... 
    // --------------------------------------------------------------------------------

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
    // region Compare and Hash ...
    // --------------------------------------------------------------------------------

    // Nothing to do here ... use DbObject implementation by getName only ... 

    // endregion  

    // region Serialization ...
    // --------------------------------------------------------------------------------
    public DbSchema addSchemaFromJSON(JSONObject json) {
        if (!json.has(DbConstants.MODEL_TAG_NAME))
            throw new DbModelException("JSON does not define a tag 'name'");
        DbSchema schema = new DbSchema(this, json.getString(DbConstants.MODEL_TAG_NAME));
        schema.fromJSON(json);
        return addSchema(schema);
    }

    @Override
    public DbModel fromJSON(JSONObject json) {
        // clear the model first ...
        this.clear();
        // now load from JSON ...
        super.fromJSON(json);
        if (json.has(DbConstants.MODEL_TAG_SCHEMA_ARRAY)) {
            JSONArray schemas = json.getJSONArray(DbConstants.MODEL_TAG_SCHEMA_ARRAY);
            schemas.forEach(item -> {
                this.addSchemaFromJSON((JSONObject) item);
            });
        }
        return this;
    }

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
