package twx.core.db.scriptable;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.InfoTable;

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
        if( this.satement != null )
            this.satement.close();
        this.satement  = cstmt;        
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
