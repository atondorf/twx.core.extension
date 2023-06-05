package twx.core.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbColumn extends DbObject<DbTable> {

    protected   String  typeName;
    protected   int     typeId;
    protected   int     size = -1;
    protected   Boolean nullable = false;
    protected   Boolean autoIncrement = false;
    protected   int     primaryKeySeq = -1;

    protected DbColumn(DbTable table, String name) {
        super(table, name);
    };

    // region Get/Set Table Properties 
    // --------------------------------------------------------------------------------
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int lenght) {
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

    public Boolean isPrimaryKey() {
        return primaryKeySeq > 0;
    }

    public void setPrimaryKeySeq(int keySeq) {
        this.primaryKeySeq = keySeq;
    }
    // endregion

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("typeName", this.typeName );
        json.put("typeId",this.typeId);
        if( this.size > 0 ) 
            json.put("lenght", this.size);
        json.put("nullable", this.nullable);
        if( this.autoIncrement )
            json.put("autoIncrement", this.autoIncrement);
        if( this.primaryKeySeq > 0 )
            json.put("primaryKeySeq", this.primaryKeySeq);
        return json;
    }
}
