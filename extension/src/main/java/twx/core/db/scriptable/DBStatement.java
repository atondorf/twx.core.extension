package twx.core.db.scriptable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.InfoTable;

import twx.core.db.util.InfoTableUtil;

public class DBStatement extends ScriptableObject {
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection connection = null;
    protected Statement stmt = null;

    protected static Statement getStatement(Scriptable me) {
        DBStatement dbStatement = (DBStatement) me;
        return dbStatement.stmt;
    }

    // endregion
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBStatement() {
    }

    public DBStatement(DBConnection dbCon) throws Exception {
        this.connection = dbCon;
        this.stmt = createStatement(null);
    }

    protected Statement createStatement(String sql) throws Exception {
        return this.connection.getConnection().createStatement();
    }

    @Override
    public String getClassName() {
        return "DBStatement";
    }

    @Override
    protected void finalize() throws Throwable {
        if (stmt != null)
            stmt.close();
        stmt = null;
    }

    // endregion
    // region Statement Handling
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() throws SQLException {
        try { 
            if (stmt != null)
                stmt.close();
            stmt = null;
        }
        catch(Exception ex ) {
            this.connection.logException("Error in close()", ex);
            throw ex;
        }
    }

    @JSFunction
    public Boolean execute(String sql) throws SQLException {
        try {
            Boolean ret = stmt.execute(sql);
            return ret;
        } catch (Exception ex) {
            this.connection.logException("Error in execute()", ex);
            throw ex;
        }
    }

    @JSFunction
    public int executeUpdate(String sql) throws SQLException {
        try {
            int ret = stmt.executeUpdate(sql);
            return ret;
        } catch (Exception ex) {
            this.connection.logException("Error in executeUpdate()", ex);
            throw ex;
        }
    }

    @JSFunction
    public InfoTable executeQuery(String sql) throws SQLException {
        InfoTable result = null;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            result = InfoTableUtil.createInfoTableFromResultset(rs);
            return result;
        } catch (Exception ex) {
            this.connection.logException("Error in executeQuery()", ex);
            throw ex;
        }
    }

    @JSFunction 
    public void addBatch(String sql) throws SQLException {
        this.stmt.addBatch(sql);
    }

    @JSFunction 
    public void clearBatch() throws SQLException {
        this.stmt.clearBatch();
    }

    @JSFunction
    public InfoTable executeBatch()  throws SQLException {
        
        return null;
    }

    

    // endregion
}
