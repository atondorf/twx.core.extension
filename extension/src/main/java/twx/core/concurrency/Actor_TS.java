package twx.core.concurrency;

import twx.core.BaseTS;
import twx.core.concurrency.imp.ActorImp;

import com.thingworx.system.ContextType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.things.Thing;
import com.thingworx.things.handlers.IThingDisposeHandler;
import com.thingworx.things.handlers.IThingInitializeHandler;
import com.thingworx.things.handlers.IThingUpdateHandler;
import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinition;
import com.thingworx.metadata.annotations.ThingworxConfigurationTableDefinitions;
import com.thingworx.metadata.annotations.ThingworxDataShapeDefinition;
import com.thingworx.metadata.annotations.ThingworxEventDefinition;
import com.thingworx.metadata.annotations.ThingworxEventDefinitions;
import com.thingworx.metadata.annotations.ThingworxFieldDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.metadata.annotations.ThingworxSubscription;
import com.thingworx.metadata.annotations.ThingworxSubscriptions;
import com.thingworx.logging.LogUtilities;

import org.json.JSONObject;
import org.slf4j.Logger;

@ThingworxEventDefinitions(events = {
		@ThingworxEventDefinition(name = "actorTask", description = "", category = "", dataShape = "TWX.Core.ActorEvent_DS", isInvocable = true, isPropertyEvent = false, isLocalOnly = false, aspects = {})
})
@ThingworxSubscriptions(subscriptions = {
		@ThingworxSubscription(source = "", eventName = "actorTask", sourceProperty = "", handler = "actorOnMeTask", enabled = true),
		@ThingworxSubscription(source = "", eventName = "ThingStart", sourceProperty = "", handler = "actorOnThingStart", enabled = true)
})

public class Actor_TS extends BaseTS implements IThingInitializeHandler, IThingDisposeHandler, IThingUpdateHandler {

	private static final String ACTOR_THING_SHAPE = Actor_TS.class.getSimpleName();
	protected static final Logger _logger = LogUtilities.getInstance().getApplicationLogger(Actor_TS.class);
	private final ConcurrentMap<String, AtomicInteger> atomicMap = new ConcurrentHashMap<String, AtomicInteger>();

	public Actor_TS() {
	}

	@Override
	public void handleInitializeThing(Thing thing) throws Exception {
		handleInitializeThing(thing, ContextType.NONE);
	}

	@Override
	public void handleInitializeThing(Thing thing, ContextType contextType) throws Exception {
		_logger.info("HandleInitializeThing for 0: {} and CTX: {}.", thing.getName(), contextType);
	}

	@Override
	public void handleDisposeThing(Thing thing) throws Exception {
		_logger.info("HandleDisposeThing for 0: {}.", thing.getName());
	}

	@Override
	public void handleUpdateThing(Thing existingThing, Thing newThing) throws Exception {
		_logger.info("HandleUpdateThing for 0: {} and 1: {}.", existingThing.getName(), newThing.getName());
	}

	@ThingworxServiceDefinition(name = "actorOnThingStart", description = "Subscription handler", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
	public void actorOnThingStart(
			@ThingworxServiceParameter(name = "eventData", description = "", baseType = "INFOTABLE") InfoTable eventData,
			@ThingworxServiceParameter(name = "eventName", description = "", baseType = "STRING") String eventName,
			@ThingworxServiceParameter(name = "eventTime", description = "", baseType = "DATETIME") DateTime eventTime,
			@ThingworxServiceParameter(name = "source", description = "", baseType = "STRING") String source,
			@ThingworxServiceParameter(name = "sourceProperty", description = "", baseType = "STRING") String sourceProperty)
			throws Exception {

	}

	@ThingworxServiceDefinition(name = "actorOnMeTask", description = "Subscription handler", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
	public void actorOnMeTask(
			@ThingworxServiceParameter(name = "eventData", description = "", baseType = "INFOTABLE") InfoTable eventData,
			@ThingworxServiceParameter(name = "eventName", description = "", baseType = "STRING") String eventName,
			@ThingworxServiceParameter(name = "eventTime", description = "", baseType = "DATETIME") DateTime eventTime,
			@ThingworxServiceParameter(name = "source", description = "", baseType = "STRING") String source,
			@ThingworxServiceParameter(name = "sourceProperty", description = "", baseType = "STRING") String sourceProperty) throws Exception {
		
		Thing me = getMe();
		ActorImp actr = ActorImp.get( me.getName() );

		

		actr.queueAtomic.set(0);
		if( !actr.queue.isEmpty() ) {
			this.readyToExecute(actr);
		}

		/*
		let name 	= me.name;
let locked 	= false;
try {
    // locked = mtx_tryLock(name, -1);	// -1 so no wait 
    locked = true;
    if( locked ) {
    	let messageCount = queue_size(name);
        if( messageCount > 0 ) {
        // while( messageCount > 0 ) {
    		let message = queue_pop(name); 
            messageCount--;
			dispatchMessage(message);
        }
    } else {
    	logger.warning("Actor-Thing [{}] Service [{}] : {}", me.name, "actorExecute", "Thread was no able to get lock!" );
    }
   	// reset the atomic to Available ... 
   	atomic_set(name,0);
	// Call readyToExecute() again is to ensure the fairness of all the actors for the thread pool
	if( queue_size(name) > 0 ) {
    	me.actorReadyToExecute();
    }
}
catch (err) {
    logger.error("Actor-Thing [{}] Service [{}] error at line [{}] : {}", me.name, err.fileName, err.lineNumber, err);
}
finally {
	//if (locked) 
	//	mtx_unlock(name);
}

function dispatchMessage(message) {
	// how to handle messages that's service is not available ???
    if( !me[message.service] ) {
        logger.warning("Actor-Thing [{}] Service [{}] : {} ", me.name, "actorExecute", "Service undefined: "+ message.service );
        return;
    }
    me[message.service]({
		time: message.time,
		sender: message.sender,
		subject: message.subject,
		body: message.body
	});
}
		 */

	}

	@ThingworxServiceDefinition(name = "actorAddMessage", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void actorAddMessage(
			@ThingworxServiceParameter(name = "sender", description = "", baseType = "THINGNAME", aspects = {"thingTemplate:Acotr_TS" }) String sender,
			@ThingworxServiceParameter(name = "subject", description = "", baseType = "STRING") String subject,
			@ThingworxServiceParameter(name = "service", description = "", baseType = "STRING") String service,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) throws Exception {
		Thing me = getMe();
		ActorImp actr = ActorImp.get( me.getName() );

		/* 
			try {
				let name 	= me.name;
				// create the json object to queue ..
				let message	= {
					time:		new Date(),
					sender:		sender 		|| me.name,
					subject:	subject 	|| "",
					service:	service 	|| "actorProcessMessage",
					body:		body
				};
				// push to queue ... 
				queue_push(name, message);
				// shedule call of actor ... 
				me.actorReadyToExecute();
			}
			catch (err) {
				logger.error("Actor-Thing [{}] Service [{}] error at line [{}] : {}", me.name, err.fileName, err.lineNumber, err);
			}
		*/			
		
		this.readyToExecute(actr);
	}

	@ThingworxServiceDefinition(name = "actorTell", description = "", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void actorTell(
			@ThingworxServiceParameter(name = "sender", description = "", baseType = "THINGNAME", aspects = {"thingTemplate:Acotr_TS" }) String recipient,
			@ThingworxServiceParameter(name = "subject", description = "", baseType = "STRING") String subject,
			@ThingworxServiceParameter(name = "service", description = "", baseType = "STRING") String service,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) throws Exception {
		/*
		 let actor = Things[recipient];
    if( !actor )
    	throw( recipient + " is not a valid Thing!" );
	if( !actor.ImplementsShape({ thingShapeName: "SIG_Base.Actor_TS" }) )
        throw( recipient + " does not implements SIG_Base.Actor_TS" );
    actor.actorAddMessage({
		sender: me.name,
		subject: subject,
		service: service,
		body: body
	}); 
		 */
	}

	@ThingworxServiceDefinition(name = "actorAddMessage", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void actorBroadcast(
			@ThingworxServiceParameter(name = "subject", description = "", baseType = "STRING") String subject,
			@ThingworxServiceParameter(name = "service", description = "", baseType = "STRING") String service,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) throws Exception {
		/*
		let actors = ThingShapes["SIG_Base.Actor_TS"].GetImplementingThings();
    	actors.rows.toArray().forEach(row => {	
		// don't send to myself
        if( row.name == me.name ) 
            return;
		let actor = Things[row.name];
        actor.actorAddMessage({
			sender: me.name,
			subject: subject,
			service: service,
			body: body
		}); 
    });
		 */
	}

	@ThingworxServiceDefinition(name = "actorProcessMessage", description = "", category = "", isAllowOverride = true, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void actorProcessMessage(
			@ThingworxServiceParameter(name = "time", description = "", baseType = "DATETIME") DateTime time,
			@ThingworxServiceParameter(name = "sender", description = "", baseType = "THINGNAME") String sender,
			@ThingworxServiceParameter(name = "subject", description = "", baseType = "STRING") String subject,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) {

	}

	public void readyToExecute(ActorImp actr) throws Exception {
		if( actr.queueAtomic.compareAndSet(0, 1) ) {
			// fire the event ... 
		}
	}

}
