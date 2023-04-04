package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

class DatabaseInsertHDTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseInsertTableHandler databaseInsertTableHandler;
  
  private List<DatabaseInsertTableHandler> historicalDataTableHandlers;
  
  DatabaseInsertHDTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.databaseInsertTableHandler = new DatabaseInsertTableHandler(dataShape, valueCollection, databaseHandler);
    Validate.notNull(this.databaseInsertTableHandler);
    DatabaseHistoricalDataHandlerFactory databaseHistoricalDataHandlerFactory = new DatabaseHistoricalDataHandlerFactory();
    this
      .historicalDataTableHandlers = databaseHistoricalDataHandlerFactory.getHistoricalDataTableHandlers(ActionType.CREATE, dataShape, valueCollection, databaseHandler);
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> found = this.databaseInsertTableHandler.execute(connection);
    if (found.isPresent()) {
      QueryResult queryResult = found.get();
      if (queryResult.updateSuccessful()) {
        Object primaryKey = getValueCollection().getValue(getPrimaryKey(getDataShape()).getName());
        FieldDefinition fieldDefinition = DatabaseHistoricalDataHandlerFactory.getHistoricalDataShape().getFieldDefinition("ReferenceKey");
        for (DatabaseInsertTableHandler insertTableHandler : this.historicalDataTableHandlers) {
          if (primaryKey != null)
            insertTableHandler.getValueCollection().SetValue(fieldDefinition, primaryKey); 
          insertTableHandler.execute(connection);
        } 
      } 
    } 
    return found;
  }
  
  protected Object getPrimaryKeyValue() {
    return this.databaseInsertTableHandler.getPrimaryKeyValue();
  }
  
  protected ValueCollection getValueCollection() {
    return this.databaseInsertTableHandler.getValueCollection();
  }
  
  public Optional<DataChange> getDataChange() {
    return this.databaseInsertTableHandler.getDataChange();
  }
}
