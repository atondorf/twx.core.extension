/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package twx.core;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbHandlerFactory;
import twx.core.db.impl.DataSourceConnectionManager;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbSchema;
import twx.core.db.model.settings.DbTableSetting;

public class App {

    final static Logger logger = LoggerFactory.getLogger(App.class);

    static final String DB_URL = "jdbc:sqlserver://localhost:1433;database=twdata;";
    static final String USER = "twx";
    static final String PASS = "twx@1234";
    static final String appName = "TWX-Data";

    SQLServerDataSource ds = null;
    DbHandler handler = null;

    public static void main(String[] args) {
        var app = new App();
        var scanner = new Scanner(System.in);
        logger.info("---------- Start-App ----------");
        Connection con = null;
        try {
            app.openDBConnection();
            app.queryModel();

        } catch (SQLException e) {
            printSQLException(e);
        } catch (Exception e) {
            logger.error("Exception: " + e.toString());
        } finally {
            app.closeDBConnection();
        }
        logger.info("---------- Exit-App ----------");
    }

    private void queryModel() throws SQLException {
        logger.info("---------- queryModel ----------");
        var model = handler.getDDLReader().queryModel(); // handler.getDbModel();

        model.setNote("This is a note at the model");
/*
        model.getDefaultSchema().setNote("This is a note at the default schema");
        model.getDefaultSchema().getTable("tab_1").addSetting(DbTableSetting.HEADERCOLOR, "0xffffff");
        model.getDefaultSchema().getTable("tab_1").addSetting(DbTableSetting.THINGWORXTYPE, "test_DS");
/*
        model.setDescription("This is a test");
        model.addTable("test_1");
        model.addTable("test_2");
*/        
        logger.info(model.toJSON().toString(2));
    }

    public void loadModelFromJSON() throws Exception {
        logger.info("---------- loadModelFromFile ----------");
        InputStream is = App.class.getResourceAsStream("/db1.json");
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + "db1.json");
        }
        JSONTokener tokener = new JSONTokener(is);
        JSONObject dbInfo = new JSONObject(tokener);
        /*
         * var model = new DbModel(appName);
         * model.fromJSON(dbInfo);
         * 
         * logger.info(model.toJSON().toString(2));
         */
    }

    public void dropTable() throws Exception {
        var con = this.ds.getConnection();
        var st = con.createStatement();
        String sql = "DROP TABLE test_1 ";

        st.execute(sql);
        con.commit();

    }

    public void createTable() throws Exception {
        var con = this.ds.getConnection();
        var st = con.createStatement();

        String sql = "CREATE TABLE test_1 ( " +
                "id_1 int NOT NULL," +
                "id_2 int NOT NULL," +
                "PRIMARY KEY (id_1,id_2)" +
                ")";
        st.execute(sql);
        con.commit();
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
