package twx.core.db.model;

import org.json.JSONObject;

public class DbIndex extends DbObject<DbTable> {

    // Internal Enum for IdxType
    // --------------------------------------------------------------------------------
    public enum IdxType {
        Statistic(0, "Statistic"), Clustered(1, "Clustered"), Hashed(2, "Hashed"), Other(3, "Other");

        public Integer  key;
        public String   label;

        private IdxType(Integer key, String label) {
            this.key = key;
            this.label = label;
        }

        public static IdxType getByKey(Integer key) {
            for (IdxType e : values()) {
                if (e.key.equals(key)) {
                    return e;
                }
            }
            return null;
        }

        public static IdxType getByLabel(String label) {
            for (IdxType e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }
    // enregion

    protected IdxType   type;
    protected String    column;
    protected boolean   unique;

    public DbIndex(DbTable table, String name) {
        super(table, name);
    };

    // region Get/Set Index Properties 
    // --------------------------------------------------------------------------------
    public int getType() {
        return type.key;
    }

    public String getColumn() {
        return column;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setType(int type) {
        this.type = IdxType.getByKey(type);
    }

    public void setColumn(String colName) {
        this.column = colName;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    // endregion 

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("type", type.label);
        json.put("column", column);
        json.put("unique", unique);
        return json;
    }
}
