package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.Optional;

public class DatabaseDeleteAPTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseDeleteHDTableHandler delete;
  
  private DatabaseDeleteTableHandler deleteAP;
  
  DatabaseDeleteAPTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.delete = new DatabaseDeleteHDTableHandler(dataShape, valueCollection, databaseHandler);
    Optional<DataShape> dataShapeAP = getDataShapeAP();
    dataShapeAP.ifPresent(dataShape1 -> this.deleteAP = new DatabaseDeleteTableHandler(dataShape1, valueCollection, databaseHandler));
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    if (this.deleteAP != null)
      this.deleteAP.execute(connection); 
    Optional<QueryResult> found = this.delete.execute(connection);
    if (found.isPresent() && ((QueryResult)found.get()).updateSuccessful()) {
      Optional<DataChange> foundDataChange = getDataChange();
      foundDataChange.ifPresent(dataChange -> ((QueryResult)found.get()).addDataChange(dataChange));
    } 
    return found;
  }
  
  public Optional<DataChange> getDataChange() {
    return this.delete.getDataChange();
  }
}
