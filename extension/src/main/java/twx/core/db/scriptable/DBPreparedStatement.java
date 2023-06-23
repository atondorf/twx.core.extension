package twx.core.db.scriptable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.InfoTable;

public class DBPreparedStatement extends ScriptableObject {
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBPreparedStatement() { }

    public DBPreparedStatement(DBConnection dbCon, String sql) throws Exception {
        this.dbCon  = dbCon;
        this.pstmt  = this.dbCon.getConnection().prepareStatement(sql);
    }

    @Override
    public String getClassName() {
        return "DBPreparedStatement";
    }

    @Override
    protected void finalize() throws Throwable {
        if (pstmt != null)
            pstmt.close();
        pstmt = null;
    }

    // endregion 
    // region Statement Handling 
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() throws SQLException {
        if( pstmt != null)
            pstmt.close();
        pstmt = null;
    }

    @JSFunction
    public Boolean execute() throws Exception {
        return pstmt.execute();
    }

    @JSFunction
    public int executeUpdate() throws Exception {
        if (pstmt == null)
            return -1;
        return pstmt.executeUpdate();
    }

    @JSFunction
    public InfoTable executeQuery() throws Exception {
        InfoTable result = null;
        if (pstmt != null) {
            ResultSet rs = pstmt.executeQuery();
            result = SQLToInfoTableConversion.createInfoTableFromResultset(rs, null);
        }
        return result;
    }

    @JSFunction
    public void executeBatch() throws Exception {
        
    }

    @JSFunction
    public void addBatch() throws Exception {
        pstmt.addBatch();
    }

    @JSFunction
    public static void set(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (args[1] == null)
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd may not be Null");

        var pstmt = DBPreparedStatement.getStatement(me);
        Integer idx = ((Number) args[0]).intValue();
        Object obj = args[1];
        String className = obj.getClass().getName();
        switch (className) {
            case "java.lang.Integer":
                pstmt.setInt(idx, (Integer) obj);
                break;
            case "java.lang.Long":
                pstmt.setLong(idx, (Long) obj);
                break;
            case "java.lang.Double":
                pstmt.setDouble(idx, (Double) obj);
                break;
            case "java.lang.String":
                pstmt.setString(idx, (String) obj);
                break;
            case "org.mozilla.javascript.NativeDate":
                long time = ((Date) Context.jsToJava(obj, Date.class)).getTime();
                pstmt.setTimestamp(idx, new Timestamp(time));
                break;
            default:
                throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), " + className + " is not supported!");
        }
    }

    // endregion 
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection      dbCon = null;
    protected PreparedStatement pstmt = null;

    protected static PreparedStatement getStatement(Scriptable me) {
        DBPreparedStatement dbStatement = (DBPreparedStatement)me;
        return dbStatement.pstmt;
    }

    // endregion
}
