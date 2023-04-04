package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

class DatabaseDeleteHDTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseDeleteTableHandler databaseDeleteTableHandler;
  
  private List<DatabaseInsertTableHandler> historicalDataTableHandlers;
  
  DatabaseDeleteHDTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.databaseDeleteTableHandler = new DatabaseDeleteTableHandler(dataShape, valueCollection, databaseHandler);
    Validate.notNull(this.databaseDeleteTableHandler);
    DatabaseHistoricalDataHandlerFactory databaseHistoricalDataHandlerFactory = new DatabaseHistoricalDataHandlerFactory();
    this
      .historicalDataTableHandlers = databaseHistoricalDataHandlerFactory.getHistoricalDataTableHandlers(ActionType.DELETE, dataShape, valueCollection, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> found = this.databaseDeleteTableHandler.execute(connection);
    if (found.isPresent()) {
      QueryResult queryResult = found.get();
      if (queryResult.updateSuccessful())
        for (DatabaseInsertTableHandler insertTableHandler : this.historicalDataTableHandlers)
          insertTableHandler.execute(connection);  
    } 
    return found;
  }
  
  protected Object getPrimaryKeyValue() {
    return this.databaseDeleteTableHandler.getPrimaryKeyValue();
  }
  
  public Optional<DataChange> getDataChange() {
    return this.databaseDeleteTableHandler.getDataChange();
  }
}
