package twx.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCMeta {
    final static Logger logger = LoggerFactory.getLogger(JDBCMeta.class);

    protected Connection con;
    protected DatabaseMetaData meta;

    JDBCMeta(Connection con) throws SQLException {
        this.con = con;
        this.meta = con.getMetaData();
    }


    public void getDatabaseInfo() throws SQLException {
        logger.info( "Product_Name: " + meta.getDatabaseProductName() );
        logger.info( "Driver_Name: " + meta.getDriverName() );
        logger.info( "Catalog_Name: " + con.getCatalog() );
    }

    public void getTables() throws SQLException {
        try (ResultSet resultSet = this.meta.getTables(null, null, null, new String[] { "TABLE" })) {
            while (resultSet.next()) {
                logger.info( "Tablename: " + resultSet.getString("TABLE_NAME") );
                logger.info( "TablenRemarks: " + resultSet.getString("REMARKS") );
            }
        }
    }



}
