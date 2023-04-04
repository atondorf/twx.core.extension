package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;

import org.json.JSONObject;

public class SqlQueryHandler extends AbstractExecuteHandler<String> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteQueryHandler.class);
  
  private String dataShapeName;
  
  private JSONObject filter;
  
  private int offset = 0;
  
  private int limit = 0;
  
  SqlQueryHandler( DatabaseHandler databaseHandler, String dataShapeName, JSONObject filter, Integer offset, Integer limit) {
    super(databaseHandler);
    this.dataShapeName = dataShapeName;
    this.filter = filter;
    this.offset = (offset != null) ? offset.intValue() : 0;
    this.limit = (limit != null) ? limit.intValue() : 0;
  }
  
  public String execute() {
    if (_logger.isDebugEnabled())
      _logger.debug("filter:" + this.filter); 
    this.dataShapeUtils.validateDataShapeName(this.dataShapeName);
    DataShape dataShape = this.dataShapeUtils.getDataShape(this.dataShapeName);
    DatabaseQueryTableHandler databaseQueryTableHandler = new DatabaseQueryTableHandler(dataShape, this.filter, getDatabaseHandler(), this.offset, this.limit);
    return databaseQueryTableHandler.getJdbcQuery().toString();
  }
}
