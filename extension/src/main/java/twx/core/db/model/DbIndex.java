package twx.core.db.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbIndex extends DbObject<DbTable> {
    private final List<DbIndexColumn> indexColumns = new LinkedList<>();
    private Boolean unique = false;
    private Boolean pk = false;

    public DbIndex(String name) {
        super(null, name);
    }

    protected DbIndex(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public void clear() {
        super.clear();
        this.indexColumns.stream().forEach(c -> c.clear());
        this.indexColumns.clear();
    }

    public DbTable getTable() {
        return (DbTable)this.getParent();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // region Get/Set Settings ...
    // --------------------------------------------------------------------------------
    public Boolean isUnique() {
        return this.unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean isPrimarayKey() {
        return this.pk;
    }

    public void setPrimarayKey(Boolean pk) {
        this.pk = pk;
    }    

    // endregion
    // region Get/Set Columns ...
    // --------------------------------------------------------------------------------
    public Boolean hasIndexColumn(final String name ) {
        return DbObject.hasObject(this.indexColumns, name);
    }

    public DbIndexColumn getIndexColumn(final String name ) {
        return DbObject.findObject(this.indexColumns, name);
    }

    public List<DbIndexColumn> getIndexColumns() {
        return Collections.unmodifiableList(indexColumns);
    }

    public DbIndexColumn createIndexColumn(final String name) {
        Integer ordinal = indexColumns.size();
        DbIndexColumn idxColumn = new DbIndexColumn(this, name);
        idxColumn.setOrdinal(ordinal);
        // search for the column in table ...        
        var column = this.getTable().getColumn(name);
        if( column == null )
            return null;
        return addIndexColumn(idxColumn);
    }

    public DbIndexColumn getOrCreateIndexColumn(String name) {
        var idxColumn = getIndexColumn(name);
        if (idxColumn == null)
            idxColumn = createIndexColumn(name);
        return idxColumn;
    }

    public DbIndexColumn removeColumn(final String name ) {
        DbIndexColumn idxColumn = this.getIndexColumn(name);
        if( idxColumn != null )
            this.indexColumns.remove(idxColumn);
        return idxColumn;
    }

    public DbIndexColumn addIndexColumn(DbIndexColumn idxColumn) {
        idxColumn.takeOwnerShip(this);
        this.indexColumns.add(idxColumn);
        return idxColumn;
    }

    public DbIndexColumn removeIndexColumn(DbIndexColumn idxColumn) {
        this.indexColumns.remove(idxColumn);
        idxColumn.parent = null;
        return idxColumn;
    }

    public void sortIndexColumns() {
        this.indexColumns.sort( (c1,c2)->c1.ordinal.compareTo(c2.ordinal));
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------

    // endregion        
    // region Compare and Hash ...
    // --------------------------------------------------------------------------------

    // endregion     
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        // add Columns ... 
        var array = new JSONArray();
        for (DbIndexColumn col : this.indexColumns ) {
            array.put( col.toJSON() );
        }
        json.put(DbConstants.MODEL_TAG_COLUMN_ARRAY, array);
        // add Settings ... 
        json.put(DbConstants.MODEL_TAG_UNIQUE, this.unique );
        json.put(DbConstants.MODEL_TAG_PRIMARY_KEY, this.pk );
        return json;
    }
    // endregion
}
