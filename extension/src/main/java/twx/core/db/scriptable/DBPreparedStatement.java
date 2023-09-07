package twx.core.db.scriptable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.thingworx.dsl.engine.adapters.SandboxNativeJavaArray;
import com.thingworx.dsl.engine.adapters.ThingworxInfoTableAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxValueCollectionAdapter;
import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.data.util.ValueCollectionSerializer;
import com.thingworx.types.primitives.IPrimitiveType;

public class DBPreparedStatement extends ScriptableObject {
    // region Private Members ...
    // --------------------------------------------------------------------------------
    protected DBConnection connection = null;
    protected PreparedStatement pstmt = null;

    protected static PreparedStatement getStatement(Scriptable me) {
        DBPreparedStatement dbStatement = (DBPreparedStatement) me;
        return dbStatement.pstmt;
    }

    // endregion
    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DBPreparedStatement() {
    }

    public DBPreparedStatement(DBConnection dbCon, String sql) throws Exception {
        this.connection = dbCon;
        this.pstmt = this.connection.getConnection().prepareStatement(sql);
    }

    protected Statement createStatement(String sql) throws Exception {
        return this.connection.getConnection().prepareStatement(sql);
    }

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

    // endregion
    // region Statement Handling
    // --------------------------------------------------------------------------------
    @JSFunction
    public void close() {
        try {
            pstmt.close();
            pstmt = null;
        } catch (Exception ex) {
            this.connection.logException("Error in close()", ex);
        }
    }

    @JSFunction
    public Boolean execute() throws Exception {
        try {
            return pstmt.execute();
        } catch (Exception ex) {
            this.connection.logException("Error in executeUpdate()", ex);
            throw ex;
        }
    }

    @JSFunction
    public int executeUpdate() throws Exception {
        try {
            return pstmt.executeUpdate();
        } catch (Exception ex) {
            this.connection.logException("Error in executeUpdate()", ex);
            throw ex;
        }
    }

    @JSFunction
    public InfoTable executeQuery() throws Exception {
        try {
            ResultSet rs = pstmt.executeQuery();
            return SQLToInfoTableConversion.createInfoTableFromResultset(rs, null);

        } catch (Exception ex) {
            this.connection.logException("Error in executeUpdate()", ex);
            throw ex;
        }
    }

    @JSFunction
    public void executeBatch() throws Exception {

    }

    @JSFunction
    public void addBatch() throws Exception {
        pstmt.addBatch();
    }

    @JSFunction
    public static void setNull(Context cx, Scriptable me, Object[] args, Function funObj) throws SQLException {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (!(args[1] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be an SQL-Type number");

        var pstmt = DBPreparedStatement.getStatement(me);
        pstmt.setNull(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
    }

    @JSFunction
    static void setInteger(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (!(args[1] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a number");
        var pstmt = DBPreparedStatement.getStatement(me);
        pstmt.setInt(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
    }

    @JSFunction
    static void setLong(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (!(args[1] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a number");
        var pstmt = DBPreparedStatement.getStatement(me);
        pstmt.setLong(((Number) args[0]).intValue(), ((Number) args[1]).longValue());
    }

    @JSFunction
    static void setDouble(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (!(args[1] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a number");
        var pstmt = DBPreparedStatement.getStatement(me);
        pstmt.setDouble(((Number) args[0]).intValue(), ((Number) args[1]).doubleValue());
    }

    @JSFunction
    static void setString(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (!(args[1] instanceof String))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a number");
        var pstmt = DBPreparedStatement.getStatement(me);
        pstmt.setString(((Number) args[0]).intValue(), (String) args[1]);
    }

    @JSFunction
    static void setDate(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof Number))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1nd must be an index number");
        if (args[1] == null)
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a date");
        if (!(args[1].getClass().getName().equals("org.mozilla.javascript.NativeDate")))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 2nd must be a date");

        var pstmt = DBPreparedStatement.getStatement(me);
        long time = ((Date) Context.jsToJava(args[1], Date.class)).getTime();
        pstmt.setTimestamp(((Number) args[0]).intValue(), new Timestamp(time));
    }

    @JSFunction
    public static void setVal(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        if (args.length < 2)
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
                long time = ((Date) Context.jsToJava(obj, Date.class)).getTime();
                pstmt.setTimestamp(idx, new Timestamp(time));
                break;
            case "com.thingworx.dsl.engine.adapters.SandboxNativeJavaArray":
                SandboxNativeJavaArray array = (SandboxNativeJavaArray) obj;
                // TODO
                pstmt.setBytes(idx, null);
                break;
            case "com.thingworx.dsl.engine.adapters.ThingworxInfoTableAdapter":
                InfoTable table = ((ThingworxInfoTableAdapter) obj).getInfoTable();
                pstmt.setString(idx, table.toJSON().toString());
                break;
            case "com.thingworx.dsl.engine.adapters.ThingworxValueCollectionAdapter":
                ValueCollection valCol = (ValueCollection) (((ThingworxValueCollectionAdapter) obj).unwrap());
                pstmt.setString(idx, valCol.toJSON().toString());
                break;
            case "com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter":
                JSONObject json = ((ThingworxJSONObjectAdapter) obj).getJSONObject();
                pstmt.setString(idx, json.toString());
                break;
            case "org.mozilla.javascript.xmlimpl.XML":
                // org.mozilla.javascript.xmlimpl.XML nativeObj =
                // (org.mozilla.javascript.xmlimpl.XML)obj;
                break;
            default:
                throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), " + className + " is not supported!");
        }
    }

    @JSFunction
    public static void setInfoTableRow(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        Integer rowIdx = 0;
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in DBPreparedStatement.set()");
        if (!(args[0] instanceof ThingworxInfoTableAdapter))
            throw new IllegalArgumentException("Invalid Argument in DBPreparedStatement.set(), 1st Argument must be an Infotable");
        if (args[1] != null) {
            if (args[1] instanceof Number) {
                rowIdx = ((Number) args[1]).intValue();
            } else {
                throw new IllegalArgumentException("Second Argument must be the row Idx of the infotable, if not given, row 0 willbe used");
            }
        }
        InfoTable table = ((ThingworxInfoTableAdapter) args[0]).getInfoTable();
        DataShapeDefinition shape = table.getDataShape();
        
        for (FieldDefinition fieldDefinition : shape.getFields().getOrderedFieldsByOrdinal() ) {
            Integer ordinal = fieldDefinition.getOrdinal();


        }
    }

    protected void setPrimitiveType(Integer idx, Object obj, BaseTypes type) throws SQLException {
        switch (type) {
            case BOOLEAN:
                this.pstmt.setBoolean(idx, (Boolean)obj );
                break;
            case INTEGER:
                break;

            case LONG:

                break;

            case DATETIME:
                break;
            case JSON:
                break;

            case NUMBER:

                break;

            case STRING:
            case HYPERLINK:
            case IMAGELINK:
            case HTML:
            case TEXT:
            case GUID:
            case PROPERTYNAME:
            case SERVICENAME:
            case EVENTNAME:
            case THINGGROUPNAME:
            case THINGNAME:
            case THINGSHAPENAME:
            case THINGTEMPLATENAME:
            case DATASHAPENAME:
            case MASHUPNAME:
            case MENUNAME:
            case BASETYPENAME:
            case USERNAME:
            case GROUPNAME:
            case CATEGORYNAME:
            case STATEDEFINITIONNAME:
            case STYLEDEFINITIONNAME:
            case MODELTAGVOCABULARYNAME:
            case DATATAGVOCABULARYNAME:
            case NETWORKNAME:
            case MEDIAENTITYNAME:
            case APPLICATIONKEYNAME:
            case LOCALIZATIONTABLENAME:
            case ORGANIZATIONNAME:
            case DASHBOARDNAME:
            case PERSISTENCEPROVIDERPACKAGENAME:
            case PERSISTENCEPROVIDERNAME:
            case PROJECTNAME:
                this.pstmt.setString(idx, (String)obj );
                break;
            case IMAGE:
            case BLOB:
            case PASSWORD:
            default:
                throw new IllegalArgumentException("");

        }
    }

    // endregion

}
