package twx.core.impl;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.relationships.RelationshipTypes;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DataShapeUtil {
   
    Optional<DataShape> findDataShape(String dataShapeName) {
        return Optional.ofNullable((DataShape) EntityUtilities.findEntity(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape));
    }

    public DataShape getDataShape(String dataShapeName) {
        DataShape dataShape = (DataShape) EntityUtilities.findEntityDirect(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape);
        if (dataShape != null) {
            if (dataShape.isVisible())
                return dataShape;
            throw new ThingworxRuntimeException("Data shape name " + dataShapeName + " not visible to current user.");
        }
        throw new ThingworxRuntimeException("Invalid data shape name " + dataShapeName);
    }

    
}