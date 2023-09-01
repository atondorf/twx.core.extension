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
        protected Integer ordinal = 0;
        protected String columnName = "";

        public DbIndexColumn(Integer ordinal, String name) {
            this.ordinal = ordinal;
            this.columnName = name;
        }

        public Integer getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(Integer ordinal) {
            this.ordinal = ordinal;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String name) {
            this.columnName = name;
        }

        public JSONObject toJSON() {
            var json = new JSONObject();
            json.put(DbConstants.MODEL_TAG_INDEX_ORDINAL, this.ordinal);
            json.put(DbConstants.MODEL_TAG_INDEX_LOCAL_COLUMN, this.columnName);
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
        return this.columns.stream().map(DbIndexColumn::getColumnName).collect(Collectors.toList());
    }

    public void addColumn(String columnName) {
        Integer seq = this.columns.size() + 1;
        this.columns.add(new DbIndexColumn(seq, columnName));
    }

    public void addColumn(String columnName, Integer ordinal) {
        this.columns.add(new DbIndexColumn(ordinal, columnName));
        this.columns.sort((c1, c2) -> c1.getOrdinal().compareTo(c2.getOrdinal()));

    }
    // endregion
    // region Serialization ... 
    // --------------------------------------------------------------------------------
    public void addColumnFromJSON(JSONObject json) {
        Integer colSeq = json.getInt(DbConstants.MODEL_TAG_INDEX_ORDINAL);
        String name = json.getString(DbConstants.MODEL_TAG_INDEX_LOCAL_COLUMN);   
        this.addColumn(name,colSeq);   
    }

    @Override
    public DbIndex fromJSON(JSONObject json) {
        super.fromJSON(json);
        if( json.has(DbConstants.MODEL_TAG_INDEX_UNIQUE)) 
            this.unique = json.getBoolean(DbConstants.MODEL_TAG_INDEX_UNIQUE);
        if( json.has(DbConstants.MODEL_TAG_INDEX_ARRAY)) {
            JSONArray columns = json.getJSONArray(DbConstants.MODEL_TAG_INDEX_ARRAY);
            columns.forEach( item -> {
                this.addColumnFromJSON((JSONObject)item);
            });
        }
        return this;
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put(DbConstants.MODEL_TAG_INDEX_UNIQUE, this.unique);
        var colArray = new JSONArray();
        for (DbIndexColumn col : this.columns) {
            colArray.put(col.toJSON());
        }
        if( colArray.length() > 0 )
            json.put(DbConstants.MODEL_TAG_INDEX_ARRAY, colArray);
        return json;
    }
    // endreggion 
}
