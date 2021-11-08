package twx.core.string;

import ch.qos.logback.classic.Logger;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.entities.utils.GroupUtilities;
import com.thingworx.entities.utils.UserUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.security.context.SecurityContext;
import com.thingworx.security.groups.Group;
import com.thingworx.security.users.User;
import com.thingworx.webservices.context.ThreadLocalContext;
import org.mozilla.javascript.*;

public class StringScriptLibrary {
    protected static final Logger _logger = LogUtilities.getInstance().getScriptLogger(StringScriptLibrary.class); 

    public static void string_format(Context cx, Scriptable me, Object[] args, Function func) throws Exception {

        throw new StringException(args.length == 0 ? "Fail called" : (String) args[0]);
    }

}
