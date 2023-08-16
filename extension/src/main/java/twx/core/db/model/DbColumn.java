package twx.core.db.model;

import org.json.JSONObject;
import java.sql.JDBCType;
import com.thingworx.types.BaseTypes;

public class DbColumn extends DbObject<DbTable> {

    protected   JDBCType    sqlType = JDBCType.NULL;
    protected   BaseTypes   twxType = BaseTypes.NOTHING;
    protected   int         size = -1;
    protected   Boolean     nullable = false;
    protected   Boolean     autoIncrement = false;

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

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("sqlType", sqlType.getName() );
        json.put("twxType", twxType.name() );
        if( this.size > 0 ) 
            json.put("lenght", this.size);
        json.put("nullable", this.nullable);
        if( this.autoIncrement )
            json.put("autoIncrement", this.autoIncrement);
        return json;
    }
}
