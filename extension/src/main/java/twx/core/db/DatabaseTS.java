package twx.core.db;

import org.json.JSONObject;
import org.slf4j.Logger;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.logging.LogUtilities;

import twx.core.db.imp.DBUtil;

public class DatabaseTS {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseTS.class);

	@ThingworxServiceDefinition(name = "GetDBKey", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBKey() throws Exception {
        return DBUtil.getKey();
    }

    @ThingworxServiceDefinition(name = "CreateTable", description = "Create database table using the datashape", isAllowOverride = true)
    @ThingworxServiceResult(name = "result", description = "result", baseType = "NOTHING")
    public void CreateTable(
            @ThingworxServiceParameter(name = "dataShapeName", description = "The data shape name", baseType = "DATASHAPENAME", aspects = { "isRequired:true" }) String dataShapeName,
            @ThingworxServiceParameter(name = "dbInfo", description = "The json description of the table to create", baseType = "JSON", aspects = { "isRequired:false" }) JSONObject dbInfo) throws Exception {
        _logger.debug("CreateTable:" + dataShapeName);

    }
    


}
