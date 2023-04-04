package twx.core.db;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import com.thingworx.dsl.utils.ValueConverter;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.datashape.DataShape;

import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.mozilla.javascript.*;

import org.json.JSONObject;
import org.json.JSONArray;

public class SQLBuilder extends ScriptableObject {

    // Scriptable Interface for contrsution 
    //--------------------------------------------------------------------------------
    public static void require_core_db(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        // Check if the class is already registered ...
        var obj = ScriptableObject.getProperty(me, "SQLBuilder");
        if (obj == Scriptable.NOT_FOUND)
            ScriptableObject.defineClass(me, SQLBuilder.class);
    }

    public static Object core_getSQLBuilder(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        // AuthenticationUtilities.validateUserSecurityContext();
        SQLBuilder.require_core_db(cx,me,args,funObj);
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in core_getDbSpec");
        DSLConverter.convertValues(args, me);
        // create and return ...
        return cx.newObject(me, "SQLBuilder");
    }

    // 
    //--------------------------------------------------------------------------------

    private StringBuilder  builder = new StringBuilder();

    @Override
    public String getClassName() {
        return "SQLBuilder";
    }

    @JSFunction
    public String getObjClassName(Object obj) {
        if( obj instanceof ThingworxEntityAdapter ) {
            return "Wrapped: " + ((ThingworxEntityAdapter)obj).unwrap().getClass().getName(); 
        }
        return obj.getClass().getName();
    }

    @JSFunction
    public String getObjToString(Object obj) {
        return obj.toString();
    }

    @JSFunction
    public SQLBuilder append(Object obj) {
        builder.append(obj.toString());
        return this;
    }

    @JSFunction
    public String create() {
        return builder.toString();
    }

    @JSFunction
    public String testDataShape(Object obj) throws Exception {
        if( !(obj instanceof ThingworxEntityAdapter) ) {
            throw new Exception("obj is not a DataShape ... ");
        }
        return ((ThingworxEntityAdapter)obj).unwrap().getClass().getName();
    }

    @JSFunction
    public String testJSON(Object obj) throws Exception {
        if( !(obj instanceof ThingworxJSONObjectAdapter) ) {
            throw new Exception("obj is not a JSON ... ");
        }
        return ((ThingworxJSONObjectAdapter)obj).getJSONObject().toString();
    }

    @JSFunction
    public String testJSON(Object obj) throws Exception {
        if( !(obj instanceof ThingworxJSONObjectAdapter) ) {
            throw new Exception("obj is not a JSON ... ");
        }
        return ((ThingworxJSONObjectAdapter)obj).getJSONObject().toString();
    }

    

}
