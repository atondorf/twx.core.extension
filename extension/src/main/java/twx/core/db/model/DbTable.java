package twx.core.db.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;

import twx.core.db.model.settings.DbRelationSetting;
import twx.core.db.model.settings.DbTableSetting;
import twx.core.db.model.settings.SettingHolder;

public class DbTable extends DbObject<DbSchema> implements SettingHolder<DbTableSetting> {
    private final Map<DbTableSetting, String> settings = new EnumMap<>(DbTableSetting.class);
	private final Set<DbColumn> columns = new LinkedHashSet<>();
    private final Set<DbIndex> indexes = new LinkedHashSet<>();
    private String alias = null;    
    private String dataShapeName = null;

    public DbTable(String name) {
        super(null, name);
        this.dataShapeName = null;
    };

    protected DbTable(DbSchema schema, String name) {
        super(schema, name);
        this.dataShapeName = null;
    };
    
    @Override
    public void clear() {
        super.clear();
        this.columns.stream().forEach(c -> c.clear());
        this.columns.clear();
        this.indexes.stream().forEach(c -> c.clear());
        this.indexes.clear();
        this.settings.clear();
    }

    // region Get/Set Settings ... 
    // --------------------------------------------------------------------------------
    @Override
    public void addSetting(DbTableSetting settingKey, String value) {
        settings.put(settingKey, value);
    }

    public String getSetting(DbTableSetting settingKey) {
        return this.settings.get(settingKey);
    }

    public Map<DbTableSetting, String> getSettings() {
        return Collections.unmodifiableMap(settings);
    }
    // endregion
    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public DbSchema getSchema() {
        return (DbSchema)this.getParent();
    }

    public String getSchemaName() {
        return this.getSchema().getName();
    }
    
    public String getDataShapeName() {
        return this.dataShapeName;
    }

    public void setDataShapeName(String dataShapeName) {
        this.dataShapeName = dataShapeName;
    }

	public String getAlias() {
		return alias;
	}
	
	public void setAlias(final String alias) {
		this.alias = alias;
	}
    // endregion
    // region Columns
    // --------------------------------------------------------------------------------
    public Set<DbColumn> getColumns() {
        return Collections.unmodifiableSet(this.columns);
    }

    public Boolean hasColumn(String name) {
        return DbObject.hasObject(this.columns, name);
    }

    public DbColumn getColumn(String name) {
        return DbObject.findObject(this.columns, name);
    }

    public DbColumn createColumn(String name) {
        DbColumn column = new DbColumn(this, name);
        return addColumn(column);
    }

    public DbColumn getOrCreateColumn(String name) {
        var column = getColumn(name);
        if (column == null)
            column = createColumn(name);
        return column;
    }

    public DbColumn removeColumn(String name) {
        var column = getColumn(name);
        return removeColumn(column);
    }

    public DbColumn addColumn(DbColumn column) {
        column.takeOwnerShip(this);
        this.columns.add(column);
        return column;
    }

    public DbColumn removeColumn(DbColumn column) {
        this.columns.remove(column);
        column.parent = null;
        return column;
    }
    // endregion
    // region Indexes
    // --------------------------------------------------------------------------------
    public Set<DbIndex> getIndexes() {
        return Collections.unmodifiableSet(this.indexes);
    }

    public Boolean hasIndexes(String name) {
        return DbObject.hasObject(this.indexes, name);
    }

    public DbIndex getIndex(String name) {
        return DbObject.findObject(this.indexes, name);
    }

    public DbIndex createIndex(String name) {
        DbIndex index = new DbIndex(this, name);
        return addIndex(index);
    }

    public DbIndex getOrCreateIndex(String name) {
        var index = getIndex(name);
        if (index == null)
            index = createIndex(name);
        return index;
    }

    public DbIndex removeIndex(String name) {
        var index = getIndex(name);
        return removeIndex(index);
    }

    public DbIndex addIndex(DbIndex index) {
        index.takeOwnerShip(this);
        this.indexes.add(index);
        return index;
    }

    public DbIndex removeIndex(DbIndex index) {
        this.indexes.remove(index);
        index.parent = null;
        return index;
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbTable mergeWith(DbTable other) throws DbModelException {
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
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final DbTable that = (DbTable) obj;
        return Objects.equals(this.getSchema(), that.getSchema() ) && this.getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSchema(), this.getName());
    }

    @Override
    public String toString() {
        return DbNameUtil.of(this.getSchema(), this.getName() );
    }
    // endregion
    // region Serialization ... 
    // --------------------------------------------------------------------------------


    @Override
    public DbTable fromJSON(JSONObject json) {
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
