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

import com.thingworx.things.database.AbstractDatabase;

public class AbstractDatabaseHandler implements IDatabaseHandler {
    final static Logger logger = LoggerFactory.getLogger(AbstractDatabaseHandler.class);

    private String              application;
    private AbstractDatabase    abstractDatabase;
    private Connection          conn;
    private DatabaseMetaData    meta;

    public AbstractDatabaseHandler(Connection conn, String application) throws Exception {
        this.application = application;
        this.abstractDatabase = null;
        this.conn = conn;
    }

    public AbstractDatabaseHandler(AbstractDatabase dbThing, String application) throws Exception {
        this.application = application;
        this.abstractDatabase = dbThing;
        this.conn = dbThing.getConnection();
    }
    
    @Override
    public AbstractDatabase getAbstractDatabase() throws Exception {
        return abstractDatabase;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.conn;
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.conn.getCatalog();
    }

    @Override
    public String getApplication() throws SQLException {
        return this.application;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        if( this.meta == null )
            this.meta = this.conn.getMetaData();
        return this.meta;
    }

    @Override
    public DbModel queryModel() throws SQLException {
        return DBModelManager.queryModel( this );
    }

    @Override
    public DbModel getModel()  {
        return DBModelManager.getDBModel(this.application);
    }

    @Override
    public Boolean isSystemSchema(String schemaName) {
        return false;
    }

    @Override
    public Integer createSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer dropSchema() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    // endregion
    // region Model & Metadata Management ...
    // --------------------------------------------------------------------------------
    


    // endregion
}
