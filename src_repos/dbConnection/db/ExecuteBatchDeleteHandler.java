package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;


class ExecuteBatchDeleteHandler extends ExecuteBatchActionHandler {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteBatchDeleteHandler.class);
  
  protected static final String BATCH_DELETE_DATA_SHAPE_NAME = "PTC.DBConnection.BatchDelete";
  
  private static final String UID = "UID";
  
  ExecuteBatchDeleteHandler( DatabaseHandler databaseHandler, InfoTable infoTable) throws Exception {
    super(databaseHandler);
    this.infoTable = getBatchActionInfoTable(infoTable);
  }
  
  private InfoTable getBatchActionInfoTable(InfoTable infoTable) throws Exception {
    DataShape batchActionDataShape = this.dataShapeUtils.getDataShape("PTC.DBConnection.BatchAction");
    FieldDefinition actionFieldDefinition = batchActionDataShape.getFieldDefinition("action");
    FieldDefinition dataShapeNameFieldDefinition = batchActionDataShape.getFieldDefinition("dataShapeName");
    FieldDefinition valuesFieldDefinition = batchActionDataShape.getFieldDefinition("values");
    InfoTable batchActionInfoTable = InfoTableInstanceFactory.createInfoTableFromDataShape(batchActionDataShape.getDataShape());
    for (ValueCollection valueCollection : infoTable.getRows()) {
      String dataShapeName = valueCollection.getStringValue("dataShapeName");
      String deleteUid = valueCollection.getStringValue("UID");
      DataShape deleteDataShape = this.dataShapeUtils.getDataShape(dataShapeName);
      ValueCollection deleteValueCollection = new ValueCollection();
      deleteValueCollection.SetValue(this.dataShapeUtils.getPrimaryKeyField(deleteDataShape), deleteUid);
      ValueCollection resultValueCollection = new ValueCollection();
      resultValueCollection.SetValue(actionFieldDefinition, ActionType.DELETE.name());
      resultValueCollection.SetValue(dataShapeNameFieldDefinition, dataShapeName);
      resultValueCollection.SetValue(valuesFieldDefinition, deleteValueCollection.toInfoTable());
      batchActionInfoTable.addRow(resultValueCollection);
    } 
    if (_logger.isDebugEnabled())
      _logger.debug("InfoTable for batch action:" + batchActionInfoTable.toJSONSerialized()); 
    return batchActionInfoTable;
  }
}
