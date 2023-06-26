package twx.core.db.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbModel extends DbObject<DbObject<?>> {

    public static final String DEFAULT_SCHEMA_NAME = "dbo";

    private final LinkedHashMap<String, DbSchema> schemas = new LinkedHashMap<String, DbSchema>();

    protected String application;

    public DbModel(String name) {
        super(null, name);
        this.application = name;
    };

    public DbModel(String name,String application) {
        super(null, name);
        this.application = application;
    };

    public String getApplication() {
        return this.application;
    }

    public void setApplication(String name) {
        this.application = name;
    }

    public List<DbSchema> getSchemas() {
        return new ArrayList<DbSchema>(this.schemas.values());
    }

    public DbSchema getDefaultSchema() {
        return getSchema(DEFAULT_SCHEMA_NAME);
    }

    public Set<String> getSchemaNames() {
        return schemas.keySet();
    }

    public DbSchema getSchema(String name) {
        return this.schemas.get(name);
    }

    public Boolean hasSchema(String name) {
        return this.schemas.get(name) != null;
    }

    public Boolean removeSchema(String name) {
        DbSchema schema = this.schemas.get(name);
        if (schema != null) {
            removeSchema(schema);
        }
        return false;
    }

    public DbSchema addSchema(String name) {
        DbSchema schema = createSchema(name);
        return addSchema(schema);
    }

    public DbSchema getOrAddSchema(String name) {
        DbSchema schema = this.schemas.get(name);
        if (schema == null) {
            schema = addSchema(name);
        }
        return schema;
    }

    public DbSchema createSchema(String name) {
        return new DbSchema(this, name);
    }

    protected <T extends DbSchema> T addSchema(T schema) {
        this.schemas.put(schema.getName(), schema);
        return schema;
    }

    protected <T extends DbSchema> T removeSchema(T schema) {
        this.schemas.remove(schema.getName());
        return schema;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for (DbSchema schema : this.schemas.values()) {
            array.put(schema.toJSON());
        }
        json.put("application", application );
        json.put("schemas", array);
        return json;
    }

    @Override
    public String toString() {
        return this.toJSON().toString(2);
    }
}
