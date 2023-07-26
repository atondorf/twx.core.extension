/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package twx.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twx.core.db.IDatabaseHandler;
import twx.core.db.imp.MsSQLDatabaseHandler;
import twx.core.db.model.DbModel;

public class App {

    final static Logger logger = LoggerFactory.getLogger(App.class);

    static final String DB_URL = "jdbc:sqlserver://localhost:1433;database=twdata;";
    static final String USER = "twx";
    static final String PASS = "twx@1234";
    Connection con = null;

    public static void main(String[] args) {
        var app = new App();
        var scanner = new Scanner(System.in);
        logger.info("---------- Start-App ----------");
        Connection con = null;
        try {
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            IDatabaseHandler handler = new MsSQLDatabaseHandler(con, "TWX_DATA"); // MsSQLDatabaseHandler
            // app.queryMeta(handler);
           // app.queryModelFromDB(handler);
           app.createModelFromJSON();
/*             var model = handler.queryModel();
            logger.info(model.toJSON().toString(2));
            
*/            
        } catch (SQLException e) {
            printSQLException(e);
        } catch (Exception e) {
            logger.error("Exception: " + e.toString() );
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    printSQLException(e);
                }
            }
        }
        logger.info("---------- Exit-App ----------");
    }

    public void queryModelFromDB(IDatabaseHandler handler) throws SQLException {
        var model = handler.queryModel();
        logger.info(model.toJSON().toString(2));  
    }

    public void createModelFromJSON()  {
        JSONObject dbInfo = new JSONObject(
        "{\"name\":\"test\",\"description\":\"jon doe\"}"
        );
        var model = new DbModel("");
        model.fromJSON(dbInfo);

        logger.info(model.toJSON().toString(2));  
    }

    public void queryMeta(IDatabaseHandler handler) throws SQLException {
        ResultSet rs = handler.getMetaData().getIndexInfo(null, "dbo","NewTable", false, false);
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            if (name != null) {
                String colName = rs.getString("COLUMN_NAME");
                Boolean unique = !(rs.getBoolean("NON_UNIQUE"));
            }
        }
    }

    public void testJackson(IDatabaseHandler handler) {
        try {
            var model = handler.queryModel();
            String str = ""; // objectMapper.writeValueAsString(model);
            logger.info(str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
        }
        // ;
    }

    public Integer testParam(Object val) {
        logger.info("Classname: " + val.getClass().getName() );
        Integer ret = 0;
        if ( val instanceof Number ) {
            ret = ((Number)val).intValue();
        }
        return ret;
    }

    public static void printSQLException(SQLException ex) {
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
