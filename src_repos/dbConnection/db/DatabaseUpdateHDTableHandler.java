package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

class DatabaseUpdateHDTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseUpdateTableHandler databaseUpdateTableHandler;
  
  private List<DatabaseInsertTableHandler> historicalDataTableHandlers;
  
  DatabaseUpdateHDTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.databaseUpdateTableHandler = new DatabaseUpdateTableHandler(dataShape, valueCollection, databaseHandler);
    Validate.notNull(this.databaseUpdateTableHandler);
    DatabaseHistoricalDataHandlerFactory databaseHistoricalDataHandlerFactory = new DatabaseHistoricalDataHandlerFactory();
    this
      .historicalDataTableHandlers = databaseHistoricalDataHandlerFactory.getHistoricalDataTableHandlers(ActionType.UPDATE, dataShape, valueCollection, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> found = this.databaseUpdateTableHandler.execute(connection);
    if (found.isPresent()) {
      QueryResult queryResult = found.get();
      if (queryResult.updateSuccessful())
        for (DatabaseInsertTableHandler insertTableHandler : this.historicalDataTableHandlers)
          insertTableHandler.execute(connection);  
    } 
    return found;
  }
  
  protected ValueCollection getValueCollection() {
    return this.databaseUpdateTableHandler.getValueCollection();
  }
  
  protected Object getPrimaryKeyValue() {
    return this.databaseUpdateTableHandler.getPrimaryKeyValue();
  }
  
  public Optional<DataChange> getDataChange() {
    return this.databaseUpdateTableHandler.getDataChange();
  }
}
