package twx.core.db;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;

// import com.thingworx.connections.Connection;
import com.thingworx.datashape.DataShape;
import com.thingworx.datashape.DataShapeUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.system.ContextType;
import com.thingworx.things.Thing;
import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.things.handlers.IThingDisposeHandler;
import com.thingworx.things.handlers.IThingInitializeHandler;
import com.thingworx.things.handlers.IThingUpdateHandler;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

import liquibase.snapshot.ResultSetCache;
import twx.core.db.handler.DbHandler;
import twx.core.db.model.DbModel;
import twx.core.db.util.DatabaseUtil;
import twx.core.imp.DataShapeUtils;

public class DatabaseTS implements IThingInitializeHandler, IThingUpdateHandler, IThingDisposeHandler {
    private static Logger logger = LogUtilities.getInstance().getDatabaseLogger(DatabaseTS.class);

    @Override
    public void handleInitializeThing(Thing arg0) throws Exception {

    }

    @Override
    public void handleInitializeThing(Thing arg0, ContextType arg1) throws Exception {
        
    }

    @Override
    public void handleUpdateThing(Thing arg0, Thing arg1) throws Exception {
        
    }

    @Override
    public void handleDisposeThing(Thing arg0) throws Exception {

    }


    // region TWX-Services Metadata Configuration ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "GetDBThingName", description = "get the root Thing of the database", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBThingName() throws Exception {
        return DatabaseUtil.getDatabaseThingName();
    }

    @ThingworxServiceDefinition(name = "GetDBName", description = "get the type name of the database", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBName() throws Exception {
        return DatabaseUtil.getHandler().getName();
    }

    @ThingworxServiceDefinition(name = "GetDBKey", description = "get the jdbc identifiert of database type", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBKey() throws Exception {
        return DatabaseUtil.getHandler().getKey();
    }

    @ThingworxServiceDefinition(name = "GetDBCatalog", description = "get the catalog name of the database", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBCatalog() throws Exception {
        return DatabaseUtil.getHandler().getDefaultCatalog();
    }

    @ThingworxServiceDefinition(name = "GetDBDefaultSchema", description = "get the default schema of dtabase", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String GetDBDefaultSchema() throws Exception {
        return DatabaseUtil.getHandler().getDefaultSchema();
    }

    // endregion
    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "QueryDBModel", description = "Queries the Model from Database, does not store it to the Model Tree", category = "Metadata Database", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject QueryDBModel() throws Exception {
        return DatabaseUtil.getHandler().getModelManager().queryModel().toJSON();
    }

    @ThingworxServiceDefinition(name = "GetDBModel", description = "Get's the Model from the internal Model Cache ...", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetDBModel() throws Exception {
        JSONObject result = null;
        var model = DatabaseUtil.getHandler().getDbModel();
        if (model != null) {
            result = model.toJSON();
        } else {
            result = new JSONObject();
        }
        return result;
    }

    @ThingworxServiceDefinition(name = "UpdateDBModel", description = "Updates the Model in the internal Model Cache ...", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject UpdateDBModel() throws Exception {
        // TODO: Move Datashapes Info from old => new ...
        DatabaseUtil.getHandler().getModelManager().updateModel(null);
        return DatabaseUtil.getHandler().getDbModel().toJSON();
    }

    @ThingworxServiceDefinition(name = "RegisterDBDataShape", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void RegisterDBDataShape(
            @ThingworxServiceParameter(name = "schemaName", description = "", baseType = "STRING") String schemaName,
            @ThingworxServiceParameter(name = "tableName", description = "", baseType = "STRING") String tableName,
            @ThingworxServiceParameter(name = "dataShapeName", description = "", baseType = "DATASHAPENAME") String dataShapeName) throws Exception {
        var dbModel = DatabaseUtil.getHandler().getDbModel();
        dbModel.getSchema(schemaName).getTable(tableName).setDataShapeName(dataShapeName);
    }

    @ThingworxServiceDefinition(name = "ValidateDBDataShape", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject ValidateDBDataShape(
            @ThingworxServiceParameter(name = "schemaName", description = "", baseType = "STRING") String schemaName,
            @ThingworxServiceParameter(name = "tableName", description = "", baseType = "STRING") String tableName,
            @ThingworxServiceParameter(name = "dataShapeName", description = "", baseType = "DATASHAPENAME") String dataShapeName) throws Exception {

        JSONObject obj = new JSONObject();
        DataShape ds = DataShapeUtils.getDatashapeDirect(dataShapeName);

        obj.put("Datashape", ds.getName());

        return obj;
    }

    @ThingworxServiceDefinition(name = "GetDBTables", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable GetDBTables() throws Exception {
        var dbModelManager = DatabaseUtil.getHandler().getModelManager();
        return dbModelManager.getTablesDesc();
    }

    @ThingworxServiceDefinition(name = "GetDBTableColumns", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable GetDBTableColumns(
            @ThingworxServiceParameter(name = "schemaName", description = "", baseType = "STRING") String schemaName,
            @ThingworxServiceParameter(name = "tableName", description = "", baseType = "STRING") String tableName) throws Exception {
        var dbModelManager = DatabaseUtil.getHandler().getModelManager();
        return dbModelManager.getTableColumnsDesc(schemaName,tableName);
    }

    // endregion
    // region TWX-Services Basic DB Operations ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "ExecuteUpdate", description = "Calls raw sql command and returns integer", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Integer ExecuteUpdate(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql) throws Exception {
        return DatabaseUtil.getHandler().executeUpdate(sql);
    }

    @ThingworxServiceDefinition(name = "ExecuteBatch", description = "Calls multiple raw sql commands and returns them in same infotable", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true", "dataShape:TWX.Core.DBBatch_DS" })
    public InfoTable ExecuteBatch(
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true", "dataShape:TWX.Core.DBBatch_DS" }) InfoTable sqlQueries) throws Exception {
        return DatabaseUtil.getHandler().executeBatch(sqlQueries);
    }

    @ThingworxServiceDefinition(name = "ExecuteQuery", description = "Calls a single query and returns the result as infotable", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecuteQuery(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql) throws Exception {
        return DatabaseUtil.getHandler().executeQuery(sql);
    }

    @ThingworxServiceDefinition(name = "ExecutePreparedCommand", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecutePreparedUpdate(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql,
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        return DatabaseUtil.getHandler().executePreparedQuery(sql, values);
    }

    @ThingworxServiceDefinition(name = "ExecutePreparedQuery", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecutePreparedQuery(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql,
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        return DatabaseUtil.getHandler().executePreparedUpdate(sql, values);
    }
    // endregion
    // region TWX-Services Basic DB Operations ...
    // --------------------------------------------------------------------------------

    // endregion
}
