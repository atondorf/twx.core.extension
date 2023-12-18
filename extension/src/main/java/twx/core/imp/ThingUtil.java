package twx.core.imp;

import java.util.Optional;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.webservices.context.ThreadLocalContext;

public class ThingUtil {
    public static Thing getMeThing() throws Exception {
        Object ctx = ThreadLocalContext.getMeContext();
        if (ctx instanceof Thing)
            return (Thing) ctx;
        throw new ThingworxRuntimeException("Object : " + ctx + " is not Thing.");
    }

    public static Optional<Thing> findThing(String thingName) {
        return Optional.ofNullable((Thing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing));
    }

    public static Thing getThing(String thingName) {
        Thing thing = (Thing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null) {
            if (thing.isVisible())
                return thing;
            throw new ThingworxRuntimeException("Thing:" + thing.getName() + " is not visible for current user.");
        }
        throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
    }

    public static Thing getThingDirect(String thingName) {
        Thing thing = (Thing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null)
            return thing;
        throw new ThingworxRuntimeException("Thing:" + thingName + " does not exist.");
    }

    public static FileRepositoryThing getFileRepos(String thingName) {
        FileRepositoryThing thing = (FileRepositoryThing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null) {
            if (thing.isVisible())
                return thing;
            throw new ThingworxRuntimeException("FileRepository:" + thing.getName() + " is not visible for current user.");
        }
        throw new ThingworxRuntimeException("FileRepository:" + thingName + " does not exist.");
    }

    public static FileRepositoryThing getFileReposDirect(String thingName) {
        FileRepositoryThing thing = (FileRepositoryThing) EntityUtilities.findEntityDirect(thingName, RelationshipTypes.ThingworxRelationshipTypes.Thing);
        if (thing != null)
            return thing;
        throw new ThingworxRuntimeException("FileRepository:" + thingName + " does not exist.");
    }

}
