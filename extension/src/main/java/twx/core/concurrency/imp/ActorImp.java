package twx.core.concurrency.imp;

import com.thingworx.things.Thing;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.system.subsystems.eventprocessing.EventProcessingSubsystem;

public class ActorImp {
    
    public String   actorName;
    public Thing    actorThing;

    public String getName() { return this.actorName; }

    public ActorImp(String actorName) {
        this.actorName  = actorName;
        this.actorThing = ThingUtilities.findThing(actorName);
    }

    

}
