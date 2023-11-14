package twx.core.db.liquibase;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.relationships.RelationshipTypes;
import com.thingworx.things.Thing;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.things.repository.FileRepositoryThing;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.UpdateSqlCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.io.WriterOutputStream;
import liquibase.logging.LogService;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import twx.core.db.DatabaseTS;
import twx.core.db.handler.DbHandler;
import twx.core.imp.ThingUtil;

public class LiquibaseRunner {

    private static Logger logger = LogUtilities.getInstance().getDatabaseLogger(LiquibaseRunner.class);
    private static LogService logService = new ThingworxLogService();
    private String changelogPath = null;
    private String changelogFile = null;
    private ResourceAccessor accessor = null;
    private AbstractDatabase abstractDatabase = null;

    // region Get Parameters Handler ...
    // --------------------------------------------------------------------------------
    public LiquibaseRunner(AbstractDatabase abstractDatabase) {
        this.abstractDatabase = abstractDatabase;
        this.setChangelogFromConfig();
    }

    // endregion
    // region Get Parameters Handler ...
    // --------------------------------------------------------------------------------
    public Boolean setChangelogFromConfig() {
        String changelogRepos = (String) (abstractDatabase.getConfigurationSetting("LiquibaseChangelog", "ChangelogRepository"));
        String changelogPath = (String) (abstractDatabase.getConfigurationSetting("LiquibaseChangelog", "ChangelogPath"));
        String changelogFile = (String) (abstractDatabase.getConfigurationSetting("LiquibaseChangelog", "ChangelogFile"));
        return setChangelog(changelogRepos, changelogPath, changelogFile);
    }

    public Boolean setChangelog(String repos, String path, String file) {
        try {
            // first lookup for Repository thing ...
            this.changelogPath = ThingUtil.getFileRepos(repos).getRootPath() + (path != null ? path : "");
            this.changelogFile = file;
            this.accessor = new DirectoryResourceAccessor(Paths.get(this.changelogPath));
        }
        catch( Exception ex ) {
            logger.error("Error on update Database", ex);
            return false;
        }
        return true;
    }

    public JSONObject getChangelog() {
        JSONObject obj = new JSONObject();
        obj.put("path", this.changelogPath);
        obj.put("file", this.changelogFile);
        return obj;
    }

    protected Map<String, Object> getScope() {
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), this.accessor);
        scopeObjects.put(Scope.Attr.logService.name(), logService);
        return scopeObjects;
    }

    // find the repository ...
    // endregion
    // Update Commands ...
    // --------------------------------------------------------------------------------
    public Boolean update() throws Exception {
        try {
            var scopeObjects = getScope();
            Scope.child(scopeObjects, () -> {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(abstractDatabase.getConnection()));
                CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
                updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
                updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, this.changelogFile );
                updateCommand.execute();
            });
        } catch (Exception ex) {
            logger.error("Error on update Database", ex);
            return false;
        }
        return true;
    }

    public String updateSQL() throws Exception {
        StringWriter sw = new StringWriter();
        try {
            var scopeObjects = getScope();
            Scope.child(scopeObjects, () -> {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(abstractDatabase.getConnection()));
                CommandScope updateCommand = new CommandScope(UpdateSqlCommandStep.COMMAND_NAME);
                updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
                updateCommand.addArgumentValue(UpdateSqlCommandStep.CHANGELOG_FILE_ARG, this.changelogFile );
                updateCommand.setOutput(new WriterOutputStream(sw, Charset.forName("UTF-8")));
                updateCommand.execute();
            });
        } catch (Exception ex) {
            logger.error("Error on update Database", ex);
            return "";
        }
        return sw.toString();
    }

    // endregion
}
