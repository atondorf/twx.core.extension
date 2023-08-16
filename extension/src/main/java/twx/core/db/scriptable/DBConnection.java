package twx.core.db.scriptable;

import java.sql.Types;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.things.database.AbstractDatabase;

import twx.core.db.IDatabaseHandler;
import twx.core.db.imp.DBUtil;

public class DBConnection extends ScriptableObject {
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBConnection() {
        this.databaseThing = null;
        this.connection = null;
    }

    public DBConnection(String thingName, Boolean autoCommit) throws Exception {
        this.databaseThing = DBUtil.getAbstractDatabaseDirect(thingName);
        this.databaseThing.beginTransaction();
        this.connection = this.databaseThing.getConnection();
        this.connection.setAutoCommit(autoCommit);
    }

    @JSConstructor
    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) throws Exception {
        if (args.length < 1 || args[0] == Context.getUndefinedValue())
            throw new IllegalArgumentException("DBConnection - First Param must be Name of an DBThing");

        String thingName = Context.toString(args[0]);
        Boolean autoCommit = false;

        if (args.length >= 2 && args[1] != Context.getUndefinedValue())
            autoCommit = Context.toBoolean(args[1]);

        return new DBConnection(thingName, autoCommit);
    }

    @Override
    public String getClassName() {
        return "DBConnection";
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
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

    @JSFunction
    public JSONObject getSpec() throws Exception {
        JSONObject obj = new JSONObject();
        var meta = getMetaData();
        obj.put("dbProductName", meta.getDatabaseProductName());
        obj.put("dbProductVersion", meta.getDatabaseProductVersion());
        obj.put("dbDriverName", meta.getDriverName());
        obj.put("dbDriverVersion", meta.getDriverVersion());
        return obj;
    }

    @JSFunction
    public JSONArray getSchemas() throws Exception {
        JSONArray arr = new JSONArray();
        ResultSet rs = getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
            arr.put(schemaName);
        }
        return arr;
    }

    @JSFunction
    public JSONArray getTables(String schema) throws Exception {
        JSONArray arr = new JSONArray();
        ResultSet rs = getMetaData().getTables(null, schema, null, null);
        while (rs.next()) {
            arr.put(rs.getString("TABLE_NAME"));
        }
        return arr;
    }

    @JSFunction
    public JSONArray getColumns(String schema, String table) throws Exception {
        JSONArray arr = new JSONArray();
        ResultSet rs = getMetaData().getColumns(null, schema, table, null);
        while (rs.next()) {
            arr.put(rs.getString("COLUMN_NAME"));
        }
        return arr;
    }

    // endregion
    // region Connection & Transaction Handling
    // --------------------------------------------------------------------------------
    @JSFunction
    public Boolean isClosed() throws SQLException {
        if (this.connection == null)
            return true;
        return this.connection.isClosed();
    }

    @JSFunction
    public void close() throws Exception {
        if (this.connection == null)
            return;
        if( this.databaseThing != null )
            this.databaseThing.endTransaction(this.connection);
        else {
            this.connection.close();
        }
        this.databaseThing = null;
        this.connection = null;
    }

    @JSFunction
    public void open(Boolean autoCommit) throws Exception {
        if (this.connection != null)
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

    protected DatabaseMetaData getMetaData() throws Exception {
        return getConnection().getMetaData();
    }


    AbstractDatabase    databaseThing = null;
    IDatabaseHandler    databaseHandler = null;
    Connection          connection = null;

    // endregion
    // region Test Services for Development only ...
    // --------------------------------------------------------------------------------
    @JSFunction
    public static Object argTest(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in argTest");

        JSONObject jsonResult = new JSONObject();
        jsonResult.put("ClassName", args[0].getClass().getName());
        jsonResult.put("ClassSimpleName", args[0].getClass().getSimpleName());
        jsonResult.put("ClassCanonicalName", args[0].getClass().getCanonicalName());

        if (args[0] instanceof NativeObject) {
            NativeObject nativeObject = (NativeObject) args[0];
            Object[] propertyKeys = nativeObject.getIds();
            // sort all arguments to an Array ...
            JSONArray argArray = new JSONArray();
            for (Object key : propertyKeys) {
                if (key instanceof String) {
                    Object propObject = nativeObject.get((String) key, null);
                    JSONObject argJSON = new JSONObject();
                    argJSON.put("Name", (String) key);
                    argJSON.put("StringVal", propObject.toString());
                    argJSON.put("ClassName", propObject.getClass().getName());
                    argArray.put(argJSON);
                    /*
                     * 
                     * sub_json.put((String) property, oRawValue.toString());
                     */
                }
            }
            jsonResult.put("ARGs", argArray);
        }
        return jsonResult;
    }
    // endregion
}
