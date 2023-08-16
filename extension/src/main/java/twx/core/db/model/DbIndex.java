package twx.core.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbIndex extends DbObject<DbTable> {

    // Internal Helpers
    // --------------------------------------------------------------------------------
    protected class DbIndexColumn {
        protected String colName = "";
        protected Integer colSeq = 0;

        public DbIndexColumn(Integer ordinal, String name) {
            this.colSeq = ordinal;
            this.colName = name;
        }

        public Integer getColSeq() {
            return colSeq;
        }

        public void setColSeq(Integer ordinal) {
            this.colSeq = ordinal;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String name) {
            this.colName = name;
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put("colSeq", this.colSeq);
            json.put("colName", this.colName);
            return json;
        }
    }
    // endregion

    // region Get/Set Index Properties
    // --------------------------------------------------------------------------------
    private final List<DbIndexColumn> columns = new ArrayList<DbIndexColumn>();
    protected boolean unique;

    public DbIndex(DbTable table, String name) {
        super(table, name);
    };
    // endregion

    // region Get/Set Index Properties
    // --------------------------------------------------------------------------------
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public List<DbIndexColumn> getColumns() {
        return this.columns;
    }

    public List<String> getColumnNames() {
        return this.columns.stream().map(DbIndexColumn::getColName).collect(Collectors.toList());
    }

    public void addColumn(String columnName) {
        Integer seq = this.columns.size() + 1;
        this.columns.add(new DbIndexColumn(seq, columnName));
    }

    public void addColumn(String columnName, Integer ordinal) {
        this.columns.add(new DbIndexColumn(ordinal, columnName));
        this.columns.sort((c1, c2) -> c1.getColSeq().compareTo(c2.getColSeq()));

    }
    // endregion

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        var colArray = new JSONArray();
        json.put("unique", this.unique);
        for (DbIndexColumn col : this.columns) {
            colArray.put(col.toJSON());
        }
        json.put("columns", colArray);
        return json;
    }
}
