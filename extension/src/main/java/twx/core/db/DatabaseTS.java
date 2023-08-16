package twx.core.db;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

import twx.core.db.imp.DBUtil;
import twx.core.db.model.DBModelManager;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbSchema;
import twx.core.imp.DataShapeUtil;

public class DatabaseTS {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseTS.class);

    private IDatabaseHandler getDatabaseHandler() throws Exception {
        return DBUtil.getDatabaseHandler(); 
    }

    private DbModel getDBModel() throws Exception  {
        return DBUtil.getDBModel();
    }

    // region TWX-Services Metadata Configuration ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetDBKey", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBKey() throws Exception {
        return DBUtil.getConfiguredKey();
    }

    @ThingworxServiceDefinition(name = "GetDBCatalog", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBCatalog() throws Exception {
        return DBUtil.getConfiguredCatalog();
    }

    @ThingworxServiceDefinition(name = "GetDBApplication", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBApplication() throws Exception {
        return DBUtil.getConfiguredApplication();
    }
    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "QueryDBModel", description = "Queries the Model from Database, does not store it to the Model Tree", category = "Metadata Database", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject QueryDBModel() throws Exception {
        DbModel model = getDatabaseHandler().queryModel();
        return model.toJSON();
    }

	@ThingworxServiceDefinition(name = "GetModelApplications", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {"isEntityDataShape:true", "dataShape:GenericStringList" })
	public InfoTable GetModelApplications() throws Exception {
        DataShape ds = DataShapeUtil.getDataShape("GenericStringList");
        InfoTable result = ds.CreateValues();
        DBModelManager.getModelNames().forEach(name -> { 
            ValueCollection row = new ValueCollection();
            row.put("item", new StringPrimitive(name));
            result.addRow(row);
        });
		return result;
	}

    @ThingworxServiceDefinition(name = "GetDBModel", description = "Get's the Model from the internal Model Cache ...", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBModel() throws Exception {
        DbModel model = DBUtil.getDBModel();
        return  model.toJSON();
    }

    @ThingworxServiceDefinition(name = "GetModelSchemas", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetModelSchemas() throws Exception {
        DbModel     model   = DBUtil.getDBModel();
        JSONObject  obj     = new JSONObject();
        JSONArray   arr     = new JSONArray();
        for (String name : model.getSchemaNames() ) {
                System.out.println(name);
                arr.put(name);
        }
        obj.put("schemas", arr);
        obj.put("name", model.getName() );
        return obj;
    }
    
    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "CreateSchema", description = "", category = "Schema Handling", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void CreateSchema( @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) throws Exception {
        DbModel model = DBUtil.getDBModel();
        if( model.hasSchema(name) )
            return;
        model.addSchema(name);
        // getDatabaseHandler().createSchema();

    }

    @ThingworxServiceDefinition(name = "DropSchema", description = "", category = "Schema Handling", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void DropSchema( @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) {
		_logger.trace("Entering Service: CreateSchema");
		_logger.trace("Exiting Service: CreateSchema");
	}

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetTableNames", description = "", category = "Table Handling", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
	public JSONObject GetTableNames(@ThingworxServiceParameter(name = "SchemaName", description = "", baseType = "STRING") String SchemaName) throws Exception {
        DbModel     model   = DBUtil.getDBModel();
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
	}
    
    @ThingworxServiceDefinition(name = "CreateTable", description = "Create database table using the datashape", category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void CreateTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        _logger.debug("CreateTable:" + dataShapeName);
    }

    @ThingworxServiceDefinition(name = "DropTable", description = "Create database table using the datashape",  category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void DropTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        _logger.debug("CreateTable:" + dataShapeName);
    }
    
    @ThingworxServiceDefinition(name = "PurgeTable", description = "Create database table using the datashape", category = "Table Handling", isAllowOverride = false)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void PurgeTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        _logger.debug("CreateTable:" + dataShapeName);
    }

    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------

    // endregion

}
