package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;


class ExecuteBatchActionHandler extends AbstractExecuteHandler<InfoTable> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteActionHandler.class);
  
  protected static final String BATCH_ACTION_DATA_SHAPE_NAME = "PTC.DBConnection.BatchAction";
  
  protected static final String DATA_SHAPE_NAME = "dataShapeName";
  
  protected static final String ACTION = "action";
  
  protected static final String VALUES = "values";
  
  protected InfoTable infoTable;
  
  ExecuteBatchActionHandler( DatabaseHandler databaseHandler, InfoTable infoTable) {
    this(databaseHandler);
    this.infoTable = infoTable;
  }
  
  ExecuteBatchActionHandler( DatabaseHandler databaseHandler) {
    super(databaseHandler);
  }
  
  public InfoTable execute() throws Exception {
    long start = 0L;
    if (_logger.isDebugEnabled())
      start = System.currentTimeMillis(); 
    DataShape batchActionDataShape = this.dataShapeUtils.getDataShape("PTC.DBConnection.BatchAction");
    FieldDefinition actionFieldDefinition = batchActionDataShape.getFieldDefinition("action");
    FieldDefinition dataShapeNameFieldDefinition = batchActionDataShape.getFieldDefinition("dataShapeName");
    FieldDefinition valuesFieldDefinition = batchActionDataShape.getFieldDefinition("values");
    InfoTable resultInfoTable = InfoTableInstanceFactory.createInfoTableFromDataShape(batchActionDataShape.getDataShape());
    for (ValueCollection valueCollection : this.infoTable.getRows()) {
      ActionType actionType = ActionType.valueOf(valueCollection.getStringValue(actionFieldDefinition.getName()));
      String dataShapeName = valueCollection.getStringValue(dataShapeNameFieldDefinition.getName());
      InfoTable values = (InfoTable)valueCollection.getValue(valuesFieldDefinition.getName());
      ExecuteActionHandler executeActionHandler = new ExecuteActionHandler(getDatabaseHandler(), actionType, dataShapeName, values);
      InfoTable actionInfoTable = executeActionHandler.execute();
      ValueCollection resultValueCollection = new ValueCollection();
      resultValueCollection.SetValue(actionFieldDefinition, actionType.name());
      resultValueCollection.SetValue(dataShapeNameFieldDefinition, dataShapeName);
      resultValueCollection.SetValue(valuesFieldDefinition, actionInfoTable);
      resultInfoTable.addRow(resultValueCollection);
    } 
    if (_logger.isDebugEnabled()) {
      long stop = System.currentTimeMillis();
      long time = stop - start;
      _logger.debug("Batch Action Time:" + time);
    } 
    return resultInfoTable;
  }
}
