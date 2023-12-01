package twx.core.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbForeignKey extends DbObject<DbTable> {
    // Internal Enum for FK-Rules
    // --------------------------------------------------------------------------------
    public enum FkRule {
        Cascade(0, "cascade"), Restrict(1, "restrict"), SetNull(2, "setNull"), NoAction(3, "noAction"), SetDefault(4, "setDefault");

        public Integer key;
        public String label;

        private FkRule(Integer key, String label) {
            this.key = key;
            this.label = label;
        }

        public static FkRule getByKey(Integer key) {
            for (FkRule e : values()) {
                if (e.key.equals(key)) {
                    return e;
                }
            }
            return null;
        }

        public static FkRule getByLabel(String label) {
            for (FkRule e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }

    // endregion
    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------    
    private final List<DbForeignKeyColumn> foreignKeyColumns = new LinkedList<>();
    protected String foreignSchemaName  = "";
    protected String foreignTableName   = "";
    protected FkRule onUpdate           = null;
    protected FkRule onDelete           = null;

    public DbForeignKey(String name) {
        super(null, name);
    }

    protected DbForeignKey(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public void clear() {
        super.clear();
        this.foreignKeyColumns.stream().forEach(c -> c.clear());
        this.foreignKeyColumns.clear();
    }

    public DbTable getTable() {
        return (DbTable)this.getParent();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // endregion 
    // region Get/Set Table ForeignKeyProperties
    // --------------------------------------------------------------------------------
    public void setOnUpdate(int onUpdate) {
        this.onUpdate = FkRule.getByKey(onUpdate);
    }

    public void setOnDelete(int onDelete) {
        this.onDelete = FkRule.getByKey(onDelete);
    }

    public int getOnUpdate() {
        return onUpdate.key;
    }

    public int getOnDelete() {
        return onDelete.key;
    }

    public void setForeignSchemaName(String foreignSchemaName) {
        this.foreignSchemaName = foreignSchemaName;
    }

    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public String getForeignSchemaName() {
        return foreignSchemaName;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }
    // endregion    
    // region Get/Set Columns ...
    // --------------------------------------------------------------------------------
    public Boolean hasForeignKeyColumn(final String name ) {
        return DbObject.hasObject(this.foreignKeyColumns, name);
    }

    public DbForeignKeyColumn getForeignKeyColumn(final String name ) {
        return DbObject.findObject(this.foreignKeyColumns, name);
    }

    public List<DbForeignKeyColumn> getForeignKeyColumns() {
        return Collections.unmodifiableList(foreignKeyColumns);
    }

    public DbForeignKeyColumn createForeignKeyColumn(final String name) {
        Integer ordinal = foreignKeyColumns.size();
        DbForeignKeyColumn idxColumn = new DbForeignKeyColumn(this, name);
        idxColumn.setOrdinal(ordinal);
        // search for the column in table ...        
        var column = this.getTable().getColumn(name);
        if( column == null )
            return null;
        return addForeignKeyColumn(idxColumn);
    }

    public DbForeignKeyColumn getOrCreateForeignKeyColumn(String name) {
        var idxColumn = getForeignKeyColumn(name);
        if (idxColumn == null)
            idxColumn = createForeignKeyColumn(name);
        return idxColumn;
    }

    public DbForeignKeyColumn removeColumn(final String name ) {
        DbForeignKeyColumn idxColumn = this.getForeignKeyColumn(name);
        if( idxColumn != null )
            this.foreignKeyColumns.remove(idxColumn);
        return idxColumn;
    }

    public DbForeignKeyColumn addForeignKeyColumn(DbForeignKeyColumn idxColumn) {
        idxColumn.takeOwnerShip(this);
        this.foreignKeyColumns.add(idxColumn);
        return idxColumn;
    }

    public DbForeignKeyColumn removeForeignKeyColumn(DbForeignKeyColumn idxColumn) {
        this.foreignKeyColumns.remove(idxColumn);
        idxColumn.parent = null;
        return idxColumn;
    }

    public void sortForeignKeyColumns() {
        this.foreignKeyColumns.sort( (c1,c2)->c1.ordinal.compareTo(c2.ordinal));
    }
    // endregion
    // region Serialization ... 
    // --------------------------------------------------------------------------------
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var colArray = new JSONArray();
        json.put(DbConstants.MODEL_TAG_INDEX_FOREIGN_SCHEMA, this.foreignSchemaName);
        json.put(DbConstants.MODEL_TAG_INDEX_FOREIGN_TABLE, this.foreignTableName);
        if( this.onUpdate != null )
            json.put(DbConstants.MODEL_TAG_INDEX_ON_UPDATE, this.onUpdate.label);
        if( this.onDelete != null )
            json.put(DbConstants.MODEL_TAG_INDEX_ON_DELETE, this.onDelete.label);
        // add Columns ... 
        var array = new JSONArray();
        for (DbForeignKeyColumn col : this.foreignKeyColumns ) {
            array.put( col.toJSON() );
        }
        json.put(DbConstants.MODEL_TAG_COLUMN_ARRAY, array);
       
        return json;
    }
    // endregion

}
