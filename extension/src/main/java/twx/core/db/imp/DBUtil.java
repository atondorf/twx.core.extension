package twx.core.db.imp;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.Thing;
import com.thingworx.webservices.context.ThreadLocalContext;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.things.database.AbstractDatabase;

public class DBUtil {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DBUtil.class);

    public static AbstractDatabase getAbstractDatabase() throws Exception {
        Object ctx = ThreadLocalContext.getMeContext();
        if (ctx instanceof AbstractDatabase) 
            return (AbstractDatabase) ctx;
        throw new ThingworxRuntimeException("Object : " + ctx + " is not Database.");
    }

}