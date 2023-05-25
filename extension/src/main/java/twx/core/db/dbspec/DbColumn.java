package twx.core.db.dbspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbColumn extends DbObject<DbTable> {

    protected   String  type;
    protected   int     lenght;
    protected   Boolean unique;
    protected   Boolean notNull;
    protected   Boolean identity;

    public DbColumn(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        
        return json;
    }

}
