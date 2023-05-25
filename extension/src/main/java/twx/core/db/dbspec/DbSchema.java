package twx.core.db.dbspec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbSchema extends DbObject<DbSpec> {
    
    private final LinkedHashMap<String,DbTable> tables = new LinkedHashMap<String,DbTable>();

    public DbSchema(DbSpec spec, String name) {
        super(spec,name);
    };
    
    public List<DbTable> getTables() {
        return new ArrayList<DbTable>( this.tables.values() );
    }

    public DbTable getTable(String name) {
        return this.tables.get(name);
    }

    public DbTable addTable(String name) {
        DbTable table = createTable(name);
        return addTable(table);
    }

    protected <T extends DbTable> T addTable(T table) {
        super.addChild(table);
        this.tables.put(table.getName(), table);
        return table;
    }

    public DbTable createTable(String name) {
        return new DbTable(this, name);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var array = new JSONArray();
        for ( DbTable table : this.tables.values() ) {
            array.put(table.toJSON());
        }
        json.put("tables",array);
        return json;        
    }

}
