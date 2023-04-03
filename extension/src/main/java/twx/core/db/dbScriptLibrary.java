package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import com.thingworx.common.utils.DateUtilities;
import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.StringPrimitive;

import org.mozilla.javascript.*;
import org.json.JSONArray;
import org.json.JSONObject;

import twx.core.db.scriptable.SqlDbSpec;

public class dbScriptLibrary {

  public static void require_core_db(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    // AuthenticationUtilities.validateUserSecurityContext();
    ScriptableObject.defineClass(me, SqlDbSpec.class);
  }

  public static Object core_getDbSpec(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    // AuthenticationUtilities.validateUserSecurityContext();
    if (args.length != 1)
      throw new Exception("Invalid Number of Arguments in core_getDbSpec");
    DSLConverter.convertValues(args, me);
    // Check if the class is already registered ...
    var obj = ScriptableObject.getProperty(me, "SqlDbSpec");
    if (obj == Scriptable.NOT_FOUND)
      ScriptableObject.defineClass(me, SqlDbSpec.class);
    // create and return ...
    return cx.newObject(me, "SqlDbSpec");
  }

}
