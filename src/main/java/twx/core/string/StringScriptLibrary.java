package twx.core.string;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;

import org.mozilla.javascript.*;

public class StringScriptLibrary {
    protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(StringScriptLibrary.class); 

    public static void string_format(Context cx, Scriptable me, Object[] args, Function func) throws Exception {

        throw new StringException(args.length == 0 ? "Fail called" : (String) args[0]);
    }

}
