package twx.core.db;

import com.thingworx.datashape.DataShape;



class UpdateColumnTableHandlerSqlServer extends UpdateColumnTableHandler {
  UpdateColumnTableHandlerSqlServer( DataShape dataShape,  String fieldName,  FieldDatabaseInfo fieldDatabaseInfo,  DatabaseHandler databaseHandler) {
    super(dataShape, fieldName, fieldDatabaseInfo, databaseHandler);
    this.setNotNullColumnTableHandler = new SetNotNullColumnTableHandlerSqlServer(dataShape, fieldName, fieldDatabaseInfo, databaseHandler);
    this.setColumnLengthTableHandler = null;
  }
}
