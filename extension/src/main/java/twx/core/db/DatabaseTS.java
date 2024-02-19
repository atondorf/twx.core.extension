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
        DatabaseUtil.getHandler().getModelManager().updateModel();
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
        var dbModel = DatabaseUtil.getHandler().getDbModel();
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("schema", BaseTypes.STRING));
        table.addField(new FieldDefinition("table", BaseTypes.STRING));
        table.addField(new FieldDefinition("dataShape", BaseTypes.STRING));

        for (var dbSchema : dbModel.getSchemas()) {
            for (var dbTable : dbSchema.getTables()) {
                ValueCollection values = new ValueCollection();
                values.put("schema", new StringPrimitive(dbSchema.getName()));
                values.put("table", new StringPrimitive(dbTable.getName()));
                values.put("dataShape", new StringPrimitive(dbTable.getDataShapeName()));
                table.addRow(values);
            }
        }
        return table;
    }

    @ThingworxServiceDefinition(name = "GetDBTableColumns", description = "", category = "DB Model", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable GetDBTableColumns(
            @ThingworxServiceParameter(name = "schemaName", description = "", baseType = "STRING") String schemaName,
            @ThingworxServiceParameter(name = "tableName", description = "", baseType = "STRING") String tableName) throws Exception {
        var dbModel = DatabaseUtil.getHandler().getDbModel();
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("schema", BaseTypes.STRING));
        table.addField(new FieldDefinition("table", BaseTypes.STRING));
        table.addField(new FieldDefinition("column", BaseTypes.STRING));
        table.addField(new FieldDefinition("sqlType", BaseTypes.STRING));
        table.addField(new FieldDefinition("twxType", BaseTypes.STRING));

        var dbSchema  = dbModel.getSchema(schemaName);
        var dbTable   = dbModel.getTable(tableName);
        for( var dbColumn : dbTable.getColumns() ) {
            ValueCollection values = new ValueCollection();
            values.put("schema", new StringPrimitive(dbSchema.getName()));
            values.put("table", new StringPrimitive(dbTable.getName()));
            values.put("column", new StringPrimitive(dbColumn.getName()));
            values.put("sqlType", new StringPrimitive(dbColumn.getTypeName()));
            values.put("twxType", new StringPrimitive(dbColumn.getTwxType().toString() ));
            
            table.addRow(values);
        }
        return table;
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
        return null;
    }

    @ThingworxServiceDefinition(name = "ExecutePreparedQuery", description = "", category = "SQL", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable ExecutePreparedQuery(
            @ThingworxServiceParameter(name = "sql", description = "SQL to execute", baseType = "STRING", aspects = { "isRequired:false" }) String sql,
            @ThingworxServiceParameter(name = "sqlQueries", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" }) InfoTable values 
            ) throws Exception {
        return null;
    }


    // endregion
    // region TWX-Services Basic DB Operations ...
    // --------------------------------------------------------------------------------

    // endregion
    // region TWX-Services DDL using Liquibase ...
    // --------------------------------------------------------------------------------
    // region Liquibase Update
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LBvalidate", description = "validates the changeLog for errors", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "ErrorCode, 0 is OK", baseType = "INTEGER", aspects = {})
    public Integer LBvalidate() throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.validate();
    }

    @ThingworxServiceDefinition(name = "LBupdate", description = "Updates the DB to latest changeset", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBupdate(
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.update(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBupdateSQL", description = "Creates SQL to update the DB to latest changeset, but does not apply", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBupdateSQL(
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateSQL(contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LBupdateCount", description = "Updates the DB with N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBupdateCount(
            @ThingworxServiceParameter(name = "changesToApply", description = "integer specifying how many changes Liquibase applies", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToApply,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.update(changesToApply, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBupdateCountSQL", description = "Creates SQL to update the DB with N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBupdateCountSQL(
            @ThingworxServiceParameter(name = "changesToApply", description = "integer specifying how many changes Liquibase applies", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToApply,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateSQL(changesToApply, contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LBupdateToTag", description = "Updates the DB to the to defened Tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBupdateToTag(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.updateToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBupdateToTagSQL", description = "Creates SQL to update the to defened Tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBupdateToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateToTagSQL(tag, contexts, labels);
        return sql;
    }

    // endregion
    // region Liquibase Rollback
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LBrollbackCount", description = "Rolls back N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBrollbackCount(
            @ThingworxServiceParameter(name = "changesToRollback", description = "integer specifying how many changes Liquibase applies", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToRollback,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollback(changesToRollback, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBrollbackCountSQL", description = "Creates SQL to roll back N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBrollbackCountSQL(
            @ThingworxServiceParameter(name = "changesToRollback", description = "integer specifying how many changes Liquibase applies", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToRollback,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackSQL(changesToRollback, contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LBrollbackToDate", description = "Rolls back to given date", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBrollbackToDate(
            @ThingworxServiceParameter(name = "dateToRollBackTo", description = "The date and time your database rolls back to", baseType = "DATETIME", aspects = { "isRequired:true" }) DateTime dateToRollBackTo,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollbackToDate(dateToRollBackTo.toDate(), contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBrollbackToDateSQL", description = "Creates SQL to roll back to given date", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBrollbackToDateSQL(
            @ThingworxServiceParameter(name = "dateToRollBackTo", description = "The date and time your database rolls back to", baseType = "DATETIME", aspects = { "isRequired:true" }) DateTime dateToRollBackTo,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackToDateSQL(dateToRollBackTo.toDate(), contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LBrollbackToTag", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBrollbackToTag(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollbackToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBrollbackToTagSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBrollbackToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackToTagSQL(tag, contexts, labels);
        return sql;
    }

    // endregion
    // region Liquibase Tracking Commands ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LBtag", description = "Creates a Tag in the current db, to mark a rollout", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBtag(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.tag(tag);
    }

    @ThingworxServiceDefinition(name = "LBtagExists", description = "checks whether the tag already exists in the db", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean LBtagExists(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING") String tag) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.tagExists(tag);
    }

    @ThingworxServiceDefinition(name = "LBgetTags", description = "Get a list of TAGS and apply date", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable LBgetTags() throws Exception {
        String sql = "SELECT TAG, DATEEXECUTED FROM dbo.DATABASECHANGELOG WHERE TAG IS NOT NULL";
        return this.ExecuteQuery(sql);
    }

    @ThingworxServiceDefinition(name = "LBstatus", description = "States the number of undeployed changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String LBstatus(
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.status(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBhistory", description = "lists all deployed changesets and their deploymentIds.", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String LBhistory() throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.history();
    }

    @ThingworxServiceDefinition(name = "LBchangeLogSync", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBchangeLogSync(
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.changeLogSync(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBchangeLogSyncSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBchangeLogSyncSQL(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.changeLogSyncSQL(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBchangeLogSyncToTag", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LBchangeLogSyncToTag(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.changeLogSyncToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LBchangeLogSyncToTagSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "generated SQL code", baseType = "STRING", aspects = {})
    public String LBchangeLogSyncToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "tag identifying which tagged changesets in the changelog to evaluate", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "comma separated list of context to filter changesets", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "label filter, see Liquibase documentation", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.changeLogSyncToTagSQL(tag, contexts, labels);
    }

    // endregion
    // endregion
}
