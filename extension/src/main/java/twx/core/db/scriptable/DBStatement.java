package twx.core.db.scriptable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.InfoTable;

public class DBStatement extends ScriptableObject {

    private static final long serialVersionUID = 1L;
    
    protected DBConnection  dbCon;
    protected Statement     stmt;

    @Override
    public String getClassName() { return "DBStatement"; }
    
    @Override
    protected void finalize() throws Throwable {
        if( stmt != null)
            stmt.close();
        stmt = null;
    }

    public DBStatement() {
        this.dbCon = null;
        this.stmt = null;
    }

    public DBStatement(DBConnection connection) throws Exception {
        this.dbCon = connection;
        this.stmt = this.getConnection().createStatement();
    }

    protected Connection getConnection() throws Exception {
        return this.dbCon.getConnection();
    }

    protected Statement getStatement() {
        return this.stmt;
    }
    
    @JSFunction
    public void close() throws SQLException {
        if( stmt != null)
            stmt.close();
        stmt = null;
    }
    
    @JSFunction
    public int executeUpdate (String sql) throws Exception {
        if( stmt == null )
            return -1;
        return stmt.executeUpdate(sql);
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
}
