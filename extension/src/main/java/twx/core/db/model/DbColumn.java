package twx.core.db.model;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.json.JSONObject;

import com.thingworx.types.BaseTypes;

import twx.core.db.model.settings.DbColumnSetting;
import twx.core.db.model.settings.DbTableSetting;
import twx.core.db.model.settings.SettingHolder;

public class DbColumn extends DbObject<DbTable> implements SettingHolder<DbColumnSetting> {
    protected final Map<DbColumnSetting, String> settings = new EnumMap<>(DbColumnSetting.class);
    protected Integer   ordinal;
    protected String    typeName;
    protected Short     type;
    protected Integer   size;

    protected DbColumn(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public void clear() {
        super.clear();
        this.settings.clear();
    }

    public DbTable getTable() {
        return (DbTable)this.getParent();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // region Get/Set Settings ... 
    // --------------------------------------------------------------------------------
    @Override
    public void addSetting(DbColumnSetting settingKey, String value) {
        settings.put(settingKey, value);
    }

    public String getSetting(DbColumnSetting settingKey) {
        return this.settings.get(settingKey);
    }

    public Map<DbColumnSetting, String> getSettings() {
        return Collections.unmodifiableMap(settings);
    }
    // endregion
    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Short getType() {
        return this.type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------

    @Override
    public DbColumn fromJSON(JSONObject json) {
        super.fromJSON(json);
/*         this.sqlType = json.has(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE) ? JDBCType.valueOf(json.getString(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE)) : JDBCType.NULL;
        this.twxType = json.has(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE) ? BaseTypes.valueOf(json.getString(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE)) : BaseTypes.NOTHING;
        this.sqlTypeName = json.has(DbConstants.MODEL_TAG_COLUMN_SQL_TYPENAME) ? json.getString(DbConstants.MODEL_TAG_COLUMN_SQL_TYPENAME) : "";
        this.sqlTypeSize = json.has(DbConstants.MODEL_TAG_COLUMN_SIZE) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_SIZE) : 0;
        this.ordinal = json.has(DbConstants.MODEL_TAG_COLUMN_ORDINAL) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_ORDINAL) : 0;
        this.nullable = json.has(DbConstants.MODEL_TAG_COLUMN_NULLABLE) ? json.getBoolean(DbConstants.MODEL_TAG_COLUMN_NULLABLE) : true;
        this.autoIncrement = json.has(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT) ? json.getBoolean(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT) : false;
        this.primaryKeySeq = json.has(DbConstants.MODEL_TAG_COLUMN_PRIMARY_KEY) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_PRIMARY_KEY) : -1;
 */        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put(DbConstants.MODEL_TAG_ORDINAL, this.ordinal );
        json.put(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE, this.type );
        json.put(DbConstants.MODEL_TAG_TYPE_NAME, this.typeName );
        json.put(DbConstants.MODEL_TAG_TYPE_SIZE, this.size);
        // add Settings ... 
        this.settings.entrySet().stream().forEach( s -> {
            json.put( s.getKey().label, s.getValue() );
        });

        // 
/*         
        if (this.twxType != null)
            json.put(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE, twxType.name());
        if (this.sqlType != null)
            json.put(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE, sqlType.getName());
        if ( this.sqlTypeName != null )
            json.put(DbConstants.MODEL_TAG_COLUMN_SQL_TYPENAME, this.sqlTypeName);
        json.put(DbConstants.MODEL_TAG_COLUMN_SIZE, this.sqlTypeSize);
        json.put(DbConstants.MODEL_TAG_COLUMN_ORDINAL,this.ordinal );
        if (this.nullable != null)
            json.put(DbConstants.MODEL_TAG_COLUMN_NULLABLE, this.nullable);
        if (this.autoIncrement)
            json.put(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT, this.autoIncrement);
        if (this.primaryKeySeq > 0)
            json.put(DbConstants.MODEL_TAG_COLUMN_PRIMARY_KEY, this.primaryKeySeq);
 */        return json;
    }
    // endregion
}
