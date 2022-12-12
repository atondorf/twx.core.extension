package twx.core;

import com.thingworx.things.Thing;
import com.thingworx.webservices.context.ThreadLocalContext;

public class BaseTS {
    
    protected Thing getMe() throws Exception {
        final Object meObj = ThreadLocalContext.getMeContext();
        if (meObj instanceof Thing) {
            return (Thing) meObj;
        } else {
            throw new Exception("Cannot cast me to Thing");
        }
    }

    protected String getMeName() throws Exception {
        final Thing me = this.getMe();
        return me.getName();
    }

    protected String getMeID(String id/*optional*/) throws Exception {
        final Thing me = this.getMe();
        return getThingID(me,id);
    }

    protected String getThingID(Thing thing, String id/*optional*/) {
        String mutexId = thing.getName();
        if (id != null) {
            if (!id.equals("")) mutexId = mutexId+"/"+id;
        }
        return mutexId;
    }

}
