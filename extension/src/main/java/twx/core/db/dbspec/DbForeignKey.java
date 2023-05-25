package twx.core.db.dbspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbForeignKey extends DbObject<DbTable> {

    protected DbTable fromTable;
    protected DbTable toTable;
    protected DbColumn fromColumn;
    protected DbColumn toColumn;

    public DbForeignKey(DbTable table, String name) {
        super(table, name);
        this.fromTable = table;
    };

    public DbTable getFromTable() {
        return this.fromTable;
    }

    public DbTable getToTable() {
        return this.toTable;
    }

    public DbColumn getFromColumn() {
        return this.fromColumn;
    }

    public DbColumn getToColumns() {
        return this.toColumn;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        return json;        
    }
}
