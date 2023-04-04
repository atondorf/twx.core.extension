package twx.core.db;


import org.apache.commons.lang3.Validate;

abstract class AbstractExecuteHandler<T> implements ExecuteHandler<T> {

  private DatabaseHandler databaseHandler;
  
  protected DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  AbstractExecuteHandler( DatabaseHandler databaseHandler) {
    Validate.notNull(databaseHandler);
    this.databaseHandler = databaseHandler;
  }
  
  protected DatabaseHandler getDatabaseHandler() {
    return this.databaseHandler;
  }
  
  protected DatabaseTableHandlerFactory getFactory() {
    return getDatabaseHandler().getDatabaseTableHandlerFactory();
  }
}
