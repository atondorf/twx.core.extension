package twx.core;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.Thing;
import com.thingworx.webservices.context.ThreadLocalContext;
import com.thingworx.common.exceptions.ThingworxRuntimeException;


public class ThingUtil {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ThingUtil.class);

    public static Thing getThing() throws Exception {
        Object ctx = ThreadLocalContext.getMeContext();
        if (ctx instanceof Thing) 
            return (Thing) ctx;
        throw new ThingworxRuntimeException("Object : " + ctx + " is not Thing.");
    }

}
