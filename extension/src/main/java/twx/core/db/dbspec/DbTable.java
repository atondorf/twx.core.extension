package twx.core.db.dbspec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbTable extends DbObject<DbSchema> {

    private final LinkedHashMap<String, DbColumn> columns = new LinkedHashMap<String, DbColumn>();
    private final LinkedHashMap<String, DbIndex> indexes = new LinkedHashMap<String, DbIndex>();
    private final LinkedHashMap<String, DbForeignKey> foreignKeys = new LinkedHashMap<String, DbForeignKey>();

    private String      dataShapeName;
    private JSONObject  dbInfo;

    public DbTable(DbSchema schema, String name) {
        super(schema, name);
        dataShapeName = null;
        dbInfo = null;
    };

    // region Columns
    // --------------------------------------------------------------------------------
    public List<DbColumn> getColumns() {
        return new ArrayList<DbColumn>(this.columns.values());
    }

    public DbColumn getColumn(String name) {
        return this.columns.get(name);
    }

    public DbColumn createColumn(String name) {
        return new DbColumn(this, name);
    }

    public DbColumn addColumn(String name) {
        DbColumn table = createColumn(name);
        return addColumn(table);
    }

    protected <T extends DbColumn> T addColumn(T table) {
        super.addChild(table);
        this.columns.put(table.getName(), table);
        return table;
    }
    // endregion

    // region Indexes
    // --------------------------------------------------------------------------------
    public List<DbIndex> getIndexes() {
        return new ArrayList<DbIndex>(this.indexes.values());
    }

    public DbIndex getIndex(String name) {
        return this.indexes.get(name);
    }

    public DbIndex addIndex(String name) {
        DbIndex table = createIndex(name);
        return addIndex(table);
    }

    public DbIndex createIndex(String name) {
        return new DbIndex(this, name);
    }

    protected <T extends DbIndex> T addIndex(T index) {
        super.addChild(index);
        this.indexes.put(index.getName(), index);
        return index;
    }
    // endregion

    // region Indexes
    // --------------------------------------------------------------------------------
    public List<DbForeignKey> getForeignKeyes() {
        return new ArrayList<DbForeignKey>(this.foreignKeys.values());
    }

    public DbForeignKey getForeignKey(String name) {
        return this.foreignKeys.get(name);
    }
    
    public DbForeignKey addForeignKey(String name) {
        DbForeignKey table = createForeignKey(name);
        return addForeignKey(table);
    }

    public DbForeignKey createForeignKey(String name) {
        return new DbForeignKey(this, name);
    }

    protected <T extends DbForeignKey> T addForeignKey(T foreignKey) {
        super.addChild(foreignKey);
        this.foreignKeys.put(foreignKey.getName(), foreignKey);
        return foreignKey;
    }
    // endregion
    
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var columns = new JSONArray();
        for (DbColumn column : this.columns.values()) {
            columns.put(column.toJSON());
        }
        json.put("columns", columns);

        var indexes = new JSONArray();
        for (DbIndex index : this.indexes.values()) {
            indexes.put(index.toJSON());
        }
        json.put("indexes ", indexes);

        var foreignKeys = new JSONArray();
        for (DbForeignKey fk : this.foreignKeys.values()) {
            columns.put(fk.toJSON());
        }
        json.put("foreignKeys", foreignKeys);
        return json;
    }
}
