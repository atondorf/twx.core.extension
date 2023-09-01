package twx.core.db.model;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Objects;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.json.JSONObject;

import twx.core.db.model.settings.DbIndexSetting;
import twx.core.db.model.settings.SettingHolder;

public class DbIndex extends DbObject<DbTable> implements SettingHolder<DbIndexSetting> {
    private final Map<DbIndexSetting, String> settings = new EnumMap<>(DbIndexSetting.class);
    private final Set<DbIndexColumn> columns = new LinkedHashSet<>();

    public DbIndex(String name) {
        super(null, name);
    }

    protected DbIndex(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public void clear() {
        super.clear();
        this.columns.clear();
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
    // reggion Internal Helpers
    // --------------------------------------------------------------------------------
    protected class DbIndexColumn {
        public DbColumn column;
        public Integer  ordinal = 0;

        public DbIndexColumn(DbColumn column, Integer ordinal) {
            this.column = column;
            this.ordinal = ordinal;
        }

        @Override
        public String toString() {
            return column.getName();
        }
    }
    protected Boolean hasColumn(final String name ) {
        return columns.stream().anyMatch(c -> c.toString().equals(name));
    }

    protected DbIndexColumn getColumn(final String name ) {
        return columns.stream().filter(c -> c.toString().equals(name)).findAny().orElse(null);
    }

    protected void setColumns( Collection<DbColumn> columns ) {
        this.columns.clear();
        columns.stream().forEach( col -> this.columns.add( new DbIndexColumn(col, this.columns.size())));
    }

    // endregion 
    public Set<DbColumn> getColumns() {
        Set<DbColumn> set = new LinkedHashSet<>();
        this.columns.stream().sorted( (o1,o2)-> o1.ordinal.compareTo(o2.ordinal)).forEach(idx -> set.add(idx.column));
        return Collections.unmodifiableSet(set);
    }
    
    public Set<DbIndexColumn> getColumnsOrdinal() {
        return Collections.unmodifiableSet(columns);
    }

    public Integer addColumn(final String name) {
        Integer ordinal = columns.size();
        if( hasColumn(name) )
            return -1;
        var column = this.getTable().getColumn(name);
        if( column == null )
            return -1;
        columns.add( new DbIndexColumn(column, ordinal) );
            return ordinal;
    }

    public DbColumn removeColumn(final String name ) {
        DbIndexColumn column = this.getColumn(name);
        if( column == null )
            return null;
        this.columns.remove(column);
        return column.column;
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

        return json;
    }
    // endregion
}
