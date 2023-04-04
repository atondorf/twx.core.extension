package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.persistence.provider.PersistenceProvider;
import com.thingworx.persistence.provider.PersistenceProviderManager;
import com.thingworx.types.InfoTable;
import com.thingworx.types.NamedObject;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

public class DatabaseService {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseService.class);

  private DataShapeUtils dataShapeUtils = new DataShapeUtils();

  private JsonUtility jsonUtility = new JsonUtility();

  @ThingworxServiceDefinition(name = "CreateTable", description = "Create database table using the datashape", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void CreateTable(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {"isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = {"isRequired:false" }) JSONObject dbInfo)
      throws Exception 
  {
    _logger.debug("CreateTable:" + dataShapeName);
  
    DataShapeDatabaseInfo dataShapeDatabaseInfo = this.jsonUtility.<DataShapeDatabaseInfo>getObjetFromJson(dbInfo, DataShapeDatabaseInfo.class);
    DataShape             dataShape             = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler  databaseTableHandler  = getFactory().getCreateTableHandler(dataShape, dataShapeDatabaseInfo);
  
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "CreateTables", description = "Create database tables using the dbInfo", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable CreateTables(
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of tables to create", baseType = "JSON", aspects = {
          "isRequired:true" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("CreateTables:" + dbInfo);
    long start = System.currentTimeMillis();
    DatabaseInfo databaseInfo = this.jsonUtility.<DatabaseInfo>getObjetFromJson(dbInfo, DatabaseInfo.class);
    ExecuteHandler<InfoTable> createTables = new CreateDatabaseTables(getDatabaseHandler(), databaseInfo);
    InfoTable result = createTables.execute();
    long stop = System.currentTimeMillis();
    long time = stop - start;
    _logger.debug("CreateTables Time:" + time);
    return result;
  }

  @ThingworxServiceDefinition(name = "UpdateDatabaseSchema", description = "Synchronized database with data shape using the dbInfo", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE", aspects = {
      "dataShape:PTC.DBConnection.DatabaseSchemaValidation" })
  public InfoTable UpdateDatabaseSchema(
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of tables to synchronized", baseType = "JSON", aspects = {
          "isRequired:true" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("UpdateDatabaseSchema:" + dbInfo);
    long start = System.currentTimeMillis();
    Validate.notNull(dbInfo);
    DatabaseInfo databaseInfo = this.jsonUtility.<DatabaseInfo>getObjetFromJson(dbInfo, DatabaseInfo.class);
    ExecuteHandler<InfoTable> updateDatabaseSchema = new UpdateDatabaseSchema(getDatabaseHandler(), databaseInfo);
    InfoTable result = updateDatabaseSchema.execute();
    long stop = System.currentTimeMillis();
    long time = stop - start;
    _logger.debug("UpdateDatabaseSchema Time:" + time);
    return result;
  }

  @ThingworxServiceDefinition(name = "DropTable", description = "Drop table in database", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void DropTable(
      @ThingworxServiceParameter(name = "dataShapeName", description = "***DEPRECATED-J*** WARNING This service drops database table.Use it at your own risk", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName)
      throws Exception {
    _logger.debug("DropTable:" + dataShapeName);
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getDropTableHandler(dataShape);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "DropTables", description = "Drop database tables using the dbInfo", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void DropTables(
      @ThingworxServiceParameter(name = "dbInfo", description = "***DEPRECATED-J*** WARNING This service drops database tables.Use it at your own risk | The json description of tables to drop", baseType = "JSON", aspects = {
          "isRequired:true" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("DropTables:" + dbInfo);
    DatabaseInfo databaseInfo = this.jsonUtility.<DatabaseInfo>getObjetFromJson(dbInfo, DatabaseInfo.class);
    ExecuteHandler<Boolean> dropTables = new DropDatabaseTables(getDatabaseHandler(), databaseInfo);
    dropTables.execute();
  }

  @ThingworxServiceDefinition(name = "AddColumn", description = "Add column in database table", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void AddColumn(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the column to add", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("AddColumn:" + fieldName + ":" + dataShapeName);
    FieldDatabaseInfo fieldDatabaseInfo = this.jsonUtility.<FieldDatabaseInfo>getObjetFromJson(dbInfo,
        FieldDatabaseInfo.class);
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getAddColumnHandler(dataShape, fieldName,
        fieldDatabaseInfo);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "UpdateColumn", description = "Update column in database table", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void UpdateColumn(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the column to update", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("UpdateColumn:" + fieldName + ":" + dataShapeName);
    FieldDatabaseInfo fieldDatabaseInfo = this.jsonUtility.<FieldDatabaseInfo>getObjetFromJson(dbInfo,
        FieldDatabaseInfo.class);
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getUpdateColumnHandler(dataShape, fieldName,
        fieldDatabaseInfo);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "RemoveColumn", description = "Remove column in database table", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void RemoveColumn(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName)
      throws Exception {
    _logger.debug("RemoveColumn:" + fieldName + ":" + dataShapeName);
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getRemoveColumnHandler(dataShape, fieldName);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "CreateIndex", description = "Create index on a column in database table", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void CreateIndex(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "unique", description = "***DEPRECATED-K*** Use dbInfo paramter to pass in value to service.", baseType = "BOOLEAN", aspects = {
          "defaultValue:false" }) Boolean unique,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the index to add", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("CreateIndex:" + fieldName + ":" + dataShapeName);
    Index index = this.jsonUtility.<Index>getObjetFromJson(dbInfo, Index.class);
    if (index == null) {
      index = new Index();
      index.setName(fieldName);
    }
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getCreateIndexHandler(dataShape, index);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "RemoveIndex", description = "Remove index off a column in database table", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void RemoveIndex(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the index to removed", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("RemoveIndex:" + fieldName + ":" + dataShapeName);
    Index index = this.jsonUtility.<Index>getObjetFromJson(dbInfo, Index.class);
    if (index == null) {
      index = new Index();
      index.setName(fieldName);
    }
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getRemoveIndexHandler(dataShape, index);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "AddForeignKey", description = "Add foreign key in database", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void AddForeignKey(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "referenceDataShapeName", description = "***DEPRECATED-K*** Use dbInfo paramter to pass in value to service.", baseType = "DATASHAPENAME", aspects = {
          "isRequired:false" }) String referenceDataShapeName,
      @ThingworxServiceParameter(name = "referenceFieldName", description = "***DEPRECATED-K*** Use dbInfo paramter to pass in value to service.", baseType = "STRING", aspects = {
          "isRequired:false" }) String referenceFieldName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the foreign key to add", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("AddForeignKey:" + fieldName + ":" + dataShapeName);
    ForeignKey foreignKey = this.jsonUtility.<ForeignKey>getObjetFromJson(dbInfo, ForeignKey.class);
    if (foreignKey == null) {
      foreignKey = new ForeignKey();
      foreignKey.setName(fieldName);
      foreignKey.setReferenceDataShapeName(referenceDataShapeName);
      foreignKey.setReferenceFieldName(referenceFieldName);
    }
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getAddForeignKeyHandler(dataShape, fieldName, foreignKey);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "RemoveForeignKey", description = "Remove foreign key in database", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void RemoveForeignKey(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "fieldName", description = "The field name", baseType = "STRING", aspects = {
          "isRequired:true" }) String fieldName,
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the foreign key to removed", baseType = "JSON", aspects = {
          "isRequired:false" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("RemoveForeignKey:" + fieldName + ":" + dataShapeName);
    ForeignKey foreignKey = this.jsonUtility.<ForeignKey>getObjetFromJson(dbInfo, ForeignKey.class);
    if (foreignKey == null) {
      foreignKey = new ForeignKey();
      foreignKey.setName(fieldName);
    }
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DatabaseTableHandler databaseTableHandler = getFactory().getRemoveForeignKeyHandler(dataShape, fieldName,
        foreignKey);
    getDatabaseHandler().execute(databaseTableHandler);
  }

  @ThingworxServiceDefinition(name = "Insert", description = "Insert datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable Insert(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {"isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "infoTable", description = "The data in an info table", baseType = "INFOTABLE", aspects = {"isRequired:true" }) InfoTable infoTable)
      throws Exception 
  {
    ExecuteHandler<InfoTable> executeActionHandler = new ExecuteActionHandler( getDatabaseHandler(), ActionType.CREATE, dataShapeName, infoTable);
    return executeActionHandler.execute();
  }

  @ThingworxServiceDefinition(name = "Update", description = "Update datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable Update(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {"isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "infoTable", description = "The data in an info table", baseType = "INFOTABLE", aspects = {"isRequired:true" }) InfoTable infoTable)
      throws Exception 
  {
    ExecuteHandler<InfoTable> executeActionHandler = new ExecuteActionHandler(getDatabaseHandler(), ActionType.UPDATE, dataShapeName, infoTable);
    return executeActionHandler.execute();
  }

  @ThingworxServiceDefinition(name = "Remove", description = "Remove datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable Remove(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {"isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "infoTable", description = "The data in an info table", baseType = "INFOTABLE", aspects = {"isRequired:true" }) InfoTable infoTable)
      throws Exception 
  {
    ExecuteHandler<InfoTable> executeActionHandler = new ExecuteActionHandler(getDatabaseHandler(), ActionType.DELETE, dataShapeName, infoTable);
    return executeActionHandler.execute();
  }

  @ThingworxServiceDefinition(name = "Delete", description = "Delete datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable Delete(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {"isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "UID", description = "The data shape id", baseType = "STRING", aspects = {"isRequired:true" }) String uid)
      throws Exception 
  {
    ExecuteHandler<InfoTable> executeDeleteHandler = new ExecuteDeleteHandler(getDatabaseHandler(), dataShapeName, uid);
    return executeDeleteHandler.execute();
  }

  @ThingworxServiceDefinition(name = "BatchDelete", description = "Batch Delete datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable BatchDelete(
      @ThingworxServiceParameter(name = "infoTable", description = "The data in an info table", baseType = "INFOTABLE", aspects = {
          "isRequired:true", "dataShape:PTC.DBConnection.BatchDelete" }) InfoTable infoTable)
      throws Exception {
    if (_logger.isDebugEnabled())
      _logger.debug("Batch Delete:" + infoTable.getRows().size());
    DatabaseHandler databaseHandler = getDatabaseHandler();
    ExecuteHandler<InfoTable> executeBatchDeleteHandler = new ExecuteBatchDeleteHandler(databaseHandler, infoTable);
    return databaseHandler.<InfoTable>executeHandler(executeBatchDeleteHandler);
  }

  @ThingworxServiceDefinition(name = "BatchAction", description = "Batch Action datas", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable BatchAction(
      @ThingworxServiceParameter(name = "infoTable", description = "The data in an info table", baseType = "INFOTABLE", aspects = {
          "isRequired:true", "dataShape:PTC.DBConnection.BatchAction" }) InfoTable infoTable)
      throws Exception {
    if (_logger.isDebugEnabled())
      _logger.debug("Batch Action:" + infoTable.getRows().size());
    DatabaseHandler databaseHandler = getDatabaseHandler();
    ExecuteHandler<InfoTable> executeBatchActionHandler = new ExecuteBatchActionHandler(databaseHandler, infoTable);
    return databaseHandler.<InfoTable>executeHandler(executeBatchActionHandler);
  }

  @ThingworxServiceDefinition(name = "Query", description = "Query data for data shape", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable Query(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "filter", description = "The filter", baseType = "JSON") JSONObject filter,
      @ThingworxServiceParameter(name = "offset", description = "The offset", baseType = "INTEGER") Integer offset,
      @ThingworxServiceParameter(name = "limit", description = "The limit", baseType = "INTEGER") Integer limit)
      throws Exception {
    ExecuteHandler<InfoTable> executeQueryHandler = new ExecuteQueryHandler(getDatabaseHandler(), dataShapeName, filter,
        offset, limit);
    return executeQueryHandler.execute();
  }

  @ThingworxServiceDefinition(name = "ExecuteService", description = "Execute a service on the thing that will be executed in a transaction.", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable ExecuteService(
      @ThingworxServiceParameter(name = "thingName", description = "The thing name", baseType = "THINGNAME", aspects = {
          "isRequired:true" }) String thingName,
      @ThingworxServiceParameter(name = "serviceName", description = "The service name", baseType = "STRING", aspects = {
          "isRequired:true" }) String serviceName,
      @ThingworxServiceParameter(name = "params", description = "The json parameters", baseType = "JSON", aspects = {
          "isRequired:true" }) JSONObject params)
      throws Exception {
    ExecuteHandler<InfoTable> executeServiceHandler = new ExecuteServiceHandler(thingName, serviceName, params);
    return getDatabaseHandler().<InfoTable>executeHandler(executeServiceHandler);
  }

  @ThingworxServiceDefinition(name = "GetDatashapeNameAp", description = "Return the additional properties data shape name", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "DATASHAPENAME")
  public String GetDatashapeNameAp(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName)
      throws Exception {
    Optional<DataShape> foundAp = this.dataShapeUtils.getDataShapeAp(dataShapeName);
    return foundAp.<String>map(NamedObject::getName).orElse(null);
  }

  @ThingworxServiceDefinition(name = "GetDataShapeName", description = "Return data shape name", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "DATASHAPENAME")
  public String GetDataShapeName(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName)
      throws Exception {
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    DataShape primaryDataShape = this.dataShapeUtils.getPrimaryDataShape(dataShape);
    return primaryDataShape.getName();
  }

  @ThingworxServiceDefinition(name = "TransactionStart", description = "Start a transaction on the database thing for this thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void TransactionStart() throws Exception {
    getDatabaseHandler().startTransaction();
  }

  @ThingworxServiceDefinition(name = "TransactionCommit", description = "Commit a transaction on the database thing for this thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void TransactionCommit() throws Exception {
    getDatabaseHandler().commitTransaction();
  }

  @ThingworxServiceDefinition(name = "TransactionRollback", description = "Rollback a transaction on the database thing for this thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void TransactionRollback() throws Exception {
    getDatabaseHandler().rollbackTransaction();
  }

  @ThingworxServiceDefinition(name = "TransactionEnd", description = "Close a transaction on the database thing for this thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void TransactionEnd() throws Exception {
    getDatabaseHandler().closeTransaction();
  }

  @ThingworxServiceDefinition(name = "StartTransactionLookup", description = "Start scheduler that will close all transaction for terminated thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void StartTransactionLookup() throws Exception {
    getDatabaseHandler().startTransactionLookUp();
  }

  @ThingworxServiceDefinition(name = "StopTransactionLookup", description = "Start scheduler that will close all transaction for terminated thread.", isAllowOverride = false, isPrivate = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void StopTransactionLookup() throws Exception {
    getDatabaseHandler().stopTransactionLookup();
  }

  @ThingworxServiceDefinition(name = "ValidateDatabaseSchema", description = "Validate existing foreign keys in database with the dbInfo", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "INFOTABLE")
  public InfoTable ValidateDatabaseSchema(
      @ThingworxServiceParameter(name = "dbInfo", description = "The json description of tables to synchronized", baseType = "JSON", aspects = {
          "isRequired:true" }) JSONObject dbInfo)
      throws Exception {
    _logger.debug("ValidateDatabaseSchema:" + dbInfo);
    DatabaseInfo databaseInfo = this.jsonUtility.<DatabaseInfo>getObjetFromJson(dbInfo, DatabaseInfo.class);
    DatabaseMetaDataManager databaseMetaDataManager = getDatabaseHandler().getDatabaseMetaDataManager();
    ValidateDatabaseSchema validateDatabaseSchema = new ValidateDatabaseSchema();
    return validateDatabaseSchema.databaseSchemaValidation(databaseInfo, databaseMetaDataManager,
        getDatabaseHandler());
  }

  @ThingworxServiceDefinition(name = "GetSqlQuery", description = "Return a SQL Query for a data shape and a filter", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "STRING")
  public String GetSqlQuery(
      @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = {
          "isRequired:true" }) String dataShapeName,
      @ThingworxServiceParameter(name = "filter", description = "The filter", baseType = "JSON") JSONObject filter,
      @ThingworxServiceParameter(name = "offset", description = "The offset", baseType = "INTEGER") Integer offset,
      @ThingworxServiceParameter(name = "limit", description = "The limit", baseType = "INTEGER") Integer limit)
      throws Exception {
    ExecuteHandler<String> executeQueryHandler = new SqlQueryHandler(getDatabaseHandler(), dataShapeName, filter,
        offset, limit);
    return executeQueryHandler.execute();
  }

  @ThingworxServiceDefinition(name = "ClearCache", description = "Clear all internal cache.", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void ClearCache() throws Exception {
    _logger.debug("Clear cache");
    DataShapeUtils.clearCache();
    DatabaseHandlerLocator.clearCache();
  }

  @ThingworxServiceDefinition(name = "EnablePersistenceProvider", description = "enable (activate) given persistence provider", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
  public void EnablePersistenceProvider(
      @ThingworxServiceParameter(name = "persistenceProviderName", description = "The name of the persistence provider", baseType = "STRING", aspects = {
          "isRequired:true" }) String persistenceProviderName)
      throws Exception {
    PersistenceProviderManager manager = PersistenceProviderManager.getInstance();
    PersistenceProvider persistenceProvider = manager.getEntityDirect(persistenceProviderName);
    if (persistenceProvider == null) {
      _logger.error("PersistenceProvider:" + persistenceProviderName + " does not exist");
      throw new ThingworxRuntimeException("PersistenceProvider: " + persistenceProviderName + " does not exist");
    }
    if (!persistenceProvider.isVisible()) {
      _logger.error("Current user does not have access to PersistenceProvider: " + persistenceProviderName);
      throw new ThingworxRuntimeException(
          "Current user does not have access to PersistenceProvider: " + persistenceProviderName);
    }
    if (!persistenceProvider.getEnabled()) {
      persistenceProvider.setEnabled(true);
      manager.getModelProvider().toStorage(persistenceProvider);
    }
  }

  private DatabaseTableHandlerFactory getFactory() throws Exception {
    return getDatabaseHandler().getDatabaseTableHandlerFactory();
  }

  private DatabaseHandler getDatabaseHandler() throws Exception {
    return DatabaseUtility.getDatabaseHandler();
  }
}
