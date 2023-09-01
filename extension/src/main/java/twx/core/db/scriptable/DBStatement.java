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
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection  connection   = null ;
    protected Statement     satement    = null;

    protected static Statement getStatement(Scriptable me) {
        DBStatement dbStatement = (DBStatement)me;
        return dbStatement.satement;
    }
    // endregion
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBStatement() { }

    public DBStatement(DBConnection dbCon) throws Exception {
        this.connection = dbCon;
        this.satement  = createStatement(null);
    }

    protected Statement createStatement(String sql) throws Exception {
        return this.connection.getConnection().createStatement();
    }

    @Override
    public String getClassName() { return "DBStatement"; }

    @Override
    protected void finalize() throws Throwable {
        if( satement != null)
            satement.close();
        satement = null;
    }
    // endregion 
    // region Statement Handling 
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() throws SQLException {
        if( satement != null)
            satement.close();
        satement = null;
    }
    
    @JSFunction
    public Boolean execute(String sql) throws Exception {
        Boolean ret = satement.execute(sql);
        return ret;
    }

    @JSFunction
    public int executeUpdate (String sql) throws Exception {
        if( satement == null )
            return -1;
        int ret = satement.executeUpdate(sql);
        return ret;
    }

    @JSFunction
    public InfoTable executeQuery(String sql) throws Exception {
        InfoTable result = null;
        if( satement != null ) {
            ResultSet rs = satement.executeQuery(sql);
            result = SQLToInfoTableConversion.createInfoTableFromResultset(rs,null);
        }
        return result;
    }

    // endregion 
}
