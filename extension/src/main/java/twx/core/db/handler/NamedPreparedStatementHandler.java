package twx.core.db.handler;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.thingworx.datashape.DataShape;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.StringPrimitive;

import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedPreparedStatementHandler implements AutoCloseable {
    // shared by all instances ...
    final static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    final static Logger logger = LoggerFactory.getLogger(NamedPreparedStatementHandler.class);
    final static String REGEX = "(@\\w*)";
    final static Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);
    //
    protected final PreparedStatement prepStmt;
    protected final List<String> fields = new ArrayList<>();
    protected final String parsedSQL;

    // region Construction & Close
    // --------------------------------------------------------------------------------
    public NamedPreparedStatementHandler(Connection conn, String sql) throws SQLException {
        this.parsedSQL = this.parseSQL(sql);
        this.prepStmt = conn.prepareStatement(this.parsedSQL);
    }

    @Override
    public void close() throws Exception {
        this.prepStmt.close();
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
    public void setFrom(ValueCollection collection) throws SQLException {
        for (int idx = 1; idx <= fields.size(); idx++) {
            String key = this.fields.get(idx - 1); // array [0..n], Field [1..n+1]
            var val = collection.getPrimitive(key);
            if (val == null) {
                logger.info("Key: {}, Idx: {}, Not present in ValueCollection setting to NULL!", key, idx);
                prepStmt.setNull(idx, 0);
            } else {
                // set using the primitive type ...
                this.setFrom(idx, val);
            }
        }
    }

    public void setFrom(String fieldName, IPrimitiveType value) throws SQLException {
        int idx = getFieldIdx(fieldName);
        if (idx == 0)
            throw new SQLException("Unknown Fieldname: " + fieldName);
        setFrom(idx, value);
    }

    public void setFrom(int idx, IPrimitiveType value) throws SQLException {
        
        // first check IPrimitive for null or contained null 
        if( value == null || value.getValue() == null ) {
            logger.info("Idx: {}, value is null", idx);
            prepStmt.setNull(idx, 0);
            return;   
        }

        // check if type is a name, than it's a string ... 
        var twxType = value.getBaseType();        
        if( twxType.name().endsWith("Name") ) {
            logger.info("Idx: {}, Type: {} Is a Name ==> String, NVARCHAR!", idx, twxType.name() );
            prepStmt.setObject(idx, value.getValue(), java.sql.Types.NVARCHAR);      
            return;      
        } 
        // finally sort by type ... 
        switch (twxType) {
            case BOOLEAN:
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.BIT);
                break;
            case STRING:
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.NVARCHAR);
                break;
            case NUMBER:
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.NUMERIC);
                break;
            case INTEGER:
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.INTEGER);
                break;
            case LONG:
                prepStmt.setObject(idx, value.getValue(), java.sql.Types.BIGINT);
                break;
            case DATETIME:
                DateTime dt = (DateTime)value.getValue();
                LocalDateTime locDT = LocalDateTime.now();
                var ts = new Timestamp( ((DateTime)value.getValue()).getMillis() );

                logger.info("Idx: {}, Type: {} Is a Timestamp: {} ISO: {}", idx, twxType.name(), value.getStringValue(), ts.toString() );
                prepStmt.setTimestamp(idx, ts, calendar );
                // prepStmt.setObject(idx, locDT, java.sql.Types.TIMESTAMP );
                break;
            case INFOTABLE: 
            case XML:
            case JSON:
            case HYPERLINK:
            case IMAGELINK:
            case PASSWORD:
            case HTML:
            case TEXT:
            case TAGS:
            case GUID:
            case VEC2:
            case VEC3:
            case VEC4:
            case THINGCODE:
            default:
                logger.info("Idx: {}, Type: {} Is not supported", idx, twxType);
                prepStmt.setNull(idx, 0);
        }
    }

    // endregion
    // region Internal Helpers and Debug ...
    // --------------------------------------------------------------------------------
    public boolean hasField(final String name) {
        return fields.indexOf(name) >= 0;
    }

    public int getFieldIdx(final String name) {
        return fields.indexOf(name) + 1;
    }

    protected String parseSQL(String sql) {
        final Matcher m = PATTERN.matcher(sql);
        while (m.find()) {
            String grp = m.group();
            fields.add(grp.substring(1));
        }
        return m.replaceAll("?");
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        //
        json.put("parsedSQL", this.parsedSQL);
        // write Fields ...
        var array = new JSONArray();
        for (var field : this.fields) {
            array.put(field);
        }
        json.put("Fields", array);
        return json;
    }
    // endregion
}
