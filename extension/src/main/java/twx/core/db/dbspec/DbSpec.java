package twx.core.db.dbspec;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;


public class DbSpec extends DbObject<DbObject<?>> {
    
    public static final String DEFAULT_SCHEMA_NAME = "dbo";

    private final LinkedHashMap<String,DbSchema> schemas = new LinkedHashMap<String,DbSchema>();

    public DbSpec(String name) {
        super(null, name);
        addSchema(DEFAULT_SCHEMA_NAME);
    };

    public List<DbSchema> getSchemas() {
        return new ArrayList<DbSchema>( this.schemas.values() );
    }

    public DbSchema getDefaultSchema() {
        return getSchema(DEFAULT_SCHEMA_NAME);
    }

    public DbSchema getSchema(String name) {
        return this.schemas.get(name);
    }

    public DbSchema addSchema(String name) {
        DbSchema schema = createSchema(name);
        return addSchema(schema);
    }

    public DbSchema createSchema(String name) {
        return new DbSchema(this, name);
    }

    protected <T extends DbSchema> T addSchema(T schema) {
        super.addChild(schema);
        this.schemas.put(schema.getName(), schema);
        return schema;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for ( DbSchema schema : this.schemas.values() ) {
            array.put(schema.toJSON());
        }
        json.put("schemas",array);
        return json;        
    }

}
