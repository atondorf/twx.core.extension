package twx.core.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbIndex extends DbObject<DbTable> {
    
    protected DbTable table;
    protected List<DbColumn> columns = new ArrayList<DbColumn>();
    protected boolean  unique;

    public DbIndex(DbTable table, String name) {
        super(table,name);
        this.table = table;
    };

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        
        return json;        
    }
}
