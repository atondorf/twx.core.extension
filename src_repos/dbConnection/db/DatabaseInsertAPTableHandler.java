package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.types.collections.ValueCollection;
import java.sql.Connection;
import java.util.Optional;

public class DatabaseInsertAPTableHandler extends AbstractDatabaseTableHandler {
  private DatabaseInsertHDTableHandler insert;
  
  private DatabaseInsertHDTableHandler insertAp;
  
  DatabaseInsertAPTableHandler(DataShape dataShape, ValueCollection valueCollection, DatabaseHandler databaseHandler) {
    super(dataShape, databaseHandler);
    this.insert = new DatabaseInsertHDTableHandler(dataShape, valueCollection, databaseHandler);
    Optional<DataShape> dataShapeAP = getDataShapeAP();
    dataShapeAP.ifPresent(dataShape1 -> this.insertAp = new DatabaseInsertHDTableHandler(dataShape1, valueCollection, databaseHandler));
  }
  
  protected String createStatement(DataShape dataShape) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> found = this.insert.execute(connection);
    if (this.insertAp != null) {
      Optional<QueryResult> resultAp = this.insertAp.execute(connection);
      if (found.isPresent() && resultAp.isPresent())
        ((QueryResult)found.get()).merge(resultAp.get()); 
    } 
    if (found.isPresent() && ((QueryResult)found.get()).updateSuccessful()) {
      Optional<DataChange> foundDataChange = getDataChange();
      foundDataChange.ifPresent(dataChange -> ((QueryResult)found.get()).addDataChange(dataChange));
    } 
    return found;
  }
  
  public Optional<DataChange> getDataChange() {
    return this.insert.getDataChange();
  }
}
