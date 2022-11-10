package twx.core.utils;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import java.util.Arrays;
import java.text.MessageFormat;
import org.mozilla.javascript.*;
import org.json.JSONArray;
import org.json.JSONObject;
import twx.core.utils.Counter;

public class UtilScriptLibrary {
  protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(UtilScriptLibrary.class);

  public static JSONObject src_getInfo(Context cx, Scriptable me, Object[] args, Function func) throws Exception {

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

  public static JSONObject exc_message(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    if (args.length != 1)
      throw new IllegalArgumentException("Invalid Number of Arguments in exc_message");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_message argument must be a string with queue name");
    var message = (String) args[0];

    JSONObject json = UtilScriptLibrary.src_getInfo(cx, me, args, funObj);
    json.put("message", message);

    return json;
  }

  public static JSONObject exc_format(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_format");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_format argument must be a format string");
    
    var format_str = (String) args[0];
    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = String.format((String) args[0], array);

    JSONObject json = UtilScriptLibrary.src_getInfo(cx, me, args, funObj);
    json.put("message", message);

    return json;
  }

  public static JSONObject exc_strFormat(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_strFormat");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_strFormat argument must be a format string");
    
    var format_str = (String) args[0];
    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = MessageFormat.format((String)args[0], array); 

    JSONObject json = UtilScriptLibrary.src_getInfo(cx, me, args, funObj);
    json.put("message", message);

    return json;
  }

  public static void exc_throw(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    if (args.length != 1)
      throw new IllegalArgumentException("Invalid Number of Arguments in exc_throw");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_throw argument must be a string with queue name");
    var message = (String) args[0];
    throw new RuntimeException(message);
  }

  public static void exc_throwFormat(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_strFormat");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_strFormat argument must be a format string");
    
    var format_str = (String) args[0];
    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = MessageFormat.format((String)args[0], array); 

    throw new RuntimeException(message);
  }

  public static JSONObject formatScriptable(Scriptable scr) {
    JSONObject json = new JSONObject();
    Object[] ids = scr.getIds();
    for (Object id : ids) {
      if (id instanceof String) {
        String name = (String) id;
        Object value = scr.get(name, scr);
        if (value instanceof NativeJavaObject) {
          value = ((NativeJavaObject) value).unwrap();
        } else if (value instanceof Undefined) {
          value = null;
        }
        if (name == "me") {
          value = scr.get(name, scr).getClass().getName();
        }
        json.put(name, value);
      }
    }
    return json;
  }

  public static JSONObject script_info(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = formatScriptable(me);
    return json;
  }

  public static JSONObject me_info(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    json.put("function", "me_info");
    String name = "me";
    Object meObj = me.get(name, me);
    if (meObj == null) {
      json.put("me", "NULL");
    } else {
      json.put("toString", meObj.toString());
      json.put("getName", meObj.getClass().getName());
      json.put("getSimpleName", meObj.getClass().getSimpleName());
      json.put("getCanonicalName", meObj.getClass().getCanonicalName());

      if (meObj instanceof ThingworxEntityAdapter) {
        ThingworxEntityAdapter adapter = (ThingworxEntityAdapter) meObj;
        Object obj = adapter.get("name", me);
        json.put("name", obj);
      }

      // json.put("Properties", formatScriptable((Scriptable)meObj) );

      /*
       * if( meObj instanceof Scriptable ) { json.put("isScriptable", true );
       * json.put("me", formatScriptable((Scriptable)meObj)); } else {
       * json.put("isScriptable", false); }
       */
    }
    return json;
  }

  public static JSONObject me_thing_info(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    Object meObj = me.get("me", me);

    json.put("toString", meObj.toString());
    json.put("getName", meObj.getClass().getName());
    json.put("getSimpleName", meObj.getClass().getSimpleName());
    json.put("getCanonicalName", meObj.getClass().getCanonicalName());

    if (meObj instanceof ThingworxEntityAdapter) {
      json.put("isThingworxEntityAdapter", true);
      json.put("me", formatScriptable((Scriptable) meObj));
    } else {
      json.put("isThingworxEntityAdapter", false);
    }
    return json;
  }

  public static JSONObject me_arg_test(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
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

  public static Scriptable me_native_counter(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    ScriptableObject.defineClass(me, Counter.class);

    Object[] arg = { Integer.valueOf(7) };
    Scriptable myCounter = cx.newObject(me, "Counter", arg);
    return myCounter;
  }

  public static JSONObject me_json_test_1(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    json.put("Greet", "Hello Thingworx!");

    return json;
  }

  public static JSONObject me_json_test_2(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    json.put("Greet", "Hello Thingworx!");

    var obj = args[0];
    json.put("ClassName", obj.getClass().getName());
    json.put("ClassSimpleName", obj.getClass().getSimpleName());
    json.put("ClassCanonicalName", obj.getClass().getCanonicalName());

    return json;
  }

}
