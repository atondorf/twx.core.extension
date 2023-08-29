package twx.core.db2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbForeignKey extends DbObject<DbTable> {

    private static final long serialVersionUID = 1L;

    // Internal Enum for FK-Rules
    // --------------------------------------------------------------------------------
    public enum FkRule {
        Cascade(0, "cascade"), Restrict(1, "restrict"), SetNull(2, "setNull"), NoAction(3, "noAction"), SetDefault(4, "setDefault");

        public Integer key;
        public String label;

        private FkRule(Integer key, String label) {
            this.key = key;
            this.label = label;
        }

        public static FkRule getByKey(Integer key) {
            for (FkRule e : values()) {
                if (e.key.equals(key)) {
                    return e;
                }
            }
            return null;
        }

        public static FkRule getByLabel(String label) {
            for (FkRule e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }

    protected class DbFKReference implements Serializable {
        private static final long serialVersionUID = 1L;

        protected Integer   ordinal = 0;
        protected String    localColumnName = "";
        protected String    foreignColumnName = "";
        
        public DbFKReference(Integer ordinal, String localColumnName, String foreignColumnName) {
            this.ordinal = ordinal;
            this.localColumnName = localColumnName;
            this.foreignColumnName = foreignColumnName;
        }

        public void setOrdinal(Integer ordinal) {
            this.ordinal = ordinal;
        }

        public Integer getOrdinal() {
            return ordinal;
        }

        public void setLocalColumnName(String name) {
            this.localColumnName = name;
        }

        public String getLocalColumnName() {
            return localColumnName;
        }

        public String getForeignColumnName() {
            return foreignColumnName;
        }

        public void setForeignColumnName(String toColumnName) {
            this.foreignColumnName = toColumnName;
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put( DbConstants.MODEL_TAG_INDEX_LOCAL_COLUMN, this.localColumnName );
            json.put( DbConstants.MODEL_TAG_INDEX_ORDINAL, this.ordinal );
            json.put( DbConstants.MODEL_TAG_INDEX_FOREIGN_COLUMN, this.foreignColumnName );
            return json;
        }
    }
    // endregion

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------    
    private final List<DbFKReference> columns = new ArrayList<DbFKReference>();
    protected String foreignSchemaName  = "";
    protected String foreignTableName   = "";
    protected FkRule onUpdate           = null;
    protected FkRule onDelete           = null;

    public DbForeignKey(DbTable table, String name) {
        super(table, name);
    };
    // endregion 

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------
    public void addColumn(Integer seq, String localColumn, String toCol ) {
        this.columns.add( new DbFKReference(seq, localColumn, toCol));
    }

    public void setOnUpdate(int onUpdate) {
        this.onUpdate = FkRule.getByKey(onUpdate);
    }

    public void setOnDelete(int onDelete) {
        this.onDelete = FkRule.getByKey(onDelete);
    }

    public int getOnUpdate() {
        return onUpdate.key;
    }

    public int getOnDelete() {
        return onDelete.key;
    }

    public void setForeignSchemaName(String foreignSchemaName) {
        this.foreignSchemaName = foreignSchemaName;
    }

    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public String getForeignSchemaName() {
        return foreignSchemaName;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }
    // endregion
    // region Serialization ... 
    // --------------------------------------------------------------------------------
    public void addColumnFromJSON(JSONObject json) {
        Integer colSeq = json.getInt(DbConstants.MODEL_TAG_INDEX_ORDINAL);
        String localColumnName = json.getString(DbConstants.MODEL_TAG_INDEX_LOCAL_COLUMN);   
        String foreignColumnName = json.getString(DbConstants.MODEL_TAG_INDEX_FOREIGN_COLUMN);   
        this.addColumn(colSeq,localColumnName,foreignColumnName);  
    }

    @Override
    public DbForeignKey fromJSON(JSONObject json) {
        super.fromJSON(json);
        if( json.has(DbConstants.MODEL_TAG_INDEX_FOREIGN_SCHEMA))
            this.foreignSchemaName = json.getString(DbConstants.MODEL_TAG_INDEX_FOREIGN_SCHEMA);
        if( json.has(DbConstants.MODEL_TAG_INDEX_FOREIGN_TABLE))
            this.foreignTableName = json.getString(DbConstants.MODEL_TAG_INDEX_FOREIGN_TABLE);
        if( json.has(DbConstants.MODEL_TAG_INDEX_ON_UPDATE))
            this.onUpdate = FkRule.getByLabel(json.getString(DbConstants.MODEL_TAG_INDEX_ON_UPDATE));
        if( json.has(DbConstants.MODEL_TAG_INDEX_ON_DELETE))
            this.onDelete = FkRule.getByLabel(json.getString(DbConstants.MODEL_TAG_INDEX_ON_DELETE));
        if( json.has(DbConstants.MODEL_TAG_COLUMN_ARRAY) ) {
            JSONArray schemas = json.getJSONArray(DbConstants.MODEL_TAG_COLUMN_ARRAY);
            schemas.forEach( item -> {
                this.addColumnFromJSON((JSONObject)item);
            });
        }        
        return this;
    }
    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var colArray = new JSONArray();
        json.put(DbConstants.MODEL_TAG_INDEX_FOREIGN_SCHEMA, this.foreignSchemaName);
        json.put(DbConstants.MODEL_TAG_INDEX_FOREIGN_TABLE, this.foreignTableName);
        if( this.onUpdate != null )
            json.put(DbConstants.MODEL_TAG_INDEX_ON_UPDATE, this.onUpdate.label);
        if( this.onDelete != null )
            json.put(DbConstants.MODEL_TAG_INDEX_ON_DELETE, this.onDelete.label);
        for (DbFKReference col : this.columns) {
            colArray.put( col.toJSON() );
        }
        json.put(DbConstants.MODEL_TAG_COLUMN_ARRAY, colArray );
        return json;
    }
    // endregion

}
