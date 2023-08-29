package twx.core.db2;

import java.sql.Connection;

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

import ch.qos.logback.core.db.dialect.DBUtil;
import twx.core.db2.handler.DbHandler;
import twx.core.db2.model.DbModel;
import twx.core.db2.model.DbObject;
import twx.core.db2.model.DbSchema;
import twx.core.db2.model.DbTable;
import twx.core.db2.util.TwxDataShapeUtil;
import twx.core.db2.util.TwxDbUtil;

public class DatabaseTS {
    private static Logger logger = LogUtilities.getInstance().getApplicationLogger(DatabaseTS.class);

    private DbModel getDBModel() throws Exception  {
        // return TwxDbUtil.getDBModel();
        return null;
    }

    // region TWX-Services Metadata Configuration ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetHandlerName", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetHandlerName() throws Exception {
        return TwxDbUtil.getHandler().getName();
    }

    @ThingworxServiceDefinition(name = "GetDBKey", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBKey() throws Exception {
        // return TwxDbUtil.getConfiguredKey();
        return TwxDbUtil.getHandler().getKey();
    }

    @ThingworxServiceDefinition(name = "GetDBCatalog", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBCatalog() throws Exception {
        // return TwxDbUtil.getConfiguredCatalog();
        return TwxDbUtil.getHandler().getCatalog();
    }

    @ThingworxServiceDefinition(name = "GetDBApplication", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBApplication() throws Exception {
        // return TwxDbUtil.getConfiguredApplication();
        return TwxDbUtil.getHandler().getApplication();        
    }

	@ThingworxServiceDefinition(name = "IsSqlThing", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean IsSqlThing() throws Exception {
        var abstractDatabase = TwxDbUtil.getHandler().getAbstractDatabase(); 
        return ( abstractDatabase instanceof com.thingworx.things.database.SQLThing );
    }

    @ThingworxServiceDefinition(name = "IsDatabaseThing", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean IsDatabaseThing() throws Exception {
        var abstractDatabase = TwxDbUtil.getHandler().getAbstractDatabase(); 
        return ( abstractDatabase instanceof com.thingworx.things.database.DatabaseSystem );
    }

    @ThingworxServiceDefinition(name = "GetSQLThingName", description = "", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetSQLThingName() throws Exception {
        // return TwxDbUtil.getConfiguredApplication();
        var abstractDatabase = TwxDbUtil.getHandler().getAbstractDatabase(); 
        if( abstractDatabase == null ) {
            return "UNDEFINED";
        }
        return abstractDatabase.getName();
    }
    
    // endregion
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetDBModel", description = "Get's the Model from the internal Model Cache ...", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBModel() throws Exception {
        return TwxDbUtil.getHandler().getModel().toJSON();
    }

    @ThingworxServiceDefinition(name = "QueryDBModel", description = "Queries the Model from Database, does not store it to the Model Tree", category = "Metadata Database", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject QueryDBModel() throws Exception {
        return TwxDbUtil.getHandler().queryModel().toJSON(); 
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
        
        var ds = TwxDataShapeUtil.getDataShape(dataShapeName);
        var modelMgr = TwxDbUtil.getHandler().getModelManager();
        DbTable table = modelMgr.getTableFromDataShape(ds);
        return table.toJSON();
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
