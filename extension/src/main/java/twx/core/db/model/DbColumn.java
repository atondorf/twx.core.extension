package twx.core.db.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.sql.JDBCType;

import org.json.JSONObject;

import com.thingworx.datashape.DataShape;

import com.thingworx.types.BaseTypes;

public class DbColumn extends DbObject<DbTable> {
    private Integer ordinal;
    private String typeName;
    private JDBCType type;
    private Integer size;
    private Boolean nullable = true;
    private Boolean autoIncrement = false;
    private String defaultValue = null;
    private BaseTypes twxType = BaseTypes.NOTHING;

    protected DbColumn(DbTable table, String name) {
        super(table, name);
    };

    @Override
    public void clear() {
        super.clear();
    }

    public DbTable getTable() {
        return (DbTable) this.getParent();
    }

    public DbSchema getSchema() {
        return this.getTable().getSchema();
    }

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------
    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public void setType(JDBCType type) {
        this.type = type;
    }

    public void setTypeInt(int type) {
        this.type = JDBCType.valueOf(type);
    }

    public JDBCType getType() {
        return this.type;
    }
    
    public int getTypeInt() {
        return this.type.getVendorTypeNumber();
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setNullable(Boolean notNull) {
        this.nullable = notNull;
    }

    public Boolean getNullable() {
        return this.nullable;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setTwxType(BaseTypes twxType) {
        this.twxType = twxType;
    }

    public BaseTypes getTwxType() {
        return twxType;
    }

    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put(DbConstants.MODEL_TAG_ORDINAL, this.ordinal);
        json.put(DbConstants.MODEL_TAG_SQL_TYPE, this.type);
        json.put(DbConstants.MODEL_TAG_TYPE_NAME, this.typeName);
        json.put(DbConstants.MODEL_TAG_TYPE_SIZE, this.size);
        if( this.nullable )
            json.put(DbConstants.MODEL_TAG_NULLABLE, this.nullable);
        if( this.autoIncrement )
            json.put(DbConstants.MODEL_TAG_AUTOINCREMENT, this.autoIncrement);
        if( this.defaultValue != null )
            json.put(DbConstants.MODEL_TAG_DEFAULT_VALUE, this.defaultValue);
        if( this.twxType != BaseTypes.NOTHING )
            json.put(DbConstants.MODEL_TAG_TWX_BASETYPE, this.twxType.friendlyName() );
        return json;
    }
    // endregion
}
