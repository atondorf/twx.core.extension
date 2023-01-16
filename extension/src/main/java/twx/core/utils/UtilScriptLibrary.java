package twx.core.utils;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import com.thingworx.common.utils.DateUtilities;
import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.security.authentication.AuthenticationUtilities;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class UtilScriptLibrary {
  protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(UtilScriptLibrary.class);
  
  //// Require  ////

  public static void require_core_util(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();
    ScriptableObject.defineClass(me, MultiTimer.class);
  }
  
  //// Internal Helpers and Tools  ////

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

  public static Object core_createException(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
    AuthenticationUtilities.validateUserSecurityContext();        
    if (args.length < 1)
      throw new IllegalArgumentException("Invalid number of arguments in exc_format");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first exc_format argument must be a format string");
      
    var format_str = (String) args[0];
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

    var format_str = (String) args[0];
    var array = Arrays.copyOfRange(args, 1, args.length);
    var message = MessageFormat.format((String) args[0], array);

    throw new RuntimeException(message);
  }

  //// System  ////

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

  public static JSONObject core_argTest(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
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

  public static JSONObject core_Test(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    JSONObject json = new JSONObject();
    JSONArray  array = new JSONArray();
    json.put("meClass", me.getClassName() );

    var ids = ScriptableObject.getPropertyIds(me);
    for( Object i : ids ) {
      array.put(i);
    }
    json.put("Array", array);
    return json;
  }

  public static Boolean core_hasProperty(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    if (args.length != 1) 
      throw new IllegalArgumentException("Invalid number of arguments in string_format");
    if (!(args[0] instanceof String))
      throw new IllegalArgumentException("The first core_hasProperty argument must be a name");
    var name = (String)args[0];
    var obj  = ScriptableObject.getProperty(me,name);
    if( obj != Scriptable.NOT_FOUND )
      return true;
    return false;
  }

}
