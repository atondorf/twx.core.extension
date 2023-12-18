package twx.core.db.handler;

import java.sql.Connection;

import com.thingworx.logging.LogUtilities;

import ch.qos.logback.classic.Logger;

public class TransactionManager {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(TransactionManager.class);

    private ConnectionManager connectionManager = null;

    public TransactionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    
}
