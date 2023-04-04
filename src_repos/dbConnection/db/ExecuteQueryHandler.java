package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.types.InfoTable;

import org.json.JSONObject;

class ExecuteQueryHandler extends AbstractExecuteHandler<InfoTable> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteQueryHandler.class);
  
  private String dataShapeName;
  
  private JSONObject filter;
  
  private int offset = 0;
  
  private int limit = 0;
  
  ExecuteQueryHandler( DatabaseHandler databaseHandler, String dataShapeName, JSONObject filter, Integer offset, Integer limit) {
    super(databaseHandler);
    this.dataShapeName = dataShapeName;
    this.filter = filter;
    this.offset = (offset != null) ? offset.intValue() : 0;
    this.limit = (limit != null) ? limit.intValue() : 0;
  }
  
  public InfoTable execute() throws Exception {
    long start = 0L;
    if (_logger.isDebugEnabled()) {
      _logger.debug("Query:" + this.dataShapeName);
      _logger.debug("filter:" + this.filter);
      start = System.currentTimeMillis();
    } 
    this.dataShapeUtils.validateDataShapeName(this.dataShapeName);
    DataShape dataShape = this.dataShapeUtils.getDataShape(this.dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getQueryHandler(dataShape, this.filter, this.offset, this.limit);
    QueryResult queryResult = getDatabaseHandler().execute(databaseTableHandler);
    InfoTable infoTable = databaseTableHandler.buildInfoTable(queryResult);
    if (_logger.isDebugEnabled()) {
      long stop = System.currentTimeMillis();
      long time = stop - start;
      _logger.debug("Query Time:" + time);
    } 
    return infoTable;
  }
}
