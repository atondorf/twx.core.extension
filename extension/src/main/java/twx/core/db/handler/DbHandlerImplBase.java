package twx.core.db.handler;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.things.database.AbstractDatabase;

import ch.qos.logback.classic.spi.PlatformInfo;
import twx.core.db.model.DbModel;

public abstract class DbHandlerImplBase implements DbHandler {
    final static Logger logger = LoggerFactory.getLogger(DbHandlerImplBase.class);

    protected AbstractDatabase      abstractDatabase;
    protected DataSource            dataSource;
    protected String                catalog;
    protected String                appliction;
    protected DbHandlerInfo         info = new DbHandlerInfo();
    protected DbModel               dbModel = new DbModel();
    protected JdbcSqlBuilder        builder;
    protected JdbcModelManager      modelReader;
    protected String                dbThingName;   

    public DbHandlerImplBase(DataSource source) throws Exception {
        this.abstractDatabase = null;
        this.dataSource = source;
        this.initialize();
    }

    public DbHandlerImplBase(AbstractDatabase dbThing) throws Exception {
        this.abstractDatabase = dbThing;
        this.dataSource = null;
        this.initialize();        
    }

    public void initialize() {

    }

    public Boolean isAbstractDatabase() {
        return this.abstractDatabase != null;
    }

    public String getApplication() {
        return this.appliction;
    }

    public void setAppliction(String appliction) {
        this.appliction = appliction;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
        this.dbModel.setName(catalog);
    }
    
    public DbHandlerInfo getHandlerInfo() {
        return this.info;
    }

    public JdbcSqlBuilder getSqlBuilder() {
        if (this.builder == null) {
            this.builder = new JdbcSqlBuilder(this);
        }
        return this.builder;
    }

    protected void setSqlBuilder(JdbcSqlBuilder builder) {
        this.builder = builder;
    }
    // enregion
    // region Model Management ...
    // --------------------------------------------------------------------------------
    public JdbcModelManager getModelManager() {
        if (this.modelReader == null) {
            this.modelReader = new JdbcModelManager(this);
        }
        return this.modelReader;
    }

    public void setModelManager(JdbcModelManager manager) {
        this.modelReader = manager;
    }

    public DbModel getModel() {
        return this.dbModel;
    }

    public DbModel queryModel() throws Exception {
        Connection con = null;
        try {
            con = this.getConnection();
            JdbcModelManager modelMgr = this.getModelManager();
            DbModel dbModel = modelMgr.queryModel(con);
            return dbModel;
        }
        finally {
            if( con != null )
                con.close();
        }
    }

    // enregion
    // region AbstractDatabase Management ...
    // --------------------------------------------------------------------------------
    public AbstractDatabase getAbstractDatabase() {
        return this.abstractDatabase;
    }

    public DataSource getDataSource() throws Exception {
        if (this.abstractDatabase != null)
            return this.abstractDatabase.getDataSource();
        if (this.dataSource != null)
            return this.dataSource;
        return null;
    }

    public Connection getConnection() throws Exception {
        if (this.abstractDatabase != null)
            return this.abstractDatabase.getConnection();
        return this.dataSource.getConnection();
    }

    public boolean isConnected() throws Exception {
        if (this.abstractDatabase != null)
            return this.abstractDatabase.isConnected();
        return this.dataSource != null;
    }

    public void beginTransaction() throws Exception {
        if (this.abstractDatabase != null)
            this.abstractDatabase.beginTransaction();
    }

    public void endTransaction(Connection conn) throws Exception {
        if (this.abstractDatabase != null)
            this.abstractDatabase.endTransaction(conn);
        if ( !conn.isClosed() )
            conn.close();
    }

    public void commit(Connection conn) throws Exception {
        if (this.abstractDatabase != null)
            this.abstractDatabase.commit(conn);
        else if (conn != null)
            conn.commit();
    }

    public void rollback(Connection conn) throws Exception {
        if (this.abstractDatabase != null)
            this.abstractDatabase.rollback(conn);
        else if (conn != null)
            conn.rollback();
    }
    // endregion
}
