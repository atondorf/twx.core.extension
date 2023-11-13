package twx.core.db;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;

import twx.core.db.liquibase.LiquibaseRunner;
import twx.core.db.util.DatabaseUtil;

public class DatabaseTS {
    private static Logger logger = LogUtilities.getInstance().getDatabaseLogger(DatabaseTS.class);

    // region TWX-Services DDL using Liquibase ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetLiquibaseConfig", description = "", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {} )
    public JSONObject GetLiquibaseConfig() throws Exception {
        var lbRunner = new LiquibaseRunner(DatabaseUtil.getAbstractDatabase());
        return lbRunner.getChangelog();
    }
    
    @ThingworxServiceDefinition(name = "Update", description = "", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void Update(
        @ThingworxServiceParameter(name = "Contexts", description = "", baseType = "STRING", aspects = {"isRequired:false" }) String contexts,
        @ThingworxServiceParameter(name = "Params", description = "", baseType = "JSON", aspects = {"isRequired:false" }) String jsonParam) throws Exception {
        logger.info("----- Liquibase update -----");            
        var lbRunner = new LiquibaseRunner(DatabaseUtil.getAbstractDatabase());
        lbRunner.update();
    }

    @ThingworxServiceDefinition(name = "UpdateSQL", description = "", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String UpdateSQL(
        @ThingworxServiceParameter(name = "Contexts", description = "", baseType = "STRING", aspects = {"isRequired:false" }) String contexts,
        @ThingworxServiceParameter(name = "Params", description = "", baseType = "JSON", aspects = {"isRequired:false" }) String jsonParam) throws Exception {
        logger.info("----- Liquibase updateSQL -----");
        var lbRunner = new LiquibaseRunner(DatabaseUtil.getAbstractDatabase());
        String sql = lbRunner.updateSQL();
        logger.info(sql);
        return sql;
    }

    // endregion
    // region TWX-Services Metadata Configuration ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetDBName", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBName() throws Exception {
        return DatabaseUtil.getHandler().getName();
    }

    @ThingworxServiceDefinition(name = "GetDBKey", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBKey() throws Exception {
        return DatabaseUtil.getHandler().getKey();
    }

    @ThingworxServiceDefinition(name = "GetDBCatalog", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBCatalog() throws Exception {
        return DatabaseUtil.getHandler().getDefaultCatalog();
    }

    @ThingworxServiceDefinition(name = "GetDBDefaultSchema", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBDefaultSchema() throws Exception {
        return DatabaseUtil.getHandler().getDefaultSchema();
    }

    @ThingworxServiceDefinition(name = "GetConfiguredKey", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetConfiguredKey() throws Exception {
        return DatabaseUtil.getConfiguredKey();
    }

    @ThingworxServiceDefinition(name = "GetConfigureCatalog", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetConfigureCatalog() throws Exception {
        return DatabaseUtil.getConfiguredCatalog();
    }

    @ThingworxServiceDefinition(name = "GetConfiguredApplication", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetConfiguredApplication() throws Exception {
        return DatabaseUtil.getConfiguredApplication();
    }

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetDBModel", description = "Get's the Model from the internal Model Cache ...", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBModel() throws Exception {
        return DatabaseUtil.getHandler().getDbModel().toJSON();
    }

    @ThingworxServiceDefinition(name = "QueryDBModel", description = "Queries the Model from Database, does not store it to the Model Tree", category = "Metadata Database", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject QueryDBModel() throws Exception {
        return DatabaseUtil.getHandler().getDDLReader().queryModel().toJSON();
    }
    
    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "CreateSchema", description = "", category = "Schema Handling", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void CreateSchema( @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) throws Exception {

    }

    @ThingworxServiceDefinition(name = "DropSchema", description = "", category = "Schema Handling", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void DropSchema( @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) {

	}

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
	@ThingworxServiceDefinition(name = "CreateTableModel", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject CreateTableModel(
            @ThingworxServiceParameter(name = "dataShapeName", description = "", baseType = "DATASHAPENAME") String dataShapeName,
            @ThingworxServiceParameter(name = "dnInfo", description = "", baseType = "JSON") JSONObject dnInfo) throws Exception {
  /*
        var ds = TwxDataShapeUtil.getDataShape(dataShapeName);
        var modelMgr = TwxDbUtil.getHandler().getModelManager();
        DbTable table = modelMgr.getTableFromDataShape(ds);
        return table.toJSON();
*/
        return null; // 
    }


    @ThingworxServiceDefinition(name = "GetTableNames", description = "", category = "Table Handling", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
	public JSONObject GetTableNames(@ThingworxServiceParameter(name = "SchemaName", description = "", baseType = "STRING") String SchemaName) throws Exception {
/*
        DbModel     model   = TwxDbUtil.getDBModel();
        DbSchema    schema  = model.getSchema(SchemaName);
        //!TODO ...
        JSONObject  obj     = new JSONObject();
        JSONArray   arr     = new JSONArray();
        for (String name : model.getSchemaNames() ) {
                System.out.println(name);
                arr.put(name);
        }
        obj.put("schemas", arr);
        obj.put("name", model.getName() );
        return obj;
*/
        return null;        
	}
    
    @ThingworxServiceDefinition(name = "CreateTable", description = "Create database table using the datashape", category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void CreateTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        logger.debug("CreateTable:" + dataShapeName);
    }

    @ThingworxServiceDefinition(name = "DropTable", description = "Create database table using the datashape",  category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void DropTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        logger.debug("CreateTable:" + dataShapeName);
    }
    
    @ThingworxServiceDefinition(name = "PurgeTable", description = "Create database table using the datashape", category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void PurgeTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        logger.debug("CreateTable:" + dataShapeName);
    }

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------

    // endregion

}
