package twx.core.db.liquibase;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.thingworx.logging.LogUtilities;
import com.thingworx.things.Thing;
import com.thingworx.things.database.AbstractDatabase;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Scope;
import liquibase.command.CommandResults;
import liquibase.command.CommandScope;
import liquibase.command.core.ChangelogSyncCommandStep;
import liquibase.command.core.ChangelogSyncSqlCommandStep;
import liquibase.command.core.ChangelogSyncToTagCommandStep;
import liquibase.command.core.ChangelogSyncToTagSqlCommandStep;
import liquibase.command.core.InternalHistoryCommandStep;
import liquibase.command.core.InternalHistoryCommandStep.DeploymentHistory;
import liquibase.command.core.RollbackCommandStep;
import liquibase.command.core.RollbackCountCommandStep;
import liquibase.command.core.RollbackCountSqlCommandStep;
import liquibase.command.core.RollbackSqlCommandStep;
import liquibase.command.core.RollbackToDateCommandStep;
import liquibase.command.core.RollbackToDateSqlCommandStep;
import liquibase.command.core.StatusCommandStep;
import liquibase.command.core.TagCommandStep;
import liquibase.command.core.TagExistsCommandStep;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.UpdateCountCommandStep;
import liquibase.command.core.UpdateCountSqlCommandStep;
import liquibase.command.core.UpdateSqlCommandStep;
import liquibase.command.core.UpdateToTagCommandStep;
import liquibase.command.core.UpdateToTagSqlCommandStep;
import liquibase.command.core.ValidateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.io.WriterOutputStream;
import liquibase.logging.LogService;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import twx.core.db.handler.DbHandler;
import twx.core.db.util.DatabaseUtil;
import twx.core.imp.ThingUtil;

public class LiquibaseRunner {
    private static Logger logger = LogUtilities.getInstance().getDatabaseLogger(LiquibaseRunner.class);
    private static LogService logService = new ThingworxLogService();
    private String changelogPath = null;
    private String changelogFile = null;
    private ResourceAccessor accessor = null;
    private DbHandler dbHandler = null;

    // region Get Parameters Handler ...
    // --------------------------------------------------------------------------------
    public LiquibaseRunner(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public LiquibaseRunner(AbstractDatabase abstractDatabase) throws Exception {
        this.dbHandler = DatabaseUtil.getHandler(abstractDatabase);
        this.setChangelogFromThingConfig(abstractDatabase);
    }

    // endregion
    // region Get Parameters Handler ...
    // --------------------------------------------------------------------------------
    public Boolean setChangelogFromThingConfig(Thing thing) {
        try {
            String changelogRepos = (String) (thing.getConfigurationSetting("LiquibaseChangelog", "ChangelogRepository"));
            String changelogPath = (String) (thing.getConfigurationSetting("LiquibaseChangelog", "ChangelogPath"));
            String changelogFile = (String) (thing.getConfigurationSetting("LiquibaseChangelog", "ChangelogFile"));

            // first lookup for Repository thing ...
            this.changelogPath = ThingUtil.getFileRepos(changelogRepos).getRootPath() + (changelogPath != null ? changelogPath : "");
            this.changelogFile = changelogFile;
            this.accessor = new DirectoryResourceAccessor(Paths.get(this.changelogPath));
        } catch (Exception ex) {
            logger.error("Error on update Database", ex);
            return false;
        }
        return true;
    }

    public Boolean setChangelog(String path, String file) {
        try {
            // first lookup for Repository thing ...
            this.changelogPath = (path != null ? path : "");
            this.changelogFile = file;
            this.accessor = new DirectoryResourceAccessor(Paths.get(this.changelogPath));
        } catch (Exception ex) {
            logger.error("Error on update Database", ex);
            return false;
        }
        return true;
    }

    protected Map<String, Object> getScope() throws LiquibaseException {
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.database.name(), this.getDatabase());
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), this.accessor);
        scopeObjects.put(Scope.Attr.logService.name(), logService);
        return scopeObjects;
    }

    protected Database getDatabase() throws LiquibaseException {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dbHandler.getConnection()));
    }

    private void runInScope(Scope.ScopedRunner scopedRunner) throws LiquibaseException {
        Map<String, Object> scopeObjects = this.getScope();
        try {
            Scope.child(scopeObjects, scopedRunner);
        } catch (Exception e) {
            logger.error("Error on Liquibase run", e);
            if (e instanceof LiquibaseException) {
                throw (LiquibaseException) e;
            } else {
                throw new LiquibaseException(e);
            }
        }
    }

    private <ReturnType> ReturnType runInScopeWithReturn(Scope.ScopedRunnerWithReturn<ReturnType> scopedRunner) throws LiquibaseException {
        Map<String, Object> scopeObjects = this.getScope();
        try {
            return Scope.child(scopeObjects, scopedRunner);
        } catch (Exception e) {
            logger.error("Error on Liquibase run", e);
            if (e instanceof LiquibaseException) {
                throw (LiquibaseException) e;
            } else {
                throw new LiquibaseException(e);
            }
        }
    }

    // endregion
    // Update Commands ...
    // --------------------------------------------------------------------------------
    public Integer validate() throws LiquibaseException {
        Object obj = runInScopeWithReturn(() -> {
            CommandScope command = new CommandScope(ValidateCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            CommandResults commandResults = command.execute();
            return commandResults.getResult("statusCode");
        });
        return (Integer) obj;
    }

    public void update() throws LiquibaseException {
        update(new Contexts(""), new LabelExpression());
    }

    public void update(String contexts, String labelExpression) throws LiquibaseException {
        update(new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void update(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.execute();
        });
    }

    public String updateSQL() throws LiquibaseException {
        return updateSQL(new Contexts(""), new LabelExpression());
    }

    public String updateSQL(String contexts, String labelExpression) throws LiquibaseException {
        return updateSQL(new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String updateSQL(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateSqlCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateSqlCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateSqlCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void update(int changesToApply) throws LiquibaseException {
        update(changesToApply, new Contexts(""), new LabelExpression());
    }

    public void update(int changesToApply, String contexts, String labelExpression) throws LiquibaseException {
        update(changesToApply, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void update(int changesToApply, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateCountCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateCountCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateCountCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateCountCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(UpdateCountCommandStep.COUNT_ARG, changesToApply);
            command.execute();
        });
    }

    public String updateSQL(int changesToApply) throws LiquibaseException {
        return updateSQL(changesToApply, new Contexts(""), new LabelExpression());
    }

    public String updateSQL(int changesToApply, String contexts, String labelExpression) throws LiquibaseException {
        return updateSQL(changesToApply, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String updateSQL(int changesToApply, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateCountSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateCountSqlCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateCountSqlCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateCountSqlCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(UpdateCountSqlCommandStep.COUNT_ARG, changesToApply);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void updateToTag(String tag) throws LiquibaseException {
        updateToTag(tag, new Contexts(), new LabelExpression());
    }

    public void updateToTag(String tag, String contexts, String labelExpression) throws LiquibaseException {
        updateToTag(tag, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void updateToTag(String tag, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateToTagCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateToTagCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateToTagCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateToTagCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(UpdateToTagCommandStep.TAG_ARG, tag);
            command.execute();
        });
    }

    public String updateToTagSQL(String tag) throws LiquibaseException {
        return updateToTagSQL(tag, new Contexts(), new LabelExpression());
    }

    public String updateToTagSQL(String tag, String contexts, String labelExpression) throws LiquibaseException {
        return updateToTagSQL(tag, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String updateToTagSQL(String tag, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(UpdateToTagSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(UpdateToTagSqlCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(UpdateToTagSqlCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(UpdateToTagSqlCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(UpdateToTagSqlCommandStep.TAG_ARG, tag);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    // endregion
    // Rollback Commands ...
    // --------------------------------------------------------------------------------
    public void rollback(int changesToRollback) throws LiquibaseException {
        rollback(changesToRollback, new Contexts(), new LabelExpression());
    }

    public void rollback(int changesToRollback, String contexts, String labelExpression) throws LiquibaseException {
        rollback(changesToRollback, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void rollback(int changesToRollback, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackCountCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackCountCommandStep.COUNT_ARG, changesToRollback);
            command.execute();
        });
    }

    public String rollbackSQL(int changesToRollback) throws LiquibaseException {
        return rollbackSQL(changesToRollback, new Contexts(), new LabelExpression());
    }

    public String rollbackSQL(int changesToRollback, String contexts, String labelExpression) throws LiquibaseException {
        return rollbackSQL(changesToRollback, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String rollbackSQL(int changesToRollback, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackCountSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackCountSqlCommandStep.COUNT_ARG, changesToRollback);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void rollbackToDate(Date dateToRollBackTo) throws LiquibaseException {
        rollbackToDate(dateToRollBackTo, new Contexts(), new LabelExpression());
    }

    public void rollbackToDate(Date dateToRollBackTo, String contexts, String labelExpression) throws LiquibaseException {
        rollbackToDate(dateToRollBackTo, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void rollbackToDate(Date dateToRollBackTo, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackToDateCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackToDateCommandStep.DATE_ARG, dateToRollBackTo);
            command.execute();
        });
    }

    public String rollbackToDateSQL(Date dateToRollBackTo) throws LiquibaseException {
        return rollbackToDateSQL(dateToRollBackTo, new Contexts(), new LabelExpression());
    }

    public String rollbackToDateSQL(Date dateToRollBackTo, String contexts, String labelExpression) throws LiquibaseException {
        return rollbackToDateSQL(dateToRollBackTo, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String rollbackToDateSQL(Date dateToRollBackTo, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackToDateSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackToDateSqlCommandStep.DATE_ARG, dateToRollBackTo);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void rollbackToTag(String tagToRollBackTo) throws LiquibaseException {
        rollbackToTag(tagToRollBackTo, new Contexts(), new LabelExpression());
    }

    public void rollbackToTag(String tagToRollBackTo, String contexts, String labelExpression) throws LiquibaseException {
        rollbackToTag(tagToRollBackTo, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void rollbackToTag(String tagToRollBackTo, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackCommandStep.TAG_ARG, tagToRollBackTo);
            command.execute();
        });
    }

    public String rollbackToTagSQL(String tagToRollBackTo) throws LiquibaseException {
        return rollbackToTagSQL(tagToRollBackTo, new Contexts(), new LabelExpression());
    }

    public String rollbackToTagSQL(String tagToRollBackTo, String contexts, String labelExpression) throws LiquibaseException {
        return rollbackToTagSQL(tagToRollBackTo, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String rollbackToTagSQL(String tagToRollBackTo, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(RollbackSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, (contexts != null ? contexts.toString() : null));
            command.addArgumentValue(RollbackSqlCommandStep.TAG_ARG, tagToRollBackTo);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    // endregion
    // Tracking Commands ...
    // --------------------------------------------------------------------------------
    public void tag(String tag) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope("tag");
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(TagCommandStep.TAG_ARG, tag);
            command.execute();
        });
    }

    public boolean tagExists(String tag) throws LiquibaseException {
        Object obj = runInScopeWithReturn(() -> {
            CommandScope command = new CommandScope("tagExists");
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(TagExistsCommandStep.TAG_ARG, tag);
            CommandResults commandResults = command.execute();
            return commandResults.getResult(TagExistsCommandStep.TAG_EXISTS_RESULT);
        });
        return (boolean) obj;
    }

    public String status() throws LiquibaseException {
        return status(new Contexts(""), new LabelExpression());
    }

    public String status(String contexts, String labelExpression) throws LiquibaseException {
        return status(new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String status(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(StatusCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public String history() throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(InternalHistoryCommandStep.COMMAND_NAME);
            command.addArgumentValue(InternalHistoryCommandStep.DATABASE_ARG, this.getDatabase());
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void changeLogSync() throws LiquibaseException {
        changeLogSync(new Contexts(), new LabelExpression());
    }

    public void changeLogSync(String contexts, String labelExpression) throws LiquibaseException {
        changeLogSync(new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void changeLogSync(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(ChangelogSyncCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.execute();
        });
    }

    public String changeLogSyncSQL() throws LiquibaseException {
        return changeLogSyncSQL(new Contexts(), new LabelExpression());
    }

    public String changeLogSyncSQL(String contexts, String labelExpression) throws LiquibaseException {
        return changeLogSyncSQL(new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String changeLogSyncSQL(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(ChangelogSyncSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    public void changeLogSyncToTag(String tag) throws LiquibaseException {
        changeLogSyncToTag(tag, new Contexts(), new LabelExpression());
    }

    public void changeLogSyncToTag(String tag, String contexts, String labelExpression) throws LiquibaseException {
        changeLogSyncToTag(tag, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public void changeLogSyncToTag(String tag, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        runInScope(() -> {
            CommandScope command = new CommandScope(ChangelogSyncToTagCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(ChangelogSyncToTagSqlCommandStep.TAG_ARG, tag);
            command.execute();
        });
    }

    public String changeLogSyncToTagSQL(String tag) throws LiquibaseException {
        return changeLogSyncToTagSQL(tag, new Contexts(), new LabelExpression());
    }

    public String changeLogSyncToTagSQL(String tag, String contexts, String labelExpression) throws LiquibaseException {
        return changeLogSyncToTagSQL(tag, new Contexts(contexts), new LabelExpression(labelExpression));
    }

    public String changeLogSyncToTagSQL(String tag, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        StringWriter sw = new StringWriter();
        runInScope(() -> {
            CommandScope command = new CommandScope(ChangelogSyncToTagSqlCommandStep.COMMAND_NAME);
            command.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, this.getDatabase());
            command.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, this.changelogFile);
            command.addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelExpression != null ? labelExpression.getOriginalString() : null);
            command.addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts != null ? contexts.toString() : null);
            command.addArgumentValue(ChangelogSyncToTagSqlCommandStep.TAG_ARG, tag);
            command.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
            command.execute();
        });
        return sw.toString();
    }

    // endregion
}
