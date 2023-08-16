package twx.core.db.scriptable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.InfoTable;

public class DBStatement extends ScriptableObject {
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBStatement() { }

    public DBStatement(DBConnection dbCon) throws Exception {
        this.dbCon = dbCon;
        this.stmt  = createStatement(null);
    }

    protected Statement createStatement(String sql) throws Exception {
        return this.dbCon.getConnection().createStatement();
    }

    @Override
    public String getClassName() { return "DBStatement"; }

    @Override
    protected void finalize() throws Throwable {
        if( stmt != null)
            stmt.close();
        stmt = null;
    }
    // endregion 
    // region Statement Handling 
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() throws SQLException {
        if( stmt != null)
            stmt.close();
        stmt = null;
    }
    
    @JSFunction
    public Boolean execute(String sql) throws Exception {
        Boolean ret = stmt.execute(sql);
        return ret;
    }

    @JSFunction
    public int executeUpdate (String sql) throws Exception {
        if( stmt == null )
            return -1;
        int ret = stmt.executeUpdate(sql);
        return ret;
    }

    @JSFunction
    public InfoTable executeQuery(String sql) throws Exception {
        InfoTable result = null;
        if( stmt != null ) {
            ResultSet rs = stmt.executeQuery(sql);
            result = SQLToInfoTableConversion.createInfoTableFromResultset(rs,null);
        }
        return result;
    }

    // endregion 
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection  dbCon   = null ;
    protected Statement     stmt    = null;

    protected static Statement getStatement(Scriptable me) {
        DBStatement dbStatement = (DBStatement)me;
        return dbStatement.stmt;
    }

    // endregion
}
