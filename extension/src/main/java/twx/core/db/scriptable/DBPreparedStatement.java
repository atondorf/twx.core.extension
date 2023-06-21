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
    private static final long serialVersionUID = 1L;

    protected DBConnection dbCon;
    protected String sql;
    protected PreparedStatement pstmt;

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

    public DBPreparedStatement() {
        this.dbCon = null;
        this.pstmt = null;
    }

    public DBPreparedStatement(DBConnection connection, String sql) throws Exception {
        this.dbCon = connection;
        this.sql = sql;
        this.pstmt = this.getConnection().prepareStatement(this.sql);
    }

    protected Connection getConnection() throws Exception {
        return this.dbCon.getConnection();
    }

    protected PreparedStatement getStatement() {
        return this.pstmt;
    }

    protected static PreparedStatement getStatement(Scriptable me) {
        DBPreparedStatement dbStatement = (DBPreparedStatement) me;
        return dbStatement.getStatement();
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
                long time = ((Date) cx.jsToJava(obj, Date.class)).getTime();
                pstmt.setTimestamp(idx, new Timestamp(time));
                break;
            default:
                throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), " + className + " is not supported!");
        }
    }
}
