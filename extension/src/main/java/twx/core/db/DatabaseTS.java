package twx.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.logging.LogUtilities;

import twx.core.db.imp.DBUtil;
import twx.core.db.imp.MsSQLDatabaseHandler;
import twx.core.db.model.DBModelManager;
import twx.core.db.model.DbModel;

public class DatabaseTS {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseTS.class);

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
    @ThingworxServiceDefinition(name = "QueryDbModel", description = "Queries the Model from Database Metadata", category = "Metadata Database", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject QueryDbModel() throws Exception {
        Connection conn = DBUtil.getConnection();
        String catalog = conn.getCatalog();
        DbModel model = DBModelManager.getDBModelCached(catalog);
        return model.toJSON();
    }

    @ThingworxServiceDefinition(name = "GetDBSchemas", description = "", category = "Metadata Cached", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBSchemas() throws Exception {

        Connection conn = DBUtil.getConnection();
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        ResultSet rs = conn.getMetaData().getSchemas();
        while (rs.next()) {
            String schemaName = rs.getString("TABLE_SCHEM");
            arr.put(schemaName);
        }
        obj.put("Schemas", arr);
        return obj;
    }

    @ThingworxServiceDefinition(name = "GetModelCached", description = "Get's the Model from the internal Cache ...", category = "Metadata Datashapes", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBModel() throws Exception {
        DbModel model = DBModelManager.getDBModelCached( DBUtil.getConfiguredApplication() );
        return  model.toJSON();
    }

    @ThingworxServiceDefinition(name = "CreateTable", description = "Create database table using the datashape", isAllowOverride = true)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void CreateTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        _logger.debug("CreateTable:" + dataShapeName);

    }

    private IDatabaseHandler getDatabaseHandler() throws Exception {
        
        Connection conn = DBUtil.getConnection();
        return new MsSQLDatabaseHandler(conn);
    }

}
