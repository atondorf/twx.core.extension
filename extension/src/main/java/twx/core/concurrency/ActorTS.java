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

import twx.core.BaseTS;

@ThingworxEventDefinitions(events = {
    @ThingworxEventDefinition(name = "actorRequestExecute", description = "Event triggered, when underlying queue gets a new message\t\t", category = "Actor", dataShape = "TWX.Core.ActorEvent_DS", isInvocable = true, isPropertyEvent = false, isLocalOnly = true, aspects = {} ) 
})
@ThingworxSubscriptions(subscriptions = {
    @ThingworxSubscription(source = "", eventName = "actorRequestExecute", sourceProperty = "", handler = "actorExecute", enabled = true ) 
})
public class ActorTS extends BaseTS {
    
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ActorTS.class);


    @ThingworxServiceDefinition(isLocalOnly = true, isPrivate = true, name = "actorExecute", description = "Handler for Execution of Actor", category = "Actor")
	public void actorExecute(
			@ThingworxServiceParameter(name = "eventName", description = "Event name", baseType = "STRING") String eventName, 
			@ThingworxServiceParameter(name = "eventTime", description = "Event time", baseType = "DATETIME") DateTime eventTime,
			@ThingworxServiceParameter(name = "source", description = "Event source", baseType = "STRING") String source, 
			@ThingworxServiceParameter(name = "sourceProperty", description = "Event source property", baseType = "STRING") String sourceProperty, 
			@ThingworxServiceParameter(name = "eventData", description = "Event data", baseType = "INFOTABLE") InfoTable eventData
	) throws Exception {
        Thing me = this.getMe();

	}

	@ThingworxServiceDefinition(name = "actorProcess", description = "Called by Actors to handle a Message", category = "Actor", isAllowOverride = true, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void actorProcess(
        @ThingworxServiceParameter(name = "timestamp", description = "", baseType = "DATETIME") DateTime timestamp,    
        @ThingworxServiceParameter(name = "sender", description = "", baseType = "THINGNAME") String sender,
        @ThingworxServiceParameter(name = "subject", description = "", baseType = "STRING") String subject,
        @ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body
    )  {  /* EMPTY */  }   


    @ThingworxServiceDefinition(name = "tell", description = "Sends a message to an actor", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void tell(
        @ThingworxServiceParameter(name = "recipient", description = "", baseType = "STRING") String recipient,
        @ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) {
            
    }


}
