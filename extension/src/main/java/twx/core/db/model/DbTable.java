package twx.core.db.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbTable extends DbObject<DbSchema> {

    private final LinkedHashMap<String, DbColumn> columns = new LinkedHashMap<String, DbColumn>();
    private final LinkedHashMap<String, DbIndex> indexes = new LinkedHashMap<String, DbIndex>();
    private final LinkedHashMap<String, DbForeignKey> foreignKeys = new LinkedHashMap<String, DbForeignKey>();
    private final DbIndex   primaryKey = new DbIndex(this, name);

    private String      schemaName;
    private String      dataShapeName;
    private JSONObject  dbInfo;

    protected DbTable(DbSchema schema, String name) {
        super(schema, name);
        this.schemaName = schema.getName();
        this.dataShapeName = null;
        this.dbInfo = null;
    };

    // region Get/Set Table Properties 
    // --------------------------------------------------------------------------------
    public String getSchemaName() {
        return this.schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDataShapeName() {
        return this.dataShapeName;
    }

    public void setDataShapeName(String dataShapeName) {
        this.dataShapeName = dataShapeName;
    }

    public JSONObject getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(JSONObject dbInfo) {
        this.dbInfo = dbInfo;
    }
    // endregion

    
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
        this.columns.put(table.getName(), table);
        return table;
    }
    // endregion

    // region Indexes
    // --------------------------------------------------------------------------------
    public DbIndex getPrimaryKey() {
        return primaryKey;
    }

    // region Indexes
    // --------------------------------------------------------------------------------

    public List<DbIndex> getIndexes() {
        return new ArrayList<DbIndex>(this.indexes.values());
    }

    public DbIndex getIndex(String name) {
        return this.indexes.get(name);
    }

    public DbIndex addIndex(String name) {
        DbIndex index = createIndex(name);
        return addIndex(index);
    }

    public DbIndex getOrAddIndex(String name) {
        DbIndex index = getIndex(name);
        if( index == null ) {
            index = this.addIndex(name);
        }
        return index;
    }

    public DbIndex createIndex(String name) {
        return new DbIndex(this, name);
    }

    protected <T extends DbIndex> T addIndex(T index) {
        this.indexes.put(index.getName(), index);
        return index;
    }
    // endregion

    // region Indexes
    // --------------------------------------------------------------------------------
    public List<DbForeignKey> getForeignKeys() {
        return new ArrayList<DbForeignKey>(this.foreignKeys.values());
    }

    public DbForeignKey getForeignKey(String name) {
        return this.foreignKeys.get(name);
    }
    
    public DbForeignKey addForeignKey(String name) {
        DbForeignKey fk = createForeignKey(name);
        return addForeignKey(fk);
    }

    public DbForeignKey getOrAddForeignKey(String name) {
        DbForeignKey fk = getForeignKey(name);
        if( fk == null ) {
            fk = this.addForeignKey(name);
        }
        return fk;
    }

    public DbForeignKey createForeignKey(String name) {
        return new DbForeignKey(this, name);
    }

    protected <T extends DbForeignKey> T addForeignKey(T foreignKey) {
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

        json.put( "primaryKey", this.primaryKey.toJSON() );

        var indexes = new JSONArray();
        for (DbIndex index : this.indexes.values()) {
            indexes.put(index.toJSON());
        }
        json.put("indexes", indexes);

        var foreignKeys = new JSONArray();
        for (DbForeignKey fk : this.foreignKeys.values()) {
            foreignKeys.put(fk.toJSON());
        }
        json.put("foreignKeys", foreignKeys);
        return json;
    }
}
