package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.Optional;

public class DatabaseUpdateAPTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseUpdateHDTableHandler update;
  
  private DatabaseUpdateHDTableHandler updateAp;
  
  DatabaseUpdateAPTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.update = new DatabaseUpdateHDTableHandler(dataShape, valueCollection, databaseHandler);
    Optional<DataShape> dataShapeAP = getDataShapeAP();
    dataShapeAP.ifPresent(dataShape1 -> this.updateAp = new DatabaseUpdateHDTableHandler(dataShape1, valueCollection, databaseHandler));
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> found = this.update.execute(connection);
    if (this.updateAp != null) {
      Optional<QueryResult> resultAp = this.updateAp.execute(connection);
      if (resultAp.isPresent())
        if (found.isPresent()) {
          ((QueryResult)found.get()).merge(resultAp.get());
        } else {
          return resultAp;
        }  
    } 
    if (found.isPresent() && ((QueryResult)found.get()).updateSuccessful()) {
      Optional<DataChange> foundDataChange = getDataChange();
      foundDataChange.ifPresent(dataChange -> ((QueryResult)found.get()).addDataChange(dataChange));
    } 
    return found;
  }
  
  public Optional<DataChange> getDataChange() {
    return this.update.getDataChange();
  }
}
