package twx.core.imp;

import java.util.Map;
import java.util.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;

import ch.qos.logback.classic.Logger;

public class DataShapeUtils {
    private static Logger logger = LogUtilities.getInstance().getDatabaseLogger(DataShapeUtils.class);
    
    private static Map<String, String> dataShapeTypeMap = Maps.newHashMap();

    public static void clearCache() {
        dataShapeTypeMap = Maps.newHashMap();
    }
    // TypeMapping ...
    // --------------------------------------------------------------------------------
    public static void registerDataShape(String name, DataShape shape) {
        
    }
    // endregion
    // General Utils to create and get DataShape instances ...
    // --------------------------------------------------------------------------------
    public static Optional<DataShape> findDataShape(String dataShapeName) {
        return Optional.ofNullable((DataShape) EntityUtilities.findEntity(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape));
    }

    public static DataShape getDataShape(String dataShapeName) {
        DataShape dataShape = (DataShape) EntityUtilities.findEntityDirect(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape);
        if (dataShape != null) {
            if (dataShape.isVisible())
                return dataShape;
            throw new ThingworxRuntimeException("Data shape name " + dataShapeName + " not visible to current user.");
        }
        throw new ThingworxRuntimeException("Invalid data shape name " + dataShapeName);
    }

    public static DataShape getDatashapeDirect(String dataShapeName) {
        DataShape dataShape = (DataShape) EntityUtilities.findEntityDirect(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape);
        if (dataShape != null) {
            return dataShape;
        }
        throw new ThingworxRuntimeException("Invalid data shape name " + dataShapeName);
    }
    // endregion
}
