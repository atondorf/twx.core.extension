package twx.core.db.scriptable;

import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import com.thingworx.dsl.utils.ValueConverter;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.datashape.DataShape;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter;
import com.thingworx.things.database.AbstractDatabase;

import org.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSStaticFunction;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.Connection;
import twx.core.db.imp.DBUtil;

import com.thingworx.things.database.SQLToInfoTableConversion;

import java.sql.SQLException;
import java.sql.Statement;

public class DBCallableStatement extends DBStatement {
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBCallableStatement() { }

    public DBCallableStatement(DBConnection connection,String sql) throws Exception {
        super(connection);
        this.createStatement(sql);
    }
    
    @Override
    protected Statement createStatement(String sql) throws Exception {
        this.cstmt = dbCon.getConnection().prepareCall(sql);
        if( this.stmt != null )
            this.stmt.close();
        this.stmt  = cstmt;        
        return this.cstmt;
    }

    @Override
    public String getClassName() { return "DBCallableStatement"; }
    
    // endregion 
    // region Statement Handling 
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() throws SQLException {
        if( cstmt != null)
            cstmt.close();
        cstmt = null;
    }

    @JSFunction
    public int executeUpdate() throws Exception {
        if (cstmt == null)
            return -1;
        return cstmt.executeUpdate();
    }

    @JSFunction
    public InfoTable executeQuery() throws Exception {
        InfoTable result = null;
        if (cstmt != null) {
            ResultSet rs = cstmt.executeQuery();
            result = SQLToInfoTableConversion.createInfoTableFromResultset(rs, null);
        }
        return result;
    }

    @JSFunction
    public void addBatch() throws Exception {
        cstmt.addBatch();
    }

    @JSFunction
    public static void set(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (args[1] == null)
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd may not be Null");

        var cstmt = DBPreparedStatement.getStatement(me);
        Integer idx = ((Number) args[0]).intValue();
        Object obj = args[1];
        String className = obj.getClass().getName();
        switch (className) {
            case "java.lang.Integer":
                cstmt.setInt(idx, (Integer) obj);
                break;
            case "java.lang.Long":
                cstmt.setLong(idx, (Long) obj);
                break;
            case "java.lang.Double":
                cstmt.setDouble(idx, (Double) obj);
                break;
            case "java.lang.String":
                cstmt.setString(idx, (String) obj);
                break;
            case "org.mozilla.javascript.NativeDate":
                long time = ((Date) Context.jsToJava(obj, Date.class)).getTime();
                cstmt.setTimestamp(idx, new Timestamp(time));
                break;
            default:
                throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), " + className + " is not supported!");
        }
    }

    // endregion 
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection          dbCon   = null;
    protected CallableStatement     cstmt   = null;

    protected static CallableStatement getStatement(Scriptable me) {
        DBCallableStatement dbStatement = (DBCallableStatement)me;
        return dbStatement.cstmt;
    }

    // endregion
}
