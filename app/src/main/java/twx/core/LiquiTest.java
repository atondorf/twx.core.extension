package twx.core;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiquiTest {

    final static Logger logger = LoggerFactory.getLogger(LiquiTest.class);

    static final String FILE = "master.changelog.xml";
    static final String PATH = System.getProperty("user.dir") + "\\data";

    public static String changelog = null;
    public static Database db = null;
    public static Liquibase lb = null;

    public static void log_changelog(Connection con) throws SQLException, DatabaseException, LiquibaseException {
        try {
            File file = new File(FILE);
            Scanner fs = new Scanner(file);
            while (fs.hasNextLine()) {
                String line = fs.nextLine();
                System.out.println(line);
            }
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log_changelog2(Connection con) throws Exception {

        Path path = Paths.get(PATH);
        logger.info("path: " + path);

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(con));
        database.setDefaultSchemaName("dbo");

        lb.update();
    }

    public static void test_database(Connection con) throws Exception {
        ResourceAccessor accessor = new DirectoryResourceAccessor( Paths.get(PATH) );
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), accessor );
        Scope.child(scopeObjects, () -> {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            // Liquibase liquibase  = new Liquibase( "changelog.mssql.sql", accessor, database);
            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, FILE );
            updateCommand.execute();
        });
    }

}
