package twx.core.db;

import org.joda.time.DateTime;
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
    // region Liquibase Update
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LiquiValidate", description = "", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Integer LiquiValidate() throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.validate();
    }

    @ThingworxServiceDefinition(name = "LiquiUpdate", description = "Updates the DB to latest changeset", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiUpdate(
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.update(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiUpdateSQL", description = "Creates SQL to update the DB to latest changeset, but does not apply", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiUpdateSQL(
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateSQL(contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LiquiUpdateN", description = "Updates the DB with N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiUpdateN(
            @ThingworxServiceParameter(name = "changesToApply", description = "", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToApply,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.update(changesToApply, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiUpdateNSQL", description = "Creates SQL to update the DB with N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiUpdateNSQL(
            @ThingworxServiceParameter(name = "changesToApply", description = "", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToApply,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateSQL(changesToApply, contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LiquiUpdateToTag", description = "Updates the DB to the to defened Tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiUpdateToTag(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.updateToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiUpdateToTagSQL", description = "Creates SQL to update the to defened Tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiUpdateToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.updateToTagSQL(tag, contexts, labels);
        return sql;
    }

    // endregion
    // region Liquibase Rollback
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LiquiRollbackN", description = "Rolls back N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiRollbackN(
            @ThingworxServiceParameter(name = "changesToRollback", description = "", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToRollback,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollback(changesToRollback, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiRollbackNSQL", description = "Creates SQL to roll back N changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiRollbackNSQL(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "INTEGER", aspects = { "isRequired:true" }) Integer changesToRollback,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackSQL(changesToRollback, contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LiquiRollbackToDate", description = "Rolls back to given date", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiRollbackToDate(
            @ThingworxServiceParameter(name = "dateToRollBackTo", description = "", baseType = "DATETIME", aspects = { "isRequired:true" }) DateTime dateToRollBackTo,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollbackToDate(dateToRollBackTo.toDate(), contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiRollbackToDateSQL", description = "Creates SQL to roll back to given date", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiRollbackToDateSQL(
            @ThingworxServiceParameter(name = "dateToRollBackTo", description = "", baseType = "DATETIME", aspects = { "isRequired:true" }) DateTime dateToRollBackTo,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackToDateSQL(dateToRollBackTo.toDate(), contexts, labels);
        return sql;
    }

    @ThingworxServiceDefinition(name = "LiquiRollbackToTag", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiRollbackToTag(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.rollbackToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiRollbackToTagSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiRollbackToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        String sql = lbRunner.rollbackToTagSQL(tag, contexts, labels);
        return sql;
    }
    // endregion
    // region Liquibase Tracking Commands ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "LiquiTag", description = "Creates a Tag in the current db, to mark a rollout", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiTag(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag ) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.tag(tag);
    }

	@ThingworxServiceDefinition(name = "LiquiTagExists", description = "checks whether the tag already exists in the db", category = "LiquiBase", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean LiquiTagExists(
                @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING") String tag) throws Exception  {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.tagExists(tag);
    }

    @ThingworxServiceDefinition(name = "LiquiStatus", description = "States the number of undeployed changesets", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String LiquiStatus(
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.status(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiHistory", description = "lists all deployed changesets and their deploymentIds.", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String LiquiHistory() throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.history();
    }

    @ThingworxServiceDefinition(name = "LiquiChangeLogSync", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiChangeLogSync(
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.changeLogSync(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiChangeLogSyncSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiChangeLogSyncSQL(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.changeLogSyncSQL(contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiChangeLogSyncToTag", description = "Rolls back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void LiquiChangeLogSyncToTag(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        lbRunner.changeLogSyncToTag(tag, contexts, labels);
    }

    @ThingworxServiceDefinition(name = "LiquiChangeLogSyncToTagSQL", description = "Creates SQL to roll back to given tag", category = "LiquiBase", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "TEXT", aspects = {})
    public String LiquiChangeLogSyncToTagSQL(
            @ThingworxServiceParameter(name = "tag", description = "", baseType = "STRING", aspects = { "isRequired:true" }) String tag,
            @ThingworxServiceParameter(name = "contexts", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String contexts,
            @ThingworxServiceParameter(name = "labels", description = "", baseType = "STRING", aspects = { "isRequired:false" }) String labels) throws Exception {
        var lbRunner = DatabaseUtil.getLiquibaseRunner();
        return lbRunner.changeLogSyncToTagSQL(tag, contexts, labels);
    }

    // endregion
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
    public void CreateSchema(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) throws Exception {

    }

    @ThingworxServiceDefinition(name = "DropSchema", description = "", category = "Schema Handling", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void DropSchema(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) {

    }

    // endregion
    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    @ThingworxServiceDefinition(name = "CreateTableModel", description = "", category = "", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject CreateTableModel(
            @ThingworxServiceParameter(name = "dataShapeName", description = "", baseType = "DATASHAPENAME") String dataShapeName,
            @ThingworxServiceParameter(name = "dnInfo", description = "", baseType = "JSON") JSONObject dnInfo) throws Exception {
        /*
         * var ds = TwxDataShapeUtil.getDataShape(dataShapeName);
         * var modelMgr = TwxDbUtil.getHandler().getModelManager();
         * DbTable table = modelMgr.getTableFromDataShape(ds);
         * return table.toJSON();
         */
        return null; //
    }

    @ThingworxServiceDefinition(name = "GetTableNames", description = "", category = "Table Handling", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetTableNames(@ThingworxServiceParameter(name = "SchemaName", description = "", baseType = "STRING") String SchemaName) throws Exception {
        /*
         * DbModel model = TwxDbUtil.getDBModel();
         * DbSchema schema = model.getSchema(SchemaName);
         * //!TODO ...
         * JSONObject obj = new JSONObject();
         * JSONArray arr = new JSONArray();
         * for (String name : model.getSchemaNames() ) {
         * System.out.println(name);
         * arr.put(name);
         * }
         * obj.put("schemas", arr);
         * obj.put("name", model.getName() );
         * return obj;
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

    @ThingworxServiceDefinition(name = "DropTable", description = "Create database table using the datashape", category = "Table Handling", isAllowOverride = false)
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
