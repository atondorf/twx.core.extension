package twx.core.db.model;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.json.JSONArray;
import org.json.JSONObject;

import twx.core.db.model.settings.DbIndexSetting;
import twx.core.db.model.settings.SettingHolder;

public class DbIndex extends DbObject<DbTable> implements SettingHolder<DbIndexSetting> {
    private final Map<DbIndexSetting, String> settings = new EnumMap<>(DbIndexSetting.class);
    private final List<DbIndexColumn> indexColumns = new LinkedList<>();

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
        this.settings.clear();
    }

    public DbTable getTable() {
        return (DbTable) this.getParent();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // region Get/Set Settings ...
    // --------------------------------------------------------------------------------
    @Override
    public void addSetting(DbIndexSetting settingKey, String value) {
        this.settings.put(settingKey, value);
    }

    public String getSetting(DbIndexSetting settingKey) {
        return this.settings.get(settingKey);
    }

    public Map<DbIndexSetting, String> getSettings() {
        return Collections.unmodifiableMap(this.settings);
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
    public DbIndex mergeWith(DbIndex other) throws DbModelException {
        return this;
    }

    public Boolean initialize() throws DbModelException {
        return true;
    }

    public Boolean validate() throws DbModelException {
        return true;
    }

    // endregion
    // region Compare and Hash ...
    // --------------------------------------------------------------------------------
    /*
     * @Override
     * public boolean equals(final Object obj) {
     * if (this == obj)
     * return true;
     * if (obj == null || getClass() != obj.getClass())
     * return false;
     * final DbTable that = (DbTable) obj;
     * return Objects.equals(this.getSchema(), that.getSchema() ) &&
     * this.getName().equals(that.getName());
     * }
     * 
     * @Override
     * public int hashCode() {
     * return Objects.hash(this.getSchema(), this.getName());
     * }
     */
    @Override
    public String toString() {
        /*
         * return columns.entrySet()
         * .stream()
         * .map(e -> e.getValue() == null ? '`' + e.getKey() + '`' : e.getKey())
         * .collect(Collectors.joining(", ", "(", ")"));
         */ return null;
    }
    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------

    @Override
    public DbIndex fromJSON(JSONObject json) {
        super.fromJSON(json);

        return this;
    }

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
        this.settings.entrySet().stream().forEach( s -> {
            json.put( s.getKey().label, s.getValue() );
        });

        return json;
    }
    // endregion
}
