package twx.core.db2.model;

import java.sql.JDBCType;

import org.json.JSONObject;

import com.thingworx.types.BaseTypes;

public class DbColumn extends DbObject<DbTable> {

    private static final long serialVersionUID = 1L;

    protected BaseTypes twxType = BaseTypes.NOTHING;
    protected JDBCType  sqlType = JDBCType.NULL;
    protected String    sqlTypeName = null;
    protected int       sqlTypeSize  = -1;
    protected int       ordinal = 0;
    protected int       primaryKeySeq = -1;
    protected Boolean   nullable = false;
    protected Boolean   autoIncrement = false;

    protected DbColumn(DbTable table, String name) {
        super(table, name);
    };

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------
    public BaseTypes getTwxType() {
        return twxType;
    }

    public void setTwxType(BaseTypes twxType) {
        this.twxType = twxType;
    }

    public JDBCType getSqlType() {
        return sqlType;
    }

    public void setSqlType(JDBCType sqlType) {
        this.sqlType = sqlType;
    }

    public String getSqlTypeName() {
        return this.sqlTypeName;
    }
    
    public void setSqlTypeName(String sqlTypeName) {
        this.sqlTypeName = sqlTypeName;
    }

    public int getSqlSize() {
        return this.sqlTypeSize;
    }

    public void setSqlSize(int lenght) {
        this.sqlTypeSize = lenght;
    }
    
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public void setPrimaryKeySeq(Integer keySeq) {
        this.primaryKeySeq = keySeq;
    }

    public Integer getPrimaryKeySeq() {
        return this.primaryKeySeq;
    }

    public Boolean isPrimaryKey() {
        return (this.primaryKeySeq > 0);
    }

    public DbTypeCategory getTypeCategory() {
        // @todo !!
        return DbTypeCategory.NUMERIC;
    }

    public Boolean isNumericType() {
        return getTypeCategory() == DbTypeCategory.NUMERIC;
    }

    public Boolean isDatetiemType() {
        return getTypeCategory() == DbTypeCategory.DATETIME;
    }

    public Boolean isTexualType() {
        return getTypeCategory() == DbTypeCategory.TEXTUAL;
    }

    public Boolean isBinaryType() {
        return getTypeCategory() == DbTypeCategory.BINARY;
    }

    public Boolean isSpecialType() {
        return getTypeCategory() == DbTypeCategory.SPECIAL;
    }

    public Boolean isOtherType() {
        return getTypeCategory() == DbTypeCategory.OTHER;
    }
    // endregion
    // region Serialization ...
    // --------------------------------------------------------------------------------

    @Override
    public DbColumn fromJSON(JSONObject json) {
        super.fromJSON(json);
        this.sqlType = json.has(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE) ? JDBCType.valueOf(json.getString(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE)) : JDBCType.NULL;
        this.twxType = json.has(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE) ? BaseTypes.valueOf(json.getString(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE)) : BaseTypes.NOTHING;
        this.sqlTypeName = json.has(DbConstants.MODEL_TAG_COLUMN_SQL_TYPENAME) ? json.getString(DbConstants.MODEL_TAG_COLUMN_SQL_TYPENAME) : "";
        this.sqlTypeSize = json.has(DbConstants.MODEL_TAG_COLUMN_SIZE) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_SIZE) : 0;
        this.ordinal = json.has(DbConstants.MODEL_TAG_COLUMN_ORDINAL) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_ORDINAL) : 0;
        this.nullable = json.has(DbConstants.MODEL_TAG_COLUMN_NULLABLE) ? json.getBoolean(DbConstants.MODEL_TAG_COLUMN_NULLABLE) : true;
        this.autoIncrement = json.has(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT) ? json.getBoolean(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT) : false;
        this.primaryKeySeq = json.has(DbConstants.MODEL_TAG_COLUMN_PRIMARY_KEY) ? json.getInt(DbConstants.MODEL_TAG_COLUMN_PRIMARY_KEY) : -1;
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
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
        return json;
    }
    // endregion
}
