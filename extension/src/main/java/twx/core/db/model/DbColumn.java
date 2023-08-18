package twx.core.db.model;

import org.json.JSONObject;
import java.sql.JDBCType;
import com.thingworx.types.BaseTypes;

public class DbColumn extends DbObject<DbTable> {

    protected   JDBCType    sqlType         = JDBCType.NULL;
    protected   BaseTypes   twxType         = BaseTypes.NOTHING;
    protected   int         size            = -1;
    protected   int         primaryKeySeq   = -1;
    protected   Boolean     nullable        = false;
    protected   Boolean     autoIncrement   = false;

    protected DbColumn(DbTable table, String name) {
        super(table, name);
    };

    // region Get/Set Table Properties 
    // --------------------------------------------------------------------------------
    public JDBCType getSqlType() {
        return sqlType;
    }

    public void setSqlType(JDBCType sqlType) {
        this.sqlType = sqlType;
    }

    public BaseTypes getTwxType() {
        return twxType;
    }

    public void setTwxType(BaseTypes twxType) {
        this.twxType = twxType;
    }
    
    public int getSqlSize() {
        return this.size;
    }

    public void setSqlSize(int lenght) {
        this.size = lenght;
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
    // endregion
    // region Serialization ... 
    // --------------------------------------------------------------------------------

    @Override
    public DbColumn fromJSON(JSONObject json) {
        super.fromJSON(json);
        if( json.has(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE)) 
            this.sqlType = JDBCType.valueOf( json.getString(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE) );
        if( json.has(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE)) 
            this.twxType = BaseTypes.valueOf( json.getString(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE));
        if( json.has(DbConstants.MODEL_TAG_COLUMN_SIZE)) 
            this.size = json.getInt(DbConstants.MODEL_TAG_COLUMN_SIZE);
        if( json.has(DbConstants.MODEL_TAG_COLUMN_NULLABLE)) 
            this.nullable = json.getBoolean(DbConstants.MODEL_TAG_COLUMN_NULLABLE);
        if( json.has(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT)) 
            this.autoIncrement = json.getBoolean(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT);
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put(DbConstants.MODEL_TAG_COLUMN_SQL_TYPE, sqlType.getName() );
        json.put(DbConstants.MODEL_TAG_COLUMN_TWX_TYPE, twxType.name() );
        if( this.size > 0 ) 
            json.put(DbConstants.MODEL_TAG_COLUMN_SIZE, this.size);
        json.put(DbConstants.MODEL_TAG_COLUMN_NULLABLE, this.nullable);
        if( this.autoIncrement )
            json.put(DbConstants.MODEL_TAG_COLUMN_AUTOINCREMENT, this.autoIncrement);
        return json;
    }
    // endregion
}
