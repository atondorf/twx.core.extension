package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.sql.Connection;
import java.util.Optional;



class AddColumnTableHandler extends AbstractDatabaseColumnTableHandler {
  private DatabaseAddColumnTableHandler databaseAddColumnTableHandler;
  
  private AddUniqueConstraintTableHandler addUniqueConstraintTableHandler;
  
  AddColumnTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.databaseAddColumnTableHandler = new DatabaseAddColumnTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler);
    if (fieldDatabaseInfo != null && fieldDatabaseInfo.isUnique())
      this.addUniqueConstraintTableHandler = new AddUniqueConstraintTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler); 
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> queryResult = this.databaseAddColumnTableHandler.execute(connection);
    if (this.addUniqueConstraintTableHandler != null)
      this.addUniqueConstraintTableHandler.execute(connection); 
    return queryResult;
  }
}
