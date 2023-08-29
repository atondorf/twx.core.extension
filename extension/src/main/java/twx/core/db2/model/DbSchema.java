package twx.core.db2.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbSchema extends DbObject<DbModel> {
    
    private static final long serialVersionUID = 1L;

    private final LinkedHashMap<String,DbTable> tables = new LinkedHashMap<String,DbTable>();

    public DbSchema(String name) {
        super(null,name);
    }

    protected DbSchema(DbModel spec, String name) {
        super(spec,name);
    };
    
    @Override
    public void clear() {
        super.clear();
        for (var entry : this.tables.entrySet() ) {
            entry.getValue().clear();
        }
        tables.clear();
    }
    
    // region Get/Set Tables 
    // --------------------------------------------------------------------------------
    public List<DbTable> getTables() {
        return new ArrayList<DbTable>( this.tables.values() );
    }

    public DbTable getTable(String name) {
        return this.tables.get(name);
    }

    public DbTable addTable(String name) {
        DbTable table = createTable(name);
        return internAddTable(table);
    }

    public DbTable addTable(DbTable table) {
        return internAddTable(table);
    }

    public DbTable createTable(String name) {
        return new DbTable(this, name);
    }
    // endregion 

    protected <T extends DbTable> T internAddTable(T table) {
        table.takeOwnerShip(this);
        this.tables.put(table.getName(), table);
        return table;
    }

    protected <T extends DbSchema> T internRemoveTable(T table) {
        this.tables.remove(table.getName());
        return table;
    }
    // region Serialization ... 
    // --------------------------------------------------------------------------------
    public DbTable addTableFromJSON(JSONObject json) {
        if( !json.has(DbConstants.MODEL_TAG_NAME) ) 
            throw new DbModelException("JSON does not define a tag 'name'");
        DbTable table = new DbTable(this, json.getString(DbConstants.MODEL_TAG_NAME));
        table.fromJSON(json);
        return internAddTable(table);
    }

    @Override
    public DbSchema fromJSON(JSONObject json) {
        super.fromJSON(json);
        if( json.has("tables") ) {
            JSONArray tables = json.getJSONArray(DbConstants.MODEL_TAG_TABLE_ARRAY);
            tables.forEach( item -> {
                addTableFromJSON((JSONObject)item);
            }); 
        }
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for ( DbTable table : this.tables.values() ) {
            array.put(table.toJSON());
        }
        json.put(DbConstants.MODEL_TAG_TABLE_ARRAY,array);
        return json;        
    }
    // endregion 
}
