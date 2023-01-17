package twx.core.string;

import java.io.UnsupportedEncodingException;
import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import java.util.Arrays;
import java.text.MessageFormat;
import org.mozilla.javascript.*;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.StringPrimitive;

public class StringScriptLibrary {

	//// Require  ////

    public static void require_core_string(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        //  ScriptableObject.defineClass(me, MultiTimer.class);
    }
    
    //// String Format and Tools  ////

    public static String core_strFormat(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        if (args.length < 1) 
            throw new IllegalArgumentException("Invalid number of arguments in string_format");
        if (!(args[0] instanceof String))
            throw new IllegalArgumentException("The first string_format argument must be a format string");
        var format_str = (String)args[0];
        var array = Arrays.copyOfRange(args,1,args.length);
        return String.format((String)args[0], array);            
    }
    
      public static String core_strMessageFormat(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length < 1) 
            throw new IllegalArgumentException("Invalid number of arguments in string_format");
        if (!(args[0] instanceof String))
            throw new IllegalArgumentException("The first string_format argument must be a format string");
        var format_str = (String)args[0];
        var array = Arrays.copyOfRange(args,1,args.length);
        return MessageFormat.format((String)args[0], array);            
      }
    
      public static Boolean core_strMatchTopic(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length < 2) 
            throw new IllegalArgumentException("Invalid number of arguments in str_topic_match");
        if (!(args[0] instanceof String))
            throw new IllegalArgumentException("The first string_format argument must be a topicFilter string");
        if (!(args[0] instanceof String))
            throw new IllegalArgumentException("The second string_format argument must be a topicName string");
        var topicFilter = (String)args[0];
        var topicName 	= (String)args[1];
        return StringTopicMatcher.match(topicFilter, topicName);
      }
}
