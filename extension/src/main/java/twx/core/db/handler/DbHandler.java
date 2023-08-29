package twx.core.db.handler;

import java.sql.Connection;

import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db2.handler.DbHandlerInfo;
import twx.core.db2.handler.JdbcSqlBuilder;

public interface DbHandler {
    
    // region TWX-Services Metadata Database ... 
    // --------------------------------------------------------------------------------
    public String getName();

    public String getKey();

    public String getCatalog();

    public String getApplication();
    // endregion

    // region Connections & Transactions ... 
    // --------------------------------------------------------------------------------  
    public ConnectionManager getConnectionManager();

    public TransactionManager getTransactionManager();

    public Connection getConnection() throws Exception;
    // endregion 

    // region DDL Handler ... 
    // --------------------------------------------------------------------------------  
    public DbInfo getDbInfo();

    public DDLBuilder getDdlBuilder();

    public DDLReader  getDdlReader();
    // endregion 
    // region DSL Handler ... 
    // --------------------------------------------------------------------------------  
    public SQLBuilder getSqlBuilder();

    // endregion 

}
