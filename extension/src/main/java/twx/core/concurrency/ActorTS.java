package twx.core.concurrency;

import twx.core.BaseTS;
import com.thingworx.system.ContextType;
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
import com.thingworx.logging.LogUtilities;

import org.json.JSONObject;
import org.slf4j.Logger;

@ThingworxConfigurationTableDefinitions(tables = {
		@ThingworxConfigurationTableDefinition(name = "Settings", description = "General Settings", isMultiRow = false, isHidden = true, dataShape = @ThingworxDataShapeDefinition(fields = {
				@ThingworxFieldDefinition(name = "runAsUser", description = "User context in which to run event handlers", baseType = "USERNAME") })
	)})
public class ActorTS extends BaseTS implements IThingInitializeHandler, IThingDisposeHandler, IThingUpdateHandler {

	private static final String ACTOR_THING_SHAPE = ActorTS.class.getSimpleName();
	
	protected static final Logger _logger = LogUtilities.getInstance().getApplicationLogger(ActorTS.class);
	
	public ActorTS() {
		// TODO Auto-generated constructor stub
	}
	
	@ThingworxServiceDefinition(isLocalOnly = true, isPrivate = true, name = "OnActorManagerChanged", description = "Notify the Actor Manager", category = "")
	public void OnActorManagerChanged( 	@ThingworxServiceParameter(name = "eventName", description = "Event name", baseType = "STRING") String eventName, 
										@ThingworxServiceParameter(name = "eventTime", description = "Event time", baseType = "DATETIME") DateTime eventTime, 
										@ThingworxServiceParameter(name = "source", description = "Event source", baseType = "STRING") String source, 
										@ThingworxServiceParameter(name = "sourceProperty", description = "Event source property", baseType = "STRING") String sourceProperty, 
										@ThingworxServiceParameter(name = "eventData", description = "Event data", baseType = "INFOTABLE") InfoTable eventData
	) throws Exception {
		/*
		Thing me = (Thing)ThreadLocalContext.getMeContext();
	    if (me instanceof RemoteThing) {
	      InfoTablePrimitive newValue = (InfoTablePrimitive)eventData.getFirstRow().getPrimitive("newValue");
	      String newThingName = newValue.getValue().getFirstRow().getStringValue("value");
	      InfoTablePrimitive oldValue = (InfoTablePrimitive)eventData.getFirstRow().getPrimitive("oldValue");
	      String oldThingName = oldValue.getValue().getFirstRow().getStringValue("value");
	      if (oldThingName != null && !oldThingName.equals(newThingName))
	        updateIndustrialThing(oldThingName, me.getName()); 
	      if (newThingName != null && !newThingName.equals(oldThingName))
	        updateIndustrialThing(newThingName, me.getName()); 
	    } 
	    */
	}
	
	@Override
	public void handleInitializeThing(Thing thing) throws Exception {
		handleInitializeThing(thing, ContextType.NONE);	
	}

	@Override
	public void handleInitializeThing(Thing thing, ContextType contextType) throws Exception {
		_logger.info("HandleInitializeThing for 0: {} and CTX: {}.", thing.getName(), contextType );	
		/*
		if (!contextType.isSecondaryOperation()) {
		      String icsThing = thing.getProperty("IndustrialThing").getValue().getStringValue();
		      Thing thingEntity = ThingManager.getInstance().getEntity(icsThing);
		      if (thingEntity instanceof IndustrialGateway) {
		        IndustrialGateway gatewayThing = (IndustrialGateway)thingEntity;
		        if (gatewayThing.isConnected()) {
		          ValueCollection params = new ValueCollection();
		          params.put("thingName", new StringPrimitive(thing.getName()));
		          gatewayThing.callServiceAsync("AddIndustrialThing", params, BaseTypes.NOTHING, 1000);
		        } 
		      } 
		    } 
*/
	}
	
	@Override
	public void handleDisposeThing(Thing thing) throws Exception {
		_logger.info("HandleDisposeThing for 0: {}.", thing.getName() );		
	}
	
	@Override
	public void handleUpdateThing(Thing existingThing, Thing newThing) throws Exception {
		_logger.info("HandleUpdateThing for 0: {} and 1: {}.", existingThing.getName(), newThing.getName() );
		/*
		setIsIndustrialThing(newThing.getImplementedThingShapes()
		        .contains(new EntityReference(INDUSTRIAL_THING_SHAPE, RelationshipTypes.ThingworxRelationshipTypes.ThingShape)));
		    if (!isIndustrialThing())
		      EntityUtilities.walkHierarchy(new EntityReference((RootEntity)newThing.getThingTemplate()), new IEntityNodeVisitor() {
		            public VisitResult preVisitEntity(EntityReference entity) {
		              if (entity.getType() == RelationshipTypes.ThingworxRelationshipTypes.ThingShape)
		                return VisitResult.Continue; 
		              return VisitResult.SkipEntity;
		            }
		            
		            public VisitResult visitEntity(IHierarchicalEntity entity) throws Exception {
		              if (entity.getName().equals(IndustrialThingShape.INDUSTRIAL_THING_SHAPE)) {
		                IndustrialThingShape.this.setIsIndustrialThing(true);
		                return VisitResult.Terminate;
		              } 
		              return VisitResult.Continue;
		            }
		            
		            public VisitResult visitEntityFailed(EntityReference entity, Exception error) throws Exception {
		              throw error;
		            }
		          }); 
		    if (!isIndustrialThing())
		      removeIndustrialThing(existingThing);
		 */		       
	}

	@ThingworxServiceDefinition(name = "processMessage", description = "", category = "", isAllowOverride = true, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void processMessage(
			@ThingworxServiceParameter(name = "sender", description = "", baseType = "STRING") String sender,
			@ThingworxServiceParameter(name = "topic", description = "", baseType = "STRING") String topic,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) {
		_logger.trace("Entering Service: processMessage");
		_logger.trace("Exiting Service: processMessage");
	}

	@ThingworxServiceDefinition(name = "addMessage", description = "", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void addMessage(
			@ThingworxServiceParameter(name = "sender", description = "", baseType = "STRING") String sender,
			@ThingworxServiceParameter(name = "topic", description = "", baseType = "STRING") String topic,
			@ThingworxServiceParameter(name = "body", description = "", baseType = "JSON") JSONObject body) {
		_logger.trace("Entering Service: addMessage");
		_logger.trace("Exiting Service: addMessage");
	}

	
	
}
