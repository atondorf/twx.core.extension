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
    @ThingworxServiceDefinition(name = "GetDBThingName", description = "get the root Thing of the database SQLThing or Persistance Provider", category = "Metadata Database Config", isAllowOverride = false, aspects = { "isAsync:false" })
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
    @ThingworxServiceDefinition(name = "QueryDBModel", description = "Queries the Model from Database, does not store it to the Model Tree", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
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
            DatabaseUtil.getHandler().getModelManager().updateModel(null);
            result = DatabaseUtil.getHandler().getDbModel().toJSON();
        }
        return result;
    }

    @ThingworxServiceDefinition(name = "RegisterDBDataShape", description = "Register a Datashape for a table", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void RegisterDBDataShape(
            @ThingworxServiceParameter(name = "schemaName", description = "", baseType = "STRING") String schemaName,
            @ThingworxServiceParameter(name = "tableName", description = "", baseType = "STRING") String tableName,
            @ThingworxServiceParameter(name = "dataShapeName", description = "", baseType = "DATASHAPENAME") String dataShapeName) throws Exception {
        var dbModel = DatabaseUtil.getHandler().getDbModel();
        dbModel.getSchema(schemaName).getTable(tableName).setDataShapeName(dataShapeName);
    }

    @ThingworxServiceDefinition(name = "GetDBTables", description = "Get a list of known tables and datashapes", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable GetDBTables() throws Exception {
        var dbModelManager = DatabaseUtil.getHandler().getModelManager();
        return dbModelManager.getTablesDesc();
    }

    @ThingworxServiceDefinition(name = "GetDBTableColumns", description = "get list of known table columsn and thingworx type mapping", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
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

    @ThingworxServiceDefinition(name = "ExecuteQuery", description = "Calls a single query and returns the result as infotable", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { })
    public InfoTable ExecuteQuery(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql) throws Exception {
        return DatabaseUtil.getHandler().executeQuery(sql);
    }

    @ThingworxServiceDefinition(name = "ExecuteUpdateBatch", description = "Calls multiple raw sql commands and returns - Send as Batch to SQL", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { })
    public InfoTable ExecuteUpdateBatch(
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true", "dataShape:TWX.Core.SQLBatch_DS" }) InfoTable sqlQueries) throws Exception {
        return DatabaseUtil.getHandler().executeUpdateBatch(sqlQueries);
    }
    
    @ThingworxServiceDefinition(name = "ExecuteQueryBatch", description = "Calls multiple raw sql queries and returns the results as infotable - No Batch, as this is not possible for query", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { })
    public InfoTable ExecuteQueryBatch(
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true", "dataShape:TWX.Core.SQLBatch_DS" }) InfoTable sqlQueries) throws Exception {
        return DatabaseUtil.getHandler().executeQueryBatch(sqlQueries);
    }
/*
    @ThingworxServiceDefinition(name = "ExecuteUpdatePrepared", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecuteUpdatePrepared(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql,
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        return DatabaseUtil.getHandler().executeUpdatePrepared(sql, values);
    }

    @ThingworxServiceDefinition(name = "ExecuteQueryPrepared", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecuteQueryPrepared(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql,
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        return DatabaseUtil.getHandler().executeQueryPrepared(sql, values);
    }
    // endregion
    // region TWX-Services Basic DB Operations With Tables and Datashapes ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "Insert", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable Insert(
            @ThingworxServiceParameter(name = "tableName", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:true" }) String tableName,
            @ThingworxServiceParameter(name = "values", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        throw new Exception("Unimplemented!");
    }

    @ThingworxServiceDefinition(name = "Update", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable Update(
            @ThingworxServiceParameter(name = "tableName", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String tableName,
            @ThingworxServiceParameter(name = "values", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        throw new Exception("Unimplemented!");
    }

    @ThingworxServiceDefinition(name = "UpdateColumns", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable UpdateColumns(
            @ThingworxServiceParameter(name = "tableName", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:true" }) String tableName,
            @ThingworxServiceParameter(name = "values", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values,
            @ThingworxServiceParameter(name = "columnNames", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String columnNames
            ) throws Exception {
        throw new Exception("Unimplemented!");
    }

    @ThingworxServiceDefinition(name = "Query", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable Query(
            @ThingworxServiceParameter(name = "tableName", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String tableName,
            @ThingworxServiceParameter(name = "filter", description = "", baseType = "JSON", aspects = { "isEntityDataShape:true" }) JSONObject filter 
            ) throws Exception {
        throw new Exception("Unimplemented!");
    }
 */    
    // endregion
}
