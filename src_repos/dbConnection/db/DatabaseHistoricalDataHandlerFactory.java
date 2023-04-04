package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.webservices.context.ThreadLocalContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.joda.time.DateTime;

class DatabaseHistoricalDataHandlerFactory {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseHistoricalDataHandlerFactory.class);
  
  private static final String HISTORICAL_DATA_SHAPE_NAME = "PTC.DBConnection.HistoricalData";
  
  private static final String KPI_VALUE_DATA_SHAPE_NAME = "PTC.DBConnection.KPIValue";
  
  private static final String KPI_ELEMENT_VALUE_DATA_SHAPE_NAME = "PTC.DBConnection.KPIElementValue";
  
  private static final String STATE_VALUE = "PTC.DBConnection.StateValue";
  
  private static final List EXCLUDE_DATA_SHAPE_LIST = Arrays.asList(new String[] { "PTC.DBConnection.KPIValue", "PTC.DBConnection.KPIElementValue", "PTC.DBConnection.StateValue" });
  
  protected static final String REF_KEY = "ReferenceKey";
  
  private static final String DATA_SHAPE_NAME = "DataShapeName";
  
  private static final String TABLE_NAME = "TableName";
  
  private static final String FIELD_NAME = "FieldName";
  
  private static final String COLUMN_NAME = "ColumnName";
  
  private static final String VALUE = "Value";
  
  private static final String ACTION_TYPE = "ActionType";
  
  private static final String TIMESTAMP = "TimeStamp";
  
  protected static final String __TIMESTAMP = "__TimeStamp";
  
  private static final String USER_NAME = "UserName";
  
  private static final String DATA_TYPE = "DataType";
  
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private FieldInformationUtil fieldInformationUtil = new FieldInformationUtil();
  
  protected List<DatabaseInsertTableHandler> getHistoricalDataTableHandlers(ActionType actionType, DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    List<DatabaseInsertTableHandler> historicalDataTableHandlers = Lists.newArrayList();
    DataShape historicalDataShape = getHistoricalDataShape();
    if (logHistorical(dataShape, actionType, historicalDataShape)) {
      Optional<DatabaseInsertTableHandler> found;
      DateTime eventTime = DateTime.now();
      if (valueCollection.containsKey("__TimeStamp")) {
        Object timeValue = valueCollection.getValue("__TimeStamp");
        if (timeValue instanceof DateTime)
          eventTime = (DateTime)valueCollection.getValue("__TimeStamp"); 
      } 
      switch (actionType) {
        case CREATE:
          if (!this.dataShapeUtils.isAdditionalPropertiesDataShape(dataShape)) {
            Optional<DatabaseInsertTableHandler> optional = getHistoricalDataTableHandler(dataShape, null, 
                getPrimaryKey(dataShape, valueCollection), null, actionType, databaseHandler, historicalDataShape, eventTime);
            Objects.requireNonNull(historicalDataTableHandlers);
            optional.ifPresent(historicalDataTableHandlers::add);
          } 
          historicalDataTableHandlers.addAll(getPropertiesHistoricalDataTableHandler(dataShape, valueCollection, databaseHandler, actionType, historicalDataShape, eventTime));
          return historicalDataTableHandlers;
        case UPDATE:
          return getPropertiesHistoricalDataTableHandler(dataShape, valueCollection, databaseHandler, actionType, historicalDataShape, eventTime);
        case DELETE:
          found = getHistoricalDataTableHandler(dataShape, null, 
              getPrimaryKey(dataShape, valueCollection), null, actionType, databaseHandler, historicalDataShape, eventTime);
          Objects.requireNonNull(historicalDataTableHandlers);
          found.ifPresent(historicalDataTableHandlers::add);
          return historicalDataTableHandlers;
      } 
      throw new RuntimeException("Action undefined:" + actionType);
    } 
    return historicalDataTableHandlers;
  }
  
  private List<DatabaseInsertTableHandler> getPropertiesHistoricalDataTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler, ActionType actionType, DataShape historicalDataShape, DateTime eventTime) {
    List<DatabaseInsertTableHandler> databaseInsertTableHandlers = Lists.newArrayList();
    String primaryKey = getPrimaryKey(dataShape, valueCollection);
    List<String> nullFieldNames = this.fieldInformationUtil.getNullFieldNames(valueCollection);
    for (FieldDefinition fieldDefinition : dataShape.getFields().values()) {
      if (valueCollection.has(fieldDefinition.getName()) || nullFieldNames
        .contains(fieldDefinition.getName())) {
        String value = valueCollection.getStringValue(fieldDefinition.getName());
        Optional<DatabaseInsertTableHandler> found = getHistoricalDataTableHandler(dataShape, fieldDefinition, primaryKey, value, actionType, databaseHandler, historicalDataShape, eventTime);
        Objects.requireNonNull(databaseInsertTableHandlers);
        found.ifPresent(databaseInsertTableHandlers::add);
      } 
    } 
    return databaseInsertTableHandlers;
  }
  
  private boolean logHistorical(DataShape dataShape, ActionType type, DataShape historicalDataShape) {
    if (dataShape == null)
      return false; 
    String dataShapeName = dataShape.getName();
    return (dataShapeName != null && !dataShapeName.isEmpty() && (type == ActionType.CREATE || type == ActionType.DELETE || type == ActionType.UPDATE) && historicalDataShape != null && 
      
      !dataShape.matches(historicalDataShape.getDataShape()) && 
      !EXCLUDE_DATA_SHAPE_LIST.contains(dataShapeName));
  }
  
  private boolean logHistorical(DataShape dataShape, FieldDefinition fieldDefinition, ActionType type) {
    if (fieldDefinition != null && type != ActionType.CREATE && type != ActionType.UPDATE)
      return false; 
    if (fieldDefinition != null && fieldDefinition.isPrimaryKey())
      return false; 
    if (HistoricalDataLoggingConfig.isLogEnable())
      return true; 
    return HistoricalDataLoggingConfig.isLogEnable(dataShape, fieldDefinition, type);
  }
  
  private Optional<DatabaseInsertTableHandler> getHistoricalDataTableHandler(DataShape dataShape, FieldDefinition fieldDefinition, String primaryKey, String value, ActionType actionType, DatabaseHandler databaseHandler, DataShape historicalDataShape, DateTime eventTime) {
    if (logHistorical(dataShape, fieldDefinition, actionType)) {
      ValueCollection historicalValueCollection = new ValueCollection();
      try {
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("ReferenceKey"), primaryKey);
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("DataShapeName"), dataShape
            .getName());
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("TableName"), databaseHandler
            .getTableName(dataShape));
        if (fieldDefinition != null) {
          historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("FieldName"), fieldDefinition
              .getName());
          historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("ColumnName"), databaseHandler
              .getColumnName(fieldDefinition));
          historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("Value"), value);
          historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("DataType"), fieldDefinition
              .getBaseType().name());
        } 
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("ActionType"), 
            Integer.valueOf(actionType.getValue()));
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("TimeStamp"), eventTime);
        historicalValueCollection.SetValue(historicalDataShape.getFieldDefinition("UserName"), 
            ThreadLocalContext.getSecurityContext().getName());
        return Optional.of(new DatabaseInsertTableHandler(historicalDataShape, historicalValueCollection, databaseHandler));
      } catch (Exception e) {
        _logger.error("Error setting value for historical data.");
        throw new ThingworxRuntimeException(e);
      } 
    } 
    return Optional.empty();
  }
  
  private String getPrimaryKey(DataShape dataShape, ValueCollection valueCollection) {
    FieldDefinition fieldDefinition = this.dataShapeUtils.getPrimaryKeyField(dataShape);
    if (valueCollection != null)
      return valueCollection.getStringValue(fieldDefinition.getName()); 
    return null;
  }
  
  protected static DataShape getHistoricalDataShape() {
    return (new DataShapeUtils()).getDataShape("PTC.DBConnection.HistoricalData");
  }
}
