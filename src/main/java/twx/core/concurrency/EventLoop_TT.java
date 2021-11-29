package twx.core.concurrency;

import com.thingworx.common.utils.StringUtilities;
import com.thingworx.security.users.RunAsUserUtilities;
import com.thingworx.security.authentication.AuthenticatorException;
import com.thingworx.system.ContextType;
import com.thingworx.metadata.annotations.ThingworxBaseTemplateDefinition;
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
import com.thingworx.things.events.EntityEventProcessor;
import com.thingworx.types.primitives.structs.VTQ;
import com.thingworx.things.properties.ThingProperty;
import com.thingworx.things.Thing;
import com.thingworx.things.ThingState;
import com.thingworx.things.events.EntityEventProcessor;
import com.thingworx.things.events.ThingworxEvent;
import com.thingworx.things.properties.ThingProperty;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.structs.VTQ;
import com.thingworx.webservices.context.ThreadLocalContext;
import java.util.Collections;

@ThingworxConfigurationTableDefinitions(tables = {
    @ThingworxConfigurationTableDefinition(name = "Settings", description = "General Settings", isMultiRow = false, isHidden = true, dataShape = @ThingworxDataShapeDefinition(fields = {
        @ThingworxFieldDefinition(name = "enabled", description = "Automatically enable loop on startup", baseType = "BOOLEAN", aspects = {"defaultValue:true" }),
        @ThingworxFieldDefinition(name = "runAsUser", description = "User context in which to run event handlers", baseType = "USERNAME") })) })
public class EventLoop_TT extends Thing {
  private static final long serialVersionUID = 1L;

  private static final String PROPERTY_ENABLED = "Enabled";

  private EntityEventProcessor entityEventProcessor = EntityEventProcessor.getInstance();

	@Override
	protected void initializeThing(ContextType contextType) throws Exception {
		super.initializeThing(contextType);

	}

	@Override
	protected void preprocessSetPropertyVTQ(ThingProperty property, VTQ newValue, boolean withUpdate) throws Exception {
		super.preprocessSetPropertyVTQ(property, newValue, withUpdate);
    String propertyName = property.getPropertyDefinition().getName();
    if (propertyName.equalsIgnoreCase("Enabled"))
      if (getState() == ThingState.ON) {
        Boolean value = ((BooleanPrimitive) newValue.getValue()).getValue();
        if (value.booleanValue()) {
          startLoop();
        } else {
          stopLoop();
        }
        this.entityEventProcessor.addEntityEventToCache(Collections.singletonList(getName()));
      }    
	}

  @Override
	protected void startThing(ContextType contextType) throws Exception {
		// TODO Auto-generated method stub
		super.startThing(contextType);
	}

  @Override
	protected void processStartNotification(ContextType contextType) {
		// TODO Auto-generated method stub
		super.processStartNotification(contextType);
	}

	@Override
	protected void stopThing(ContextType contextType) throws Exception {
		// TODO Auto-generated method stub
		super.stopThing(contextType);
	}

  protected void startLoop() {

  }

  protected void stopLoop() {
  
  }

  @ThingworxServiceDefinition(name = "DisableLoop", description = "Disable/Stop this eventLoop", category = "Timer")
  public void DisableLoop() throws Exception {
    setPropertyVTQ("Enabled", new VTQ((IPrimitiveType) new BooleanPrimitive(Boolean.valueOf(false))), true);
  }

  @ThingworxServiceDefinition(name = "EnableLoop", description = "Enable this eventLoop", category = "Timer")
  public void EnableLoop() throws Exception {
    setPropertyVTQ("Enabled", new VTQ((IPrimitiveType) new BooleanPrimitive(Boolean.valueOf(true))), true);
  }

  private void validateRunAsUser() throws Exception {
    String runAsUser = (String) this.getConfigurationSetting("Settings", "runAsUser");
    try {
      if (!StringUtilities.isNullOrEmpty(runAsUser))
        RunAsUserUtilities.isUserEnabled(runAsUser);
    } catch (AuthenticatorException e) {
      throw new RuntimeException(e);
    }
  }

}
