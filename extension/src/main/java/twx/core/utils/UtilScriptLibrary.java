package twx.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Evaluator;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.logging.LogUtilities;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.things.Thing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.INamedObject;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.webservices.context.ThreadLocalContext;

import ch.qos.logback.classic.Logger;
import twx.core.concurrency.imp.MutexManager;
import twx.core.utils.scriptable.MultiTimer;

public class UtilScriptLibrary {
  protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(UtilScriptLibrary.class);

  // Scriptable Interface for contrsution
  // --------------------------------------------------------------------------------
  public static void require_core_util(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    // Check if the class is already registered ...
    var obj = ScriptableObject.getProperty(me, "MultiTimer");
    if (obj == Scriptable.NOT_FOUND)
      ScriptableObject.defineClass(me, MultiTimer.class);
  }

  public static Object core_getMultiTimer(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    require_core_util(cx, me, args, funObj);
    // AuthenticationUtilities.validateUserSecurityContext();
    if (args.length != 1)
      throw new Exception("Invalid Number of Arguments in core_getMultiTimer");
    DSLConverter.convertValues(args, me);
    // create and return ...
    StringPrimitive desc = (StringPrimitive) BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
    Object[] args_new = { desc.getValue() };
    return cx.newObject(me, "MultiTimer", args_new);
  }

  // Internal Helpers and Tools
  // --------------------------------------------------------------------------------
  public static JSONObject core_getSrcInfo(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    JSONObject json = new JSONObject();
    String name = "me";
    Object meObj = me.get(name, me);
    if (meObj instanceof ThingworxEntityAdapter) {
      ThingworxEntityAdapter adapter = (ThingworxEntityAdapter) meObj;
      Object obj = adapter.get("name", me);
      json.put("name", obj);
    } else {
      json.put("name", "Unknown");
    }
    /**
     * A bit of a hack, but the only way to get filename and line number from an
     * enclosing frame.
     * Src taken form Rhino Engine ...
     */
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    for (StackTraceElement st : stackTrace) {
      String file = st.getFileName();
      if (!(file == null || file.endsWith(".java"))) {
        int line = st.getLineNumber();
        if (line >= 0) {
          json.put("lineNumber", line);
          json.put("fileName", file);
          return json;
        }
      }
    }
    json.put("lineNumber", "-1");
    json.put("fileName", "Unknown");
    return json;
  }

  public static JSONObject core_getStackTrace(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    JSONObject json = new JSONObject();
    JSONArray stack = new JSONArray();
    json.put("stack", stack);

    // Stack ...
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    for (StackTraceElement st : stackTrace) {
      String file = st.getFileName();
      if (!(file == null || file.endsWith(".java"))) {
        int line = st.getLineNumber();
        if (line >= 0) {
          JSONObject elem = new JSONObject();
          elem.put("fileName", st.getFileName());
          elem.put("lineNumber", st.getLineNumber());
          elem.put("methodName", st.getMethodName());
          elem.put("moduleName", st.getModuleName());
          elem.put("moduleVersion", st.getModuleVersion());
          elem.put("classLoaderName", st.getClassLoaderName());
          elem.put("className", st.getClassName());
          stack.put(elem);
        }
      }
    }
    return json;
  }

  public static Object core_createException(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_format");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_format argument must be a format string");

    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = MessageFormat.format((String) args[0], array);

    JSONObject json = UtilScriptLibrary.core_getSrcInfo(cx, me, args, funObj);
    json.put("message", message);
    return json;
  }

  public static void core_throwException(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_strFormat");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_strFormat argument must be a format string");

    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = MessageFormat.format((String) args[0], array);

    throw new RuntimeException(message);
  }

  // System
  // --------------------------------------------------------------------------------
  protected static Object[] callPowerShell(String script) throws Exception {
    List<String> list = new LinkedList<>();

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("powershell.exe", "/c", script);

    Process process = processBuilder.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line = null;
    while ((line = reader.readLine()) != null) {
      list.add(line);
    }
    return list.toArray();
  }

  public static Integer core_getTimeZoneOffsetFromSystem(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    String script = "$now = date; (($now)-($now).touniversaltime()).TotalMinutes";
    Object[] res = callPowerShell(script);
    if (res.length > 0) {
      return Integer.parseInt((String) res[0]);
    }
    return 0;
  }

  public static Object core_callFunc(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    if (args.length < 1)
      throw new Exception("Invalid number of arguments in core_call");
    if (!(args[0] instanceof Function))
      throw new Exception("The first core_callFunc argument must be a function");
    
    var passArgs = new Object[0];
    if( args.length > 1 ) 
      passArgs = Arrays.copyOfRange(args,1,args.length);
    return ((Function)args[args.length - 1]).call(cx, func.getParentScope(), me, passArgs);
  }

  public static JSONObject core_argTest(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();

    var origObj = args[0];
    var convObj = DSLConverter.convert(args[0], me);

    json.put("orgClassName", origObj.getClass().getName());
    json.put("orgClassSimpleName", origObj.getClass().getSimpleName());
    json.put("orgClassCanonicalName", origObj.getClass().getCanonicalName());

    try {
      json.put("convClassName", convObj.getClass().getName());
      json.put("convClassSimpleName", convObj.getClass().getSimpleName());
      json.put("convClassCanonicalName", convObj.getClass().getCanonicalName());
    }
    catch( Exception ex ) {
      json.put("convClassName", "conversion Failed!");
    }
    
    // is ThingworxEntityAdapter ... 
    if (origObj instanceof ThingworxEntityAdapter) {
      ThingworxEntityAdapter adapter = (ThingworxEntityAdapter)origObj;
      INamedObject unwrapped = (INamedObject) adapter.unwrap();
      json.put("class", unwrapped.getClass());
      json.put("name", unwrapped.getName());
      json.put("desc", unwrapped.getDescription());
    }
    // is function 
    if( origObj instanceof Function ) {
        json.put("is_function", true );
    }
    if( origObj instanceof NativeFunction ) {
      NativeFunction funct = (NativeFunction)origObj;
      json.put("is_nativeFunction", true );
      json.put("is_nativeFunction_name", funct.getFunctionName() );
    }
    // is NativeObject
    if (origObj instanceof NativeObject) {
      NativeObject nativeObject = (NativeObject) origObj;
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

  public static Boolean core_hasProperty(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    if (args.length != 1)
      throw new IllegalArgumentException("Invalid number of arguments in string_format");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first core_hasProperty argument must be a name");
    var name = (String) args[0];
    var obj = ScriptableObject.getProperty(me, name);
    if (obj != Scriptable.NOT_FOUND)
      return true;
    return false;
  }

  public static JSONObject core_Test(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    JSONObject meJson = new JSONObject();
    JSONArray meArray = new JSONArray();
    JSONObject thingJson = new JSONObject();
    JSONObject ctxJson = new JSONObject();

    // ------------------------------------------------------------

    // ------------------------------------------------------------
    // analyse context ...

    // adding subJSONs
    json.put("thing", thingJson);
    json.put("me", meJson);
    json.put("ctx", ctxJson);

    return json;
  }

  public static JSONObject core_getScriptableInfo(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    JSONArray array = new JSONArray();

    json.put("ClassName", me.getClass().getName());
    var ids = ScriptableObject.getPropertyIds(me);
    for (Object i : ids) {
      array.put(i);
    }
    json.put("PropertyIds", array);

    return json;
  }

  public static JSONObject core_getThingInfo(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();

    Object meObj = ScriptableObject.getProperty(me, "me");
    if (meObj instanceof ThingworxEntityAdapter) {
      ThingworxEntityAdapter adapter = (ThingworxEntityAdapter) meObj;
      json.put("thing_name", adapter.get("name", me));
      json.put("thing_description", adapter.get("description", me));

      Object obj = adapter.unwrap();
      if (obj != null)
        json.put("thing_unwrap_className", obj.getClass().getName());
      else
        json.put("thing_unwrap_className", "NULL");

    } else {
      json.put("thing_name", "Unknown");
      json.put("thing_description", "");
    }

    return json;
  }

  public static JSONObject core_getContextInfo(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();

    final Object obj = ThreadLocalContext.getMeContext();
    if (obj != null) {
      json.put("local_className", obj.getClass().getName());
      if (obj instanceof Thing) {
        Thing thing = (Thing) obj;
        json.put("local_thingName", thing.getName());
      }
    } else {
      json.put("local_thingName", "NULL");
    }

    return json;
  }

}
