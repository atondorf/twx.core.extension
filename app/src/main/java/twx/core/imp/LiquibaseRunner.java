package twx.core.imp;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.things.database.AbstractDatabase;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateSqlCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.io.WriterOutputStream;
import liquibase.logging.LogService;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import twx.core.App;
import twx.core.db.handler.DbHandler;
import twx.core.db.liquibase.ThingworxLogService;

public class LiquibaseRunner {

    private static Logger logger = LoggerFactory.getLogger(LiquibaseRunner.class);

    static final String FILE = "changelog.master.xml";
    static final String PATH = System.getProperty("user.dir") + "\\data";

    private String changelogPath = null;
    private String changelogFile = null;
    private ResourceAccessor accessor = null;
    private DbHandler dbHandler = null;

    public LiquibaseRunner(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.setChangelog(PATH, FILE);
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

    protected Map<String, Object> getScope() {
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), this.accessor);
        return scopeObjects;
    }

    protected Database getDatabase() throws Exception {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dbHandler.getConnection()));
    }

    public String updateSQL() throws Exception {
        StringWriter sw = new StringWriter();
        try {
            var scopeObjects = getScope();
            Scope.child(scopeObjects, () -> {
                Database database = getDatabase();
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

}
