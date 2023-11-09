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
/*
    private final Set<DbEnum> enums = new LinkedHashSet<>();
    private final Set<DbTableGroup> tableGroups = new LinkedHashSet<>();
*/
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
/*
    // region Get/Set Enums
    // --------------------------------------------------------------------------------
    public Set<DbEnum> getEnums() {
        return Collections.unmodifiableSet(this.enums);
    }

    public Boolean hasEnum(String name) {
        return DbObject.hasObject(this.enums, name);
    }

    public DbEnum getEnum(String name) {
        return DbObject.findObject(this.enums, name);
    }

    public DbEnum createEnum(String name) {
        var en = new DbEnum(this, name);
        return addEnum(en);
    }

    public DbEnum getOrCreateEnum(String name) {
        var en = getEnum(name);
        if (en == null)
            en = createEnum(name);
        return en;
    }

    public DbEnum removeSchema(String name) {
        var en = getEnum(name);
        return removeEnum(en);
    }

    public DbEnum addEnum(DbEnum en) {
        en.takeOwnerShip(this);
        this.enums.add(en);
        return en;
    }

    public DbEnum removeEnum(DbEnum dbEnum) {
        this.enums.remove(dbEnum);
        dbEnum.parent = null;
        return dbEnum;
    }

    // endregion
    // region Get/Set TableGroups
    // --------------------------------------------------------------------------------
    public Set<DbTableGroup> getTableGroups() {
        return Collections.unmodifiableSet(this.tableGroups);
    }

    public Boolean hasTableGroup(String name) {
        return DbObject.hasObject(this.tableGroups, name);
    }

    public DbTableGroup getTableGroup(String name) {
        return DbObject.findObject(this.tableGroups, name);
    }

    public DbTableGroup createTableGroup(String name) {
        DbTableGroup tableGroup = new DbTableGroup(this, name);
        return addTableGroup(tableGroup);
    }

    public DbTableGroup getOrCreateTableGroup(String name) {
        var tableGroup = getTableGroup(name);
        if (tableGroup == null)
            tableGroup = createTableGroup(name);
        return tableGroup;
    }

    public DbTableGroup removeTableGroup(String name) {
        var tableGroup = getTableGroup(name);
        return removeTableGroup(tableGroup);
    }

    public DbTableGroup addTableGroup(DbTableGroup tableGroup) {
        tableGroup.takeOwnerShip(this);
        this.tableGroups.add(tableGroup);
        return tableGroup;
    }

    public DbTableGroup removeTableGroup(DbTableGroup tableGroup) {
        this.tableGroups.remove(tableGroup);
        tableGroup.parent = null;
        return tableGroup;
    }
    // endregion
 */        
    // region Model Join & Compare
    // --------------------------------------------------------------------------------
    public DbSchema mergeWith(DbSchema other) throws DbModelException {
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
/*
    public DbTable addTableFromJSON(JSONObject json) {
        if (!json.has(DbConstants.MODEL_TAG_NAME))
            throw new DbModelException("JSON does not define a tag 'name'");
        DbTable table = new DbTable(this, json.getString(DbConstants.MODEL_TAG_NAME));
        table.fromJSON(json);
        return addTable(table);
    }

    @Override
    public DbSchema fromJSON(JSONObject json) {
        super.fromJSON(json);
        if (json.has("tables")) {
            JSONArray tables = json.getJSONArray(DbConstants.MODEL_TAG_TABLE_ARRAY);
            tables.forEach(item -> {
                addTableFromJSON((JSONObject) item);
            });
        }
        return this;
    }
 */

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
