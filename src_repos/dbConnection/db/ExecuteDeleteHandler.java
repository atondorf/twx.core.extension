package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;


class ExecuteDeleteHandler extends ExecuteActionHandler {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteDeleteHandler.class);
  
  private InfoTableUtils infoTableUtils = new InfoTableUtils();
  
  ExecuteDeleteHandler( DatabaseHandler databaseHandler, String dataShapeName, Object uid) throws Exception {
    super(databaseHandler, ActionType.DELETE, dataShapeName);
    this.infoTable = getActionInfoTable(dataShapeName, uid);
  }
  
  private InfoTable getActionInfoTable(String dataShapeName, Object uid) throws Exception {
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    InfoTable infoTable = this.infoTableUtils.getInfoTable(dataShape);
    ValueCollection valueCollection = new ValueCollection();
    valueCollection.SetValue(this.dataShapeUtils.getPrimaryKeyField(dataShape), uid);
    infoTable.addRow(valueCollection);
    if (_logger.isDebugEnabled())
      _logger.debug("InfoTable for batch action:" + infoTable.toJSONSerialized()); 
    return infoTable;
  }
}
