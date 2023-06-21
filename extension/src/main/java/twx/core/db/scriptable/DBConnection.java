package twx.core.db.scriptable;

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

import twx.core.db.imp.DBUtil;

public class DBConnection extends ScriptableObject {
    private static final long serialVersionUID = 1L;

    public DBConnection() {}

    @JSConstructor
    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        DBConnection result = new DBConnection();
        if (args.length < 1 || args[0] == Context.getUndefinedValue())
            throw new IllegalArgumentException("DBConnection - First Param must be Name of an DBThing");

        String thingName = Context.toString(args[0]);
        result.databaseThing = DBUtil.getAbstractDatabaseDirect(thingName);

        if (result.databaseThing == null)
            throw new ThingworxRuntimeException("DBConnection - " + thingName + " is not a database");

        if (args.length >= 2 && args[1] != Context.getUndefinedValue())
            result.autoCommit = Context.toBoolean(args[1]);

        return result;
    }

    @JSConstructor
    public DBConnection(String dbThingName, Boolean autoCommit) throws Exception {
        this.databaseThing = DBUtil.getAbstractDatabaseDirect(dbThingName);
        if (this.databaseThing == null)
            throw new ThingworxRuntimeException("Thing:" + dbThingName + " is not a database");
        connection = databaseThing.getConnection();
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public String getClassName() {
        return "DBConnection";
    }

    @Override
    protected void finalize() throws Throwable {
        if (connection != null)
            connection.close();
        connection = null;
    }

    protected AbstractDatabase getAbstractDatabase() throws Exception {
        return this.databaseThing;
    }

    protected Connection getConnection() throws Exception {
        if (this.connection == null)
            this.connection = this.getAbstractDatabase().getConnection();
        return this.connection;
    }

    protected DatabaseMetaData getMetaData() throws Exception {
        return getConnection().getMetaData();
    }

    @JSFunction
    public Boolean isAutoCommit() {
        return this.autoCommit;
    }

    @JSFunction
    public String getThingName() throws Exception {
        if (databaseThing == null)
            return "UNDEFINED";
        return databaseThing.getName();
    }

    @JSFunction
    public String getCatalog() throws Exception {
        return getConnection().getCatalog();
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

    @JSFunction
    public void commit() throws Exception {
        getAbstractDatabase().commit(connection);
    }

    @JSFunction
    public void rollback() throws Exception {
        getAbstractDatabase().rollback(connection);
    }

    @JSFunction
    public void close() throws Exception {
        if (connection != null)
            connection.close();
        connection = null;
    }

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

    /** Some private data for this class. */
    AbstractDatabase databaseThing = null;
    Connection connection = null;
    Boolean autoCommit = false;
}
