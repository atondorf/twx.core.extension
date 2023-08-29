package twx.core.db2.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.things.database.DBConstants;

public class DbModel extends DbObject<DbObject<?>> {
    
    private static final long serialVersionUID = -1L;
    
    private String version = "0";
    private final LinkedHashMap<String, DbSchema> schemas = new LinkedHashMap<String, DbSchema>();

    public DbModel() {
        super(null,"");
        this.addSchema(DbConstants.DEFAULT_SCHEMA_NAME);
    }

    public DbModel(String name) {
        super(null, name);
        this.addSchema(DbConstants.DEFAULT_SCHEMA_NAME);
    };

    @Override
    public void clear() {
        super.clear();
        this.version = "0";
        for (var entry : this.schemas.entrySet() ) {
            entry.getValue().clear();
        }
        schemas.clear();
        this.addSchema(DbConstants.DEFAULT_SCHEMA_NAME);
    }

    // region Get/Set Properties 
    // --------------------------------------------------------------------------------
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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

    // region Get/Set Schemas 
    // --------------------------------------------------------------------------------
    public List<DbSchema> getSchemas() {
        return new ArrayList<DbSchema>(this.schemas.values());
    }

    public Set<String> getSchemaNames() {
        return schemas.keySet();
    }

    public DbSchema addSchema(String name) {
        DbSchema schema = new DbSchema(this, name);
        return internAddSchema(schema);
    }
    
    public DbSchema getOrAddSchema(String name) {
        DbSchema schema = this.schemas.get(name);
        if (schema == null) {
            schema = addSchema(name);
        }
        return schema;
    }

    public DbSchema addSchema(DbSchema schema) {
        return internAddSchema(schema);
    }

    public DbSchema getDefaultSchema() {
        return getSchema(DbConstants.DEFAULT_SCHEMA_NAME);
    }

    public DbSchema getSchema(String name) {
        if( name == null )
            return this.getDefaultSchema();
        return this.schemas.get(name);
    }

    public Boolean hasSchema(String name) {
        return this.schemas.get(name) != null;
    }

    public Boolean removeSchema(String name) {
        DbSchema schema = this.schemas.get(name);
        if (schema != null) {
            internRemoveSchema(schema);
            return true;
        }
        return false;
    }
    
    // endregion
    // region Get/Set Tables 
    // --------------------------------------------------------------------------------


    // endregion
    // region Get/Set Columns 
    // --------------------------------------------------------------------------------


    // endregion
    // region Get/Set Indexes
    // --------------------------------------------------------------------------------


    // endregion

    // region Get/Set FK-Keys 
    // --------------------------------------------------------------------------------


    // endregion

    // region 
    // --------------------------------------------------------------------------------
    protected <T extends DbSchema> T internAddSchema(T schema) {
        schema.takeOwnerShip(this);
        this.schemas.put(schema.getName(), schema);
        return schema;
    }

    protected <T extends DbSchema> T internRemoveSchema(T schema) {
        this.schemas.remove(schema.getName());
        return schema;
    }
    // endregion 


    // region Serialization ... 
    // --------------------------------------------------------------------------------
    public DbSchema addSchemaFromJSON(JSONObject json) {
        if( !json.has(DbConstants.MODEL_TAG_NAME) ) 
            throw new DbModelException("JSON does not define a tag 'name'");
        DbSchema schema = new DbSchema(this, json.getString(DbConstants.MODEL_TAG_NAME));
        schema.fromJSON(json);
        return internAddSchema(schema);
    }

    @Override
    public DbModel fromJSON(JSONObject json) {
        // clear the model first ... 
        this.clear();
        // now load from JSON ... 
        super.fromJSON(json);
        if( json.has(DbConstants.MODEL_TAG_SCHEMA_ARRAY) ) {
            JSONArray schemas = json.getJSONArray(DbConstants.MODEL_TAG_SCHEMA_ARRAY);
            schemas.forEach( item -> { 
                this.addSchemaFromJSON((JSONObject)item);
            });
        }        
        return this;
    }
    
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (DbSchema schema : this.schemas.values()) {
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
