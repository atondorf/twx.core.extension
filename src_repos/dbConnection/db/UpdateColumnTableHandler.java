package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import java.sql.Connection;
import java.util.Optional;



class UpdateColumnTableHandler extends AbstractDatabaseColumnTableHandler {
  protected SetNotNullColumnTableHandler setNotNullColumnTableHandler;
  
  protected SetColumnLengthTableHandler setColumnLengthTableHandler;
  
  private AddUniqueConstraintTableHandler addUniqueConstraintTableHandler;
  
  private AddDefaultConstraintTableHandler addDefaultConstraintTableHandler;
  
  UpdateColumnTableHandler( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, databaseHandler);
    this.setNotNullColumnTableHandler = new SetNotNullColumnTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler);
    this.setColumnLengthTableHandler = new SetColumnLengthTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler);
    if (fieldDatabaseInfo != null && fieldDatabaseInfo.isUnique())
      this.addUniqueConstraintTableHandler = new AddUniqueConstraintTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler); 
    if (fieldDatabaseInfo != null && fieldDatabaseInfo.getDefaultValue() != null)
      this.addDefaultConstraintTableHandler = new AddDefaultConstraintTableHandler(dataShape, fieldName, fieldDatabaseInfo, databaseHandler); 
  }
  
  protected String createStatement(DataShape dataShape, FieldDefinition fieldDefinition) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    Optional<QueryResult> queryResult = this.setNotNullColumnTableHandler.execute(connection);
    if (this.setColumnLengthTableHandler != null)
      this.setColumnLengthTableHandler.execute(connection); 
    if (this.addUniqueConstraintTableHandler != null)
      this.addUniqueConstraintTableHandler.execute(connection); 
    if (this.addDefaultConstraintTableHandler != null)
      this.addDefaultConstraintTableHandler.execute(connection); 
    return queryResult;
  }
}
