package twx.core.db.imp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twx.core.db.IDatabaseHandler;
import twx.core.db.model.*;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDatabaseHandler implements IDatabaseHandler {
    final static Logger logger = LoggerFactory.getLogger(AbstractDatabaseHandler.class);

    private Connection      conn;
    private List<String>    systemSchemas;
    private List<String>    systemTables;

    public AbstractDatabaseHandler(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Boolean isSystemSchema(String schemaName) {
        // TODO Auto-generated method stub
        return false;
    }

    // endregion
    // region Model & Metadata Management ... 
    // --------------------------------------------------------------------------------
    @Override
    public DbModel queryModelFromDB() throws SQLException {
        return DBModelManager.queryDBModel(conn);
    }

    // endregion 
}
