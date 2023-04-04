package twx.core.db;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.relationships.RelationshipTypes;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class DataShapeUtils {
  private static final String AP = "_AP";
  
  private static Map<String, String> dataShapeNameAPMap = Maps.newHashMap();
  
  private static Map<String, String> dataShapeNameMap = Maps.newHashMap();
  
  private static Map<String, String> dataShapePrimaryKeyMap = Maps.newHashMap();
  
  private static Set<String> validDataShapeSet = Sets.newHashSet();
  
  private static Map<String, Boolean> dataShapeMap = Maps.newHashMap();
  
  protected static void clearCache() {
    dataShapeNameAPMap = Maps.newHashMap();
    dataShapeNameMap = Maps.newHashMap();
    dataShapePrimaryKeyMap = Maps.newHashMap();
    validDataShapeSet = Sets.newHashSet();
    dataShapeMap = Maps.newHashMap();
  }
  
  Optional<DataShape> findDataShape(String dataShapeName) {
    return Optional.ofNullable((DataShape)EntityUtilities.findEntity(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape));
  }
  
  public DataShape getDataShape(String dataShapeName) {
    DataShape dataShape = (DataShape)EntityUtilities.findEntityDirect(dataShapeName, RelationshipTypes.ThingworxRelationshipTypes.DataShape);
    if (dataShape != null) {
      if (dataShape.isVisible())
        return dataShape; 
      throw new ThingworxRuntimeException("Data shape name " + dataShapeName + " not visible to current user.");
    } 
    throw new ThingworxRuntimeException("Invalid data shape name " + dataShapeName);
  }
  
  protected Optional<FieldDefinition> getFieldDefinition(DataShape dataShape, String fieldName) {
    FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName);
    if (fieldDefinition != null)
      return Optional.of(fieldDefinition); 
    throw new ThingworxRuntimeException("Invalid field " + dataShape.getName() + ":" + fieldName);
  }
  
  public FieldDefinition getPrimaryKeyField(DataShape dataShape) {
    if (dataShapePrimaryKeyMap.containsKey(dataShape.getName()))
      return dataShape.getFieldDefinition(dataShapePrimaryKeyMap.get(dataShape.getName())); 
    FieldDefinition fieldDefinitionPrimaryKey = null;
    for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
      if (fieldDefinition.isPrimaryKey()) {
        if (fieldDefinitionPrimaryKey != null)
          throw new ThingworxRuntimeException("Data shape:" + dataShape
              .getName() + " have more than one primary key"); 
        fieldDefinitionPrimaryKey = fieldDefinition;
      } 
    } 
    if (fieldDefinitionPrimaryKey != null) {
      dataShapePrimaryKeyMap.put(dataShape.getName(), fieldDefinitionPrimaryKey.getName());
      return fieldDefinitionPrimaryKey;
    } 
    throw new ThingworxRuntimeException("No primary key in Data shape:" + dataShape.getName());
  }
  
  private Optional<String> getAdditionalPropertiesDataShapeName(String dataShapeName) {
    if (dataShapeNameAPMap.containsKey(dataShapeName))
      return Optional.of(dataShapeNameAPMap.get(dataShapeName)); 
    if (dataShapeName != null && !dataShapeName.isEmpty() && 
      !isAdditionalPropertiesDataShapeName(dataShapeName)) {
      dataShapeNameAPMap.put(dataShapeName, dataShapeName + "_AP");
      dataShapeNameMap.put(dataShapeName + "_AP", dataShapeName);
      dataShapeNameMap.put(dataShapeName, dataShapeName);
      return Optional.of(dataShapeNameAPMap.get(dataShapeName));
    } 
    return Optional.empty();
  }
  
  private boolean isAdditionalPropertiesDataShapeName(String dataShapeName) {
    if (!dataShapeMap.containsKey(dataShapeName)) {
      boolean isAdditionalPropertiesDataShape = (dataShapeName != null && dataShapeName.endsWith("_AP"));
      dataShapeMap.put(dataShapeName, Boolean.valueOf(isAdditionalPropertiesDataShape));
    } 
    return ((Boolean)dataShapeMap.get(dataShapeName)).booleanValue();
  }
  
  protected boolean isAdditionalPropertiesDataShape(DataShape dataShape) {
    return (dataShape != null && isAdditionalPropertiesDataShapeName(dataShape.getName()));
  }
  
  protected void validateDataShapeName(String dataShapeName) {
    if (validDataShapeSet.contains(dataShapeName))
      return; 
    if (isAdditionalPropertiesDataShapeName(dataShapeName))
      throw new ThingworxRuntimeException("Invalid data shape name " + dataShapeName); 
    validDataShapeSet.add(dataShapeName);
  }
  
  protected Optional<DataShape> getDataShapeAp(DataShape dataShape) {
    Optional<String> foundDataShapeName = getAdditionalPropertiesDataShapeName(dataShape.getName());
    if (foundDataShapeName.isPresent())
      return findDataShape(foundDataShapeName.get()); 
    return Optional.empty();
  }
  
  protected Optional<DataShape> getDataShapeAp(String dataShapeName) {
    return getDataShapeAp(getDataShape(dataShapeName));
  }
  

  protected DataShape getPrimaryDataShape( DataShape dataShape) {
    if (dataShapeNameMap.containsKey(dataShape.getName()))
      return getDataShape(dataShapeNameMap.get(dataShape.getName())); 
    if (!isAdditionalPropertiesDataShape(dataShape)) {
      dataShapeNameMap.put(dataShape.getName(), dataShape.getName());
      return dataShape;
    } 
    String dataShapeNameAp = dataShape.getName();
    String dataShapeName = dataShapeNameAp.replace("_AP", "");
    Optional<DataShape> foundDataShape = findDataShape(dataShapeName);
    if (foundDataShape.isPresent()) {
      dataShapeNameMap.put(dataShapeNameAp, ((DataShape)foundDataShape.get()).getName());
      return foundDataShape.get();
    } 
    return dataShape;
  }
}
