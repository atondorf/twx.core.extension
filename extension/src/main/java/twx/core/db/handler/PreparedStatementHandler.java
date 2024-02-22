package twx.core.db.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BlobPrimitive;
import com.thingworx.types.primitives.IPrimitiveType;


public class PreparedStatementHandler implements AutoCloseable {
    // shared by all instances ...
    final static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    final static Logger logger = LoggerFactory.getLogger(PreparedStatementHandler.class);
    final static String REGEX = "(@\\w*)";
    final static Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);
    //
    protected class TypeMapEntry {
        public String fieldName = "";
        public int idx = 0;
        public JDBCType jdbcType = JDBCType.CHAR;
        public TypeMapEntry(String fieldName, int idx, JDBCType jdbcType ) {
            this.fieldName = fieldName;
            this.idx = idx;
            this.jdbcType = jdbcType;
        }
        public JSONObject toJSON() {
            JSONObject json = new JSONObject();
            json.put("name", fieldName );
            json.put("idx", idx);
            json.put("jdbcType", jdbcType);
            return json;            
        }
    }
    
    protected final PreparedStatement prepStmt;
    protected final Map<String,TypeMapEntry> keyMap = new HashMap();
    protected final String parsedSQL;

    // region Construction & Close
    // --------------------------------------------------------------------------------
    public PreparedStatementHandler(Connection conn, String sql, DataShapeDefinition dsDef) throws SQLException {
        this.parsedSQL = this.parseSQL(sql, dsDef);
        this.prepStmt = conn.prepareStatement(this.parsedSQL);
    }

    @Override
    public void close() throws Exception {
        this.prepStmt.close();
    }

    /* 
     *  analyses the sql-string and extracts the named fields
     */
    protected String parseSQL(String sql, DataShapeDefinition dsDef ) throws SQLException {
        final Matcher m = PATTERN.matcher(sql);
        int idx = 1;
        while (m.find()) {
            String fieldName = m.group().substring(1);
            var dsField = dsDef.getFieldDefinition(fieldName);
            if( dsField == null )
                throw new SQLException("DataShape " + dsDef.getName() + " does not contain field " + fieldName + ".");
            var baseType = dsField.getBaseType();
            JDBCType jdbcType = DbInfo.base2JdbcDefault( baseType );
            if( jdbcType == JDBCType.NULL )
                throw new SQLException("DataShape " + dsDef.getName() + " contains " + fieldName + " of type " + dsField.getBaseType() + ". This is not supported, yet.");

            keyMap.putIfAbsent(fieldName, new TypeMapEntry(fieldName,idx, jdbcType) );
            idx++;
        }
        return m.replaceAll("?");
    }

    // endregion
    // region Prepared Statement execution ...
    // --------------------------------------------------------------------------------
    public PreparedStatement getPreparedStatement() {
        return this.prepStmt;
    }

    public ResultSet executeQuery() throws SQLException {
        return this.prepStmt.executeQuery();
    }

    public int executUpdate() throws SQLException {
        return this.prepStmt.executeUpdate();
    }

    public void addBatch() throws SQLException {
        this.prepStmt.addBatch();
    }

    public void clearBatch() throws SQLException {
        this.prepStmt.clearBatch();
    }

    public int[] executeBatch() throws SQLException {
        return this.prepStmt.executeBatch();
    }

    // endregion
    // region Named Parameters mapping ...
    // --------------------------------------------------------------------------------
    public void set(ValueCollection collection) throws SQLException {
        for( var entry : keyMap.values() ) {
            String fieldName = entry.fieldName;
            int idx = entry.idx;
            JDBCType jdbcType = entry.jdbcType;
            var value = collection.getPrimitive(fieldName);
            this.set(idx, value, jdbcType);
        }
    }

    public void set(String fieldName, IPrimitiveType value) throws SQLException {
        if( !keyMap.containsKey( fieldName )) 
            throw new SQLException("Unknown Fieldname: " + fieldName);
        var entry = keyMap.get(fieldName);
        int idx = entry.idx;
        JDBCType jdbcType = entry.jdbcType;

        this.set(idx, value, jdbcType);
    }

    protected void setNull(int idx, JDBCType jdbcType) throws SQLException {
        prepStmt.setNull(idx, jdbcType.getVendorTypeNumber() ); 
    }

    protected void set(int idx, IPrimitiveType value, JDBCType jdbcType) throws SQLException {
        // first check IPrimitive for null or contained null 
        if( value == null || value.getValue() == null ) {
            this.setNull(idx,jdbcType); 
            return;
        }
        var twxType = value.getBaseType();        
        // finally sort by type ... 
        switch (twxType) {
            case DATETIME:
                var ts = new Timestamp( ((DateTime)value.getValue()).getMillis() );
                prepStmt.setTimestamp(idx, ts, calendar );
                break;
            case BLOB:
                BlobPrimitive blob = (BlobPrimitive)value;
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.VARBINARY);
                break;
            default:
                prepStmt.setObject(idx, value.getValue(), jdbcType);
                break;
        }
    }

    // endregion
    // region Internal Helpers and Debug ...
    // --------------------------------------------------------------------------------
    public boolean hasField(final String fieldName) {
        return this.keyMap.containsKey(fieldName);
    }

    public int getFieldIdx(final String fieldName) {
        int idx = -1;
        if( this.keyMap.containsKey(fieldName) )
            idx = this.keyMap.get(fieldName).idx;
        return idx;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        //
        json.put("parsedSQL", this.parsedSQL);
        // write Fields ...
        var array = new JSONArray();
        for (var elem : this.keyMap.values() ) {
            array.put(elem.toJSON());
        }
        json.put("Fields", array);
        return json;
    }
    // endregion
}
