package twx.core;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.UpdateSqlCommandStep;
import liquibase.command.core.ValidateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.io.WriterOutputStream;
import liquibase.report.UpdateReportParameters;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiquiTest {

    final static Logger logger = LoggerFactory.getLogger(LiquiTest.class);

    static final String FILE = "changelog.master.xml";
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

    public static void update(Connection con) throws Exception {
        ResourceAccessor accessor = new DirectoryResourceAccessor( Paths.get(PATH) );
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), accessor );
        Scope.child(scopeObjects, () -> {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            // Liquibase liquibase  = new Liquibase( "\\data\\changelog.mssql.sql", accessor, database);
            // liquibase.update();

            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, FILE );
            updateCommand.execute();

            logger.info("----- Resultset -----");

        });
    }

    public static void updateSQL(Connection con) throws Exception {
        ResourceAccessor accessor = new DirectoryResourceAccessor( Paths.get(PATH) );
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), accessor );
        Scope.child(scopeObjects, () -> {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            StringWriter sw = new StringWriter();
/*
            Liquibase liquibase  = new Liquibase( FILE, accessor, database);
            liquibase.update( new Contexts(), sw );
 */
            CommandScope updateCommand = new CommandScope(UpdateSqlCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
            updateCommand.addArgumentValue(UpdateSqlCommandStep.CHANGELOG_FILE_ARG, FILE );
//            updateCommand.addArgumentValue(UpdateSqlCommandStep.OUTPUT_WRITER, sw );
            updateCommand.setOutput(new WriterOutputStream(sw));

            var results = updateCommand.execute().getResults();

            logger.info("----- Resultset -----");
            logger.info("SQL: " + sw.toString() );


/*
            logger.info("Status: " + results.get("statusCode"));

            UpdateReportParameters params = (UpdateReportParameters)results.get("updateReport");
            params.toString();
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("OperationInfo: " + params.getOperationInfo().toString() );
            logger.info("ChangesetInfo: " + params.getChangesetInfo().toString());

            for( var res : results.entrySet()) {
                logger.info( res.getKey() + " :  " + res.getValue() );
            }



/*            
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
            logger.info("runtimeInfo: " + params.getRuntimeInfo().toString() );
*/
            
        });
    }

    public static void validate(Connection con) throws Exception {
        ResourceAccessor accessor = new DirectoryResourceAccessor( Paths.get(PATH) );
        Map<String, Object> scopeObjects = new HashMap<>();
        scopeObjects.put(Scope.Attr.resourceAccessor.name(), accessor );
        Scope.child(scopeObjects, () -> {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(con));
            
            // Liquibase liquibase  = new Liquibase( "changelog.mssql.sql", accessor, database);


            CommandScope updateCommand = new CommandScope(ValidateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database );
            updateCommand.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, FILE );
            updateCommand.execute();
        });
    }
}
