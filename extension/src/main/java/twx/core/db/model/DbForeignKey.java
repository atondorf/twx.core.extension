package twx.core.db.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import twx.core.db.model.DbIndex.DbIndexColumn;

public class DbForeignKey extends DbObject<DbTable> {

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

    protected class DbFKColumn {
        protected String columnName = "";
        protected Integer columnOrdinal = 0;
        protected String toColumnName = "";
        
        public DbFKColumn(String columnName, Integer columnOrdinal, String toColumnName) {
            this.columnName = columnName;
            this.columnOrdinal = columnOrdinal;
            this.toColumnName = toColumnName;
        }

        public void setColumnName(String name) {
            this.columnName = name;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnOrdinal(Integer ordinal) {
            this.columnOrdinal = ordinal;
        }

        public Integer getColumnOrdinal() {
            return columnOrdinal;
        }

        public String getToColumnName() {
            return toColumnName;
        }

        public void setToColumnName(String toColumnName) {
            this.toColumnName = toColumnName;
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put( "columnName", this.columnName );
            json.put( "columnOrdinal", this.columnOrdinal );
            json.put( "toColumnName", this.toColumnName );
            return json;
        }
    }
    // endregion

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------    
    private final List<DbFKColumn> columns = new ArrayList<DbFKColumn>();
    protected String toSchema;
    protected String toTable;
    protected FkRule onUpdate;
    protected FkRule onDelete;

    public DbForeignKey(DbTable table, String name) {
        super(table, name);
    };
    // endregion 

    // region Get/Set Table Properties
    // --------------------------------------------------------------------------------
    public void addColumn(Integer seq, String fromCol, String toCol ) {
        this.columns.add( new DbFKColumn(toCol, seq, toCol));
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

    public void setToSchema(String toSchema) {
        this.toSchema = toSchema;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }

    public String getToSchema() {
        return toSchema;
    }

    public String getToTable() {
        return toTable;
    }
    // endregion

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var colArray = new JSONArray();
        json.put("toSchema", toSchema);
        json.put("toTable", toTable);
        json.put("onUpdate", onUpdate.label);
        json.put("onDelete", onDelete.label);
        for (DbFKColumn col : this.columns) {
            colArray.put(col.toJSON());
        }
        json.put("columns", colArray );
        return json;
    }
}
