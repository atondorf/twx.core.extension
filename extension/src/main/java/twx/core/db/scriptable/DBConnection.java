package twx.core.db.scriptable;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;

import com.thingworx.things.database.AbstractDatabase;

import twx.core.db.imp.DBUtil;

public class DBConnection extends ScriptableObject {
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBConnection() {}

    public DBConnection(String thingName) throws Exception {
        this.databaseThing = DBUtil.getAbstractDatabaseDirect(thingName);
        this.databaseThing.beginTransaction();
        this.connection = this.databaseThing.getConnection();
        this.connection.setAutoCommit(false);
    }

    public DBConnection(String thingName, Boolean autoCommit) throws Exception {
        this.databaseThing = DBUtil.getAbstractDatabaseDirect(thingName);
        this.databaseThing.beginTransaction();
        this.connection = this.databaseThing.getConnection();
        this.connection.setAutoCommit(autoCommit);
    }

    @Override
    public String getClassName() {
        return "DBConnection";
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        this.databaseThing = null;
    }
    // endregion 
    // region Thing and DB - Information
    // --------------------------------------------------------------------------------
     @JSFunction
    public String getThingName() throws Exception {
        if (databaseThing == null)
            return "UNDEFINED";
        return databaseThing.getName();
    }

    @JSFunction
    public String getCatalog() throws Exception {
        return this.connection.getCatalog();
    }
    // endregion 
    // region Connection & Transaction Handling 
    // --------------------------------------------------------------------------------
    @JSFunction
    public Boolean isClosed() throws SQLException {
        if( this.connection == null )
            return true;
        return this.connection.isClosed();
    }

    @JSFunction
    public void close() throws Exception {
        if( this.connection == null )
            return;
        this.databaseThing.endTransaction(this.connection);
        this.connection = null;
    }

    @JSFunction
    public void open(Boolean autoCommit) throws Exception {
        if( this.connection != null )
            return;
        this.databaseThing.beginTransaction();
        this.connection = this.databaseThing.getConnection();
        this.connection.setAutoCommit(autoCommit);        
    }

    @JSFunction
    public void commit() throws Exception {
        this.databaseThing.commit(this.connection);
    }

    @JSFunction
    public void rollback() throws Exception {
        this.databaseThing.rollback(this.connection);
    }

    @JSFunction
    public Boolean isAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }

    @JSFunction
    public void setAutoCommit(Boolean autoCommit) throws SQLException {
        this.connection.setAutoCommit(autoCommit);
    };
    // endregion 
    // region Statements 
    // --------------------------------------------------------------------------------
    @JSFunction
    public static Object createStatement(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        DBConnection con = (DBConnection) me;
        Object[] args_new = { con };
        return cx.newObject(me, "DBStatement", args_new);
    }

    @JSFunction
    public static Object prepareStatement(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        DBConnection con = (DBConnection) me;
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in prepareStatement");
        Object[] args_new = { con, Context.toString(args[0]) };
        return cx.newObject(me, "DBPreparedStatement", args_new);
    }
    // endregion 
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected Connection getConnection() {
        return this.connection;
    }    
    
    AbstractDatabase    databaseThing   = null;
    Connection          connection      = null;
    
    // endregion 
    // region Test Services for Development only ...  
    // --------------------------------------------------------------------------------
    @JSFunction
    public static Object argTest(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in argTest");
        JSONObject json = new JSONObject();
        json.put("Greet", "Hello Thingworx!");

        var obj = args[0];
        json.put("ClassName", obj.getClass().getName());
        json.put("ClassSimpleName", obj.getClass().getSimpleName());
        json.put("ClassCanonicalName", obj.getClass().getCanonicalName());

        if (obj instanceof NativeObject) {
            NativeObject nativeObject = (NativeObject) obj;
            Object[] propertyID = nativeObject.getIds();
            JSONObject sub_json = new JSONObject();
            for (Object property : propertyID) {
                if (property instanceof String) {
                    Object oRawValue = nativeObject.get((String) property, null);
                    sub_json.put((String) property, oRawValue.toString());
                }
            }
            json.put("ARG", sub_json);
        }
        return json;
    }
    // endregion 
}
