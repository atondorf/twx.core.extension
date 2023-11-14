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
    public void close() throws Exception {
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
    public Boolean execute(String sql) throws Exception {
        try {
            Boolean ret = stmt.execute(sql);
            return ret;
        } catch (Exception ex) {
            this.connection.logException("Error in execute()", ex);
            throw ex;
        }
    }

    @JSFunction
    public int executeUpdate(String sql) throws Exception {
        try {
            int ret = stmt.executeUpdate(sql);
            return ret;
        } catch (Exception ex) {
            this.connection.logException("Error in executeUpdate()", ex);
            throw ex;
        }
    }

    @JSFunction
    public InfoTable executeQuery(String sql) throws Exception {
        InfoTable result = null;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            result = SQLToInfoTableConversion.createInfoTableFromResultset(rs, null);
            return result;
        } catch (Exception ex) {
            this.connection.logException("Error in executeQuery()", ex);
            throw ex;
        }
    }

    // endregion
}
