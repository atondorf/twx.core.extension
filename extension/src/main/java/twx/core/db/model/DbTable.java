package twx.core.db.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;

public class DbTable extends DbObject<DbSchema> {

    private static final long serialVersionUID = 1L;

    private final LinkedHashMap<String, DbColumn> columns = new LinkedHashMap<String, DbColumn>();
    private final LinkedHashMap<String, DbIndex> indexes = new LinkedHashMap<String, DbIndex>();
    private final LinkedHashMap<String, DbForeignKey> foreignKeys = new LinkedHashMap<String, DbForeignKey>();
    
    private String      primaryKey = null;
    private String      dataShapeName = null;

    public DbTable(String name) {
        super(null, name);
        this.dataShapeName = null;
    };

    protected DbTable(DbSchema schema, String name) {
        super(schema, name);
        this.dataShapeName = null;
    };

    // region Get/Set Table Properties 
    // --------------------------------------------------------------------------------
    public String getSchemaName() {
        if( this.isRoot() )
            return null;
        return this.getParent().getName();
    }
    
    public String getDataShapeName() {
        return this.dataShapeName;
    }

    public void setDataShapeName(String dataShapeName) {
        this.dataShapeName = dataShapeName;
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
    public Boolean hasPrimaryKey() {
        return this.primaryKey != null;
    }

    public DbIndex getPrimaryKey() {
        if( this.primaryKey == null )
            return null;
        return this.indexes.get(this.primaryKey);
    }

    public void setPrimaryKey(String keyName) {
        if( this.indexes.containsKey(keyName) ) 
            this.primaryKey = keyName;
    }

    public DbIndex addPrimaryKey() {
        if( this.primaryKey != null )
            throw new DbModelException("Table already has a primary Key");
        String keyName = this.getName() + "_PK";
        DbIndex pidx = this.addIndex(keyName);
        pidx.setUnique(true);
        return pidx;
    }

    public DbIndex removePrimaryKey() {
        if( this.primaryKey == null )
            return null;
        return this.indexes.remove(this.primaryKey);
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
    // region Serialization ... 
    // --------------------------------------------------------------------------------
    public DbColumn addColumnFromJSON(JSONObject json) {
        if( !json.has(DbConstants.MODEL_TAG_NAME) ) 
            throw new DbModelException("JSON does not define a tag 'name'");
        DbColumn col = new DbColumn(this, json.getString(DbConstants.MODEL_TAG_NAME));
        col.fromJSON(json);
        return addColumn(col);
    }
    
    public DbIndex addIndexFromJSON(JSONObject json) {
        if( !json.has(DbConstants.MODEL_TAG_NAME) ) 
            throw new DbModelException("JSON does not define a tag 'name'");
        DbIndex index = new DbIndex(this, json.getString(DbConstants.MODEL_TAG_NAME));
        index.fromJSON(json);
        // if the index is unique ... it's the primary key ... 
        if( index.isUnique() )
            this.primaryKey = index.getName();
        return addIndex(index);
    }

    public DbForeignKey addForeignKeyFromJSON(JSONObject json) {
        if( !json.has(DbConstants.MODEL_TAG_NAME) ) 
            throw new DbModelException("JSON does not define a tag 'name'");
        DbForeignKey fkKey = new DbForeignKey(this, json.getString(DbConstants.MODEL_TAG_NAME));
        fkKey.fromJSON(json);
        return addForeignKey(fkKey);
        // return null;
    }

    @Override
    public DbTable fromJSON(JSONObject json) {
        super.fromJSON(json);
        // read the columns ... 
        if( json.has(DbConstants.MODEL_TAG_COLUMN_ARRAY)) {
            JSONArray columns = json.getJSONArray(DbConstants.MODEL_TAG_COLUMN_ARRAY);
            columns.forEach( item -> {
                this.addColumnFromJSON((JSONObject)item);
            });
        }
        
        // read primary key if set ... is the same as an INDEX ... 
        if( json.has(DbConstants.MODEL_TAG_TABLE_PRIMARY_KEY)) 
            this.primaryKey = json.getString(DbConstants.MODEL_TAG_TABLE_PRIMARY_KEY);

         if( json.has(DbConstants.MODEL_TAG_TABLE_DATASHAPE_NAME)) 
            this.dataShapeName = json.getString(DbConstants.MODEL_TAG_TABLE_DATASHAPE_NAME);

        // read the indexes ... 
        if( json.has(DbConstants.MODEL_TAG_INDEX_ARRAY)) {
            JSONArray indexes = json.getJSONArray(DbConstants.MODEL_TAG_INDEX_ARRAY);
            indexes.forEach( item -> {
                this.addIndexFromJSON((JSONObject)item);
            });
        }
        if( json.has(DbConstants.MODEL_TAG_FKKEYS_ARRAY)) {
            JSONArray fkKeys = json.getJSONArray(DbConstants.MODEL_TAG_FKKEYS_ARRAY); 
            fkKeys.forEach( item -> {
                this.addForeignKeyFromJSON((JSONObject)item);
            }); 
        }
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var columns = new JSONArray();
        for (DbColumn column : this.columns.values()) {
            columns.put(column.toJSON());
        }
        json.put(DbConstants.MODEL_TAG_COLUMN_ARRAY, columns);
        json.put(DbConstants.MODEL_TAG_TABLE_PRIMARY_KEY, this.primaryKey );

        var indexes = new JSONArray();
        for (DbIndex index : this.indexes.values()) {
            indexes.put(index.toJSON());
        }
        if( indexes.length() > 0 )
            json.put(DbConstants.MODEL_TAG_INDEX_ARRAY, indexes);

        var foreignKeys = new JSONArray();
        for (DbForeignKey fk : this.foreignKeys.values()) {
            foreignKeys.put(fk.toJSON());
        }
        if( foreignKeys.length() > 0 )
            json.put(DbConstants.MODEL_TAG_FKKEYS_ARRAY, foreignKeys);
        return json;
    }
    // endregion 
}
