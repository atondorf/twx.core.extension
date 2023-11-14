package twx.core.db.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbSchema extends DbObject<DbModel> {
    public static final String DEFAULT_SCHEMA_NAME = "";
    private final Set<DbTable> tables = new LinkedHashSet<>();

    public DbSchema(String name) {
        super(null, name);
        if (isDefaultName())
            this.setName(DEFAULT_SCHEMA_NAME);
    }

    protected DbSchema(DbModel spec, String name) {
        super(spec, name);
        if (isDefaultName())
            this.setName(DEFAULT_SCHEMA_NAME);
    };

    @Override
    public void clear() {
        super.clear();
        this.tables.stream().forEach(c -> c.clear());
        tables.clear();
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public Boolean isDefault() {
        return this.isDefaultName() || this.getName().equals(DEFAULT_SCHEMA_NAME);
    }

    public DbModel getModel() {
        return (DbModel)this.getParent();
    }

    // endregion
    // region Get/Set Tables
    // --------------------------------------------------------------------------------
    public Set<DbTable> getTables() {
        return Collections.unmodifiableSet(this.tables);
    }

    public Boolean hasTable(String name) {
        return DbObject.hasObject(this.tables, name);
    }

    public DbTable getTable(String name) {
        return DbObject.findObject(this.tables, name);
    }

    public DbTable createTable(String name) {
        DbTable table = new DbTable(this, name);
        return addTable(table);
    }

    public DbTable getOrCreateTable(String name) {
        var table = getTable(name);
        if (table == null)
            table = createTable(name);
        return table;
    }

    public DbTable removeTable(String name) {
        var table = getTable(name);
        return removeTable(table);
    }

    public DbTable addTable(DbTable table) {
        table.takeOwnerShip(this);
        this.tables.add(table);
        return table;
    }

    public DbTable removeTable(DbTable table) {
        this.tables.remove(table);
        table.parent = null;
        return table;
    }
    // endregion
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbSchema mergeWith(DbTable other) throws DbModelException {
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

    // Nothing to do here ... use DbObject implementation by getName only ...

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        // write Tables ... 
        var array = new JSONArray();
        for (DbTable table : this.tables) {
            array.put(table.toJSON());
        }
        json.put(DbConstants.MODEL_TAG_TABLE_ARRAY, array);
        return json;
    }
    // endregion
}
