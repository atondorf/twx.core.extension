package twx.core.db;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.StringPrimitive;

import twx.core.db.scriptable.DBConnection;
import twx.core.db.scriptable.DBPreparedStatement;
import twx.core.db.scriptable.DBStatement;
import twx.core.db.scriptable.QueryBuilder;

public class DatabaseScriptLibrary {

    // Scriptable Interface for contrsution
    // --------------------------------------------------------------------------------
    public static void require_core_db(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        // Check if the class is already registered ...
        {
            var obj = ScriptableObject.getProperty(me, "DBConnection");
            if (obj == Scriptable.NOT_FOUND)
                ScriptableObject.defineClass(me, DBConnection.class);
        }
        {
            var obj = ScriptableObject.getProperty(me, "DBStatement");
            if (obj == Scriptable.NOT_FOUND)
                ScriptableObject.defineClass(me, DBStatement.class);
        }
        {
            var obj = ScriptableObject.getProperty(me, "DBPreparedStatement");
            if (obj == Scriptable.NOT_FOUND)
                ScriptableObject.defineClass(me, DBPreparedStatement.class);
        }
        {
            var obj = ScriptableObject.getProperty(me, "QueryBuilder");
            if (obj == Scriptable.NOT_FOUND)
                ScriptableObject.defineClass(me, QueryBuilder.class);
        }
    }

    public static Object core_db_getConnection(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        // first register helpers and wrappers ...
        require_core_db(cx, me, args, funObj);
        // AuthenticationUtilities.validateUserSecurityContext();
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in core_getMultiTimer");
        DSLConverter.convertValues(args, me);
        // create and return ...
        StringPrimitive desc = (StringPrimitive) BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
        Object[] args_new = { desc.getValue() };
        return cx.newObject(me, "DBConnection", args_new);
    }

    public static Object core_db_getQueryBuilder(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        // first register helpers and wrappers ...
        require_core_db(cx, me, args, funObj);
        // AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);
        // create and return ...
        StringPrimitive desc = (StringPrimitive) BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
        Object[] args_new = { desc.getValue() };
        return cx.newObject(me, "QueryBuilder", args_new);
    }

}
