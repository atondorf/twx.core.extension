package twx.core.db2.util;

import java.util.Optional;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.relationships.RelationshipTypes;

public class TwxDataShapeUtil {

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

    public static String getShortName(String dataShapeName) {
        DataShape ds = findDataShape(dataShapeName).get();
        return getShortName(ds);
    }

    public static String getShortName(DataShape ds) {
        if (ds != null) {
            String dataShapeName = ds.getName();
            String projectName = ds.getProjectName();
            int start = 0;
            if (!projectName.isEmpty())
                start = dataShapeName.startsWith(projectName) ? projectName.length() + 1 : 0;
            int end = dataShapeName.endsWith("_DS") ? dataShapeName.length() - 3 : dataShapeName.length();
            return dataShapeName.substring(start, end);
        }
        return "";
    }
}