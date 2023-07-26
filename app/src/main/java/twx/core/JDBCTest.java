package twx.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class JDBCTest {
    static final String DB_URL = "jdbc:sqlserver://localhost:1433;database=twsys;";
    static final String USER = "twx";
    static final String PASS = "twx@1234";
    Connection con = null;

    public JDBCTest() {
        try { 
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            this.con = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            printSQLException(e);
        }
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

    public void open() {
        ResultSet resultSet = null;
        try {
            Statement statement = this.con.createStatement();

            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT * FROM thing_model";
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void test_1() {
        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();

        DbTable customerTable = schema.addTable("test");
        customerTable.addColumn("UID", "bigint", null);
        customerTable.addColumn("cust_id", "number", null);
        customerTable.addColumn("name", "varchar", 255);
        customerTable.primaryKey("Customer_UID_PK", "UID");
        customerTable.unique("Customer_UID_UN", "UID");

        String createCustomerTable = new CreateTableQuery(customerTable, true).validate().toString();

        System.out.println("Cause: " + createCustomerTable);
    }

}
