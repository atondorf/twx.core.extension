package twx.core.db.model;

import org.json.JSONObject;

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
    // endregion

    protected String column;
    protected String toSchema;
    protected String toTable;
    protected String toColumn;
    protected FkRule onUpdate;
    protected FkRule onDelete;

    public DbForeignKey(DbTable table, String name) {
        super(table, name);
    };

    // region Get/Set Table Properties 
    // --------------------------------------------------------------------------------
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

    public void setColumn(String fromColumn) {
        this.column = fromColumn;
    }

    public void setToSchema(String toSchema) {
        this.toSchema = toSchema;
    }

    public void setToTable(String toTable) {
        this.toTable = toTable;
    }

    public void setToColumn(String toColumn) {
        this.toColumn = toColumn;
    }

    public String getColumn() {
        return column;
    }

    public String getToSchema() {
        return toSchema;
    }

    public String getToTable() {
        return toTable;
    }

    public String getToColumn() {
        return toColumn;
    }
    // endregion

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("column", column);
        json.put("toSchema", toSchema);
        json.put("toTable", toTable);
        json.put("toColumn", toColumn);
        json.put("onUpdate", onUpdate.label);
        json.put("onDelete", onDelete.label);

        return json;
    }
}
