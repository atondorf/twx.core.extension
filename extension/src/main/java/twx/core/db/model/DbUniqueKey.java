package twx.core.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbUniqueKey extends DbObject<DbTable> {
    
    protected class Data {
        public String columnName;
        public int keySeq;
        public DbColumn dbColumnn;
    }

    List<String> columns = new ArrayList<String>();

    protected DbUniqueKey(DbTable table, String name) {
        super(table, name);
    };


}
