/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package twx.core;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import org.antlr.v4.codegen.model.ExceptionClause;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.thingworx.logging.LogUtilities;
import com.thingworx.types.InfoTable;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbHandlerFactory;
import twx.core.db.liquibase.LiquibaseRunner;

public class App {

    final static Logger logger = LoggerFactory.getLogger(App.class);

    static final String DB_URL  = "jdbc:sqlserver://localhost:1433;database=twdata;";
    static final String USER    = "twx";
    static final String PASS    = "twx@1234";
    static final String appName = "TWX-Data";

    static final String FILE = "changelog.master.xml";
    static final String PATH = System.getProperty("user.dir") + "\\data";

    SQLServerDataSource ds = null;
    DbHandler handler = null;
    LiquibaseRunner lb = null;

    public static void main(String[] args) {
        var app = new App();
        var scanner = new Scanner(System.in);
        
        logger.info("LogLevel: " + LogUtilities.getLoggerLevel(""));

        logger.info("---------- Start-App ----------");
        Connection con = null;
        try {
            app.openDBConnection();
            logger.info("---------- Running Tests ----------");
            // app.handlerTest();
            // app.queryModel();
            // app.modelTest();
            // app.statementTest();
            app.datashapeTest();

        } catch (SQLException e) {
            printSQLException(e);
        } catch (Exception e) {
            logger.error("Exception: " + e.toString());
        } finally {
            app.closeDBConnection();
        }
        logger.info("---------- Exit-App ----------");
    }

    private void statementTest() {
        logger.info("---------- statementTest ----------");
        var test = new StatementTest(handler);
        test.runTests();
    }

    private void handlerTest() throws Exception {
        logger.info("---------- handlerTest ----------");        
        var test = new DbHandlerTest(this.handler);
        test.runTests();
    }

    private void modelTest()  throws Exception {
        logger.info("---------- handlerTest ----------");                
        var test = new DbModelTests(this.handler);
        test.runTests();
    }

    private void datashapeTest()  throws Exception {
        logger.info("---------- datashapeTest ----------");                
        var test = new DatashapeTests(this.handler);
        test.runTests();
    }

    private void queryModel() throws Exception {
        logger.info("---------- queryModel ----------");
        var model = handler.getModelManager().updateModel(null);
        model.setNote("This is a note at the model");
        
        // logger.info( handler.getModelManager().getModelTables().toString() );
    }

    protected void openDBConnection() throws Exception {
        logger.info("---------- openDBConnection ----------");
        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        this.ds = new SQLServerDataSource();
        this.ds.setUser("twx");
        this.ds.setPassword("twx@1234");
        this.ds.setServerName("localhost");
        this.ds.setPortNumber(1433);
        this.ds.setDatabaseName("twdata");
        this.ds.setApplicationName("TWX-Data");
        this.handler = DbHandlerFactory.getInstance().createMsSqlHandler(ds);

        logger.info("---------- DB Connection Opened ----------");
        logger.info("Handler Name   : {}", this.handler.getName() );
        logger.info("Handler Key    : {}", this.handler.getKey()  );
        logger.info("Handler Catalog: {}", this.handler.getDefaultCatalog());

        this.lb = new LiquibaseRunner( this.handler );
        lb.setChangelog(PATH, FILE);
        // lb.rollback(10);
        lb.update("","");
    }

    private void closeDBConnection() {
        
    }

    protected static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
