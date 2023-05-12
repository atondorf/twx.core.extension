package twx.core.concurrency;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxEventDefinition;
import com.thingworx.metadata.annotations.ThingworxEventDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.metadata.annotations.ThingworxSubscription;
import com.thingworx.metadata.annotations.ThingworxSubscriptions;
import com.thingworx.things.Thing;
import com.thingworx.types.InfoTable;

import twx.core.ThingUtil;

public class ActorTS {

    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ActorTS.class);

    @ThingworxServiceDefinition(name = "actorExecuteNative", description = "Internal Execution", category = "Actor", isAllowOverride = false )
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void actorExecuteNative() throws Exception {
        Thing thing = ThingUtil.getThing();
        
    }

}
