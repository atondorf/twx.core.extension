package twx.core.db.handler.generic;

import java.sql.Connection;

import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db.handler.DDLBuilder;
import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.SQLBuilder;

public abstract class GenericHandler implements DbHandler {
    private ConnectionManager   conncetionManager = null;
    private TransactionManager  transactionManager = null;

    // region Database Handler ... 
    // --------------------------------------------------------------------------------  
    public Connection getConnection() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getApplication() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCatalog() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLBuilder getSqlBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public ConnectionManager getConnectionManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public TransactionManager getTransactionManager() {
        // TODO Auto-generated method stub
        return null;
    }
    // endregion 
    // region DDL Handler ... 
    // --------------------------------------------------------------------------------  
    @Override
    public DbInfo getDbInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DDLBuilder getDdlBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DDLReader getDdlReader() {
        // TODO Auto-generated method stub
        return null;
    }
    // endregion 

}
