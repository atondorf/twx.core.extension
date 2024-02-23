package twx.core.imp;

import org.json.JSONObject;
import org.slf4j.Logger;
import com.thingworx.things.Thing;
import com.thingworx.things.handlers.*;
import com.thingworx.thingshape.ThingShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.system.ContextType;
import com.thingworx.webservices.context.ThreadLocalContext;
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

@ThingworxPropertyDefinitions(properties = { @ThingworxPropertyDefinition(name = "Enabled", description = "Current enabled status", baseType = "BOOLEAN", aspects = { "isPersistent:false", "isReadOnly:false" }) })
@ThingworxEventDefinitions(events = { @ThingworxEventDefinition(name = "Timer", description = "Timer event", dataShape = "TimerEvent") }) 
@ThingworxConfigurationTableDefinitions(tables = {
    @ThingworxConfigurationTableDefinition(name = "Settings", description = "General Settings", isMultiRow = false, isHidden = true, dataShape = @ThingworxDataShapeDefinition(fields = {
        @ThingworxFieldDefinition(name = "updateRate", description = "Update rate", baseType = "NUMBER", aspects = { "defaultValue:60000" }),
        @ThingworxFieldDefinition(name = "enabled", description = "Automatically enable timer on startup", baseType = "BOOLEAN", aspects = { "defaultValue:true" }),
        @ThingworxFieldDefinition(name = "runAsUser", description = "User context in which to run event handlers", baseType = "USERNAME") 
    })) 
})
public class BaseTS extends ThingShape implements IThingInitializeHandler, IThingUpdateHandler, IThingDisposeHandler {

    private static Logger _logger = LogUtilities.getInstance().getScriptLogger(BaseTS.class);

    protected Thing getMe() throws Exception {
        final Object meObj = ThreadLocalContext.getMeContext();
        if (meObj instanceof Thing) {
            return (Thing) meObj;
        } else {
            this.logError("getMe() Cannot cast me to Thing.");
            throw new Exception("Cannot cast me to Thing");
        }
    }

    protected String getMeName() throws Exception {
        final Thing me = this.getMe();
        return me.getName();
    }

    protected void logError(String text) {
        try {
            _logger.error("[wupBaseThingShape(" + this.getMeName() + ")]." + text);
        } catch (Exception e) {

        }
    }

    @Override
    public void handleInitializeThing(Thing arg0) throws Exception {
        _logger.info("BaseTS - handleInitializeThing Thing: " + this.getThingStatus(arg0).toString() );
    }

    @Override
    public void handleInitializeThing(Thing arg0, ContextType arg1) throws Exception {
        _logger.info("BaseTS - handleInitializeThing Thing: " + this.getThingStatus(arg0).toString() + " Context: " + arg1 );
    }

    @Override
    public void handleUpdateThing(Thing arg0, Thing arg1) throws Exception {
        _logger.info("BaseTS - handleUpdateThing Thing0: " + this.getThingStatus(arg0).toString() + " Thing1: " + this.getThingStatus(arg1).toString() );
        _logger.info("BaseTS - handleUpdateThing Same Things ? == " + ( arg0 == arg1 ) );
        _logger.info("BaseTS - handleUpdateThing Same HASH ? == " + ( System.identityHashCode(arg0) == System.identityHashCode(arg1) ) );
        
    }

    @Override
    public void handleDisposeThing(Thing arg0) throws Exception {
        _logger.info("BaseTS - handleDisposeThing " + this.getThingStatus(arg0).toString() );
    }

    protected JSONObject getThingStatus(Thing thing) throws Exception {
        JSONObject json = new JSONObject();
        json.put( "thingName", thing.getName() );
        json.put( "thingState", thing.getState() );
        json.put( "thingEnabled", thing.IsEnabled() );
        json.put( "thingLastMod", thing.getLastModifiedDate() );
        json.put( "thingID", thing.getIdentifier() );
        json.put( "thingHash", System.identityHashCode(thing) );
        
        return json;
    }
}
