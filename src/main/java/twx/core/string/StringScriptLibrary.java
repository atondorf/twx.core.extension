package twx.core.string;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import java.util.Arrays;
import java.text.MessageFormat;
import org.mozilla.javascript.*;

public class StringScriptLibrary {
    protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(StringScriptLibrary.class); 

    public static String str_format(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length < 1) 
            throw new Exception("Invalid number of arguments in string_format");
        if (!(args[0] instanceof String))
            throw new Exception("The first string_format argument must be a format string");
        var format_str = (String)args[0];
        var array = Arrays.copyOfRange(args,1,args.length);
        return String.format((String)args[0], array);            
    }

    public static String str_message_format(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length < 1) 
            throw new Exception("Invalid number of arguments in string_format");
        if (!(args[0] instanceof String))
            throw new Exception("The first string_format argument must be a format string");
        var format_str = (String)args[0];
        var array = Arrays.copyOfRange(args,1,args.length);
        return MessageFormat.format((String)args[0], array);            
    }
    
    public static Boolean str_match_topic(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
    	if(args.length < 2 )
    		throw new Exception("Invalid number of arguments in string_format");
    	if (!(args[0] instanceof String))
            throw new Exception("The first string_format argument must be a format string");
    	if (!(args[1] instanceof String))
            throw new Exception("The second string_format argument must be a format string");
    	return match_topic( (String)args[0], (String)args[1] );
    }
    
    public static Boolean match_topic(String topic, String subscribe ) throws Exception {
    	Boolean ret = false;
    	
    	return ret;
    }
  
}
