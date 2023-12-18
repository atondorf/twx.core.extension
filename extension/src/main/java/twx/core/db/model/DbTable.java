package twx.core.db.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.datashape.DataShape;

import liquibase.structure.core.Data;

public class DbTable extends DbObject<DbSchema> {
	private final Set<DbColumn> columns = new LinkedHashSet<>();
    private final Set<DbIndex> indexes = new LinkedHashSet<>();
    private final Set<DbForeignKey> foreignKeys = new LinkedHashSet<>();
    private String twxDataShapeName = null;
    private DataShape twxDataShape = null;

    public DbTable(String name) {
        super(null, name);
    };

    protected DbTable(DbSchema schema, String name) {
        super(schema, name);
    };
    
    @Override
    public void clear() {
        super.clear();
        this.columns.stream().forEach(c -> c.clear());
        this.columns.clear();
        this.indexes.stream().forEach(c -> c.clear());
        this.indexes.clear();
    }

    // region Get/Set Properties
    // --------------------------------------------------------------------------------
    public DbSchema getSchema() {
        return (DbSchema)this.getParent();
    }

    public String getSchemaName() {
        return this.getSchema().getName();
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

    public String getDataShapeName() {
        return this.twxDataShapeName;
    }

    public void setDataShapeName(String dataShapeName) {
        this.twxDataShapeName = dataShapeName;
    }
    // endregion
    // region Indexes
    // --------------------------------------------------------------------------------
    public Set<DbForeignKey> getForeignKeys() {
        return Collections.unmodifiableSet(this.foreignKeys);
    }

    public Boolean hasForeignKey(String name) {
        return DbObject.hasObject(this.foreignKeys, name);
    }

    public DbForeignKey getForeignKey(String name) {
        return DbObject.findObject(this.foreignKeys, name);
    }

    public DbForeignKey createForeignKey(String name) {
        var foreignKey = new DbForeignKey(this, name);
        return addForeignKey(foreignKey);
    }

    public DbForeignKey getOrCreateForeignKey(String name) {
        var foreignKey = getForeignKey(name);
        if (foreignKey == null)
            foreignKey = createForeignKey(name);
        return foreignKey;
    }

    public DbForeignKey removeForeignKey(String name) {
        var foreignKey = getForeignKey(name);
        return removeForeignKey(foreignKey);
    }

    public DbForeignKey addForeignKey(DbForeignKey foreignKey) {
        foreignKey.takeOwnerShip(this);
        this.foreignKeys.add(foreignKey);
        return foreignKey;
    }

    public DbForeignKey removeForeignKey(DbForeignKey foreignKey) {
        this.foreignKeys.remove(foreignKey);
        foreignKey.parent = null;
        return foreignKey;
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
    public JSONObject toJSON() {
        var json = super.toJSON();

        // add Columns ... 
        var array = new JSONArray();
        for (DbColumn column : this.columns ) {
            array.put(column.toJSON());
        }
        json.put(DbConstants.MODEL_TAG_COLUMN_ARRAY, array);
        // add indexes ... 
        var indexes = new JSONArray();
        for (DbIndex index : this.indexes ) {
            indexes.put(index.toJSON());
        }
        if( indexes.length() > 0 )
            json.put(DbConstants.MODEL_TAG_INDEX_ARRAY, indexes);

        // add ForeignKeys ... 
        var foreignKeys = new JSONArray();
        for (DbForeignKey foreignKey : this.foreignKeys ) {
            foreignKeys.put(foreignKey.toJSON());
        }
        if( foreignKeys.length() > 0 )
            json.put(DbConstants.MODEL_TAG_FKKEYS_ARRAY, foreignKeys);

        if( twxDataShape != null )
            json.put(DbConstants.MODEL_TAG_TWX_DATASHAPE, twxDataShape.getName() );
        return json;
    }
    // endregion 

}
