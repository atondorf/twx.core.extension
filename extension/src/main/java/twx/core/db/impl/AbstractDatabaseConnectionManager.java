package twx.core.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ch.qos.logback.classic.Logger;
import twx.core.db.ConnectionManager;

import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.database.AbstractDatabase;
import com.thingworx.logging.LogUtilities;

public class AbstractDatabaseConnectionManager extends DataSourceConnectionManager {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(AbstractDatabaseConnectionManager.class);

    protected AbstractDatabase abstractDatabase = null;
    protected String  catalog = null;

    public AbstractDatabaseConnectionManager(AbstractDatabase abstractDatabase) throws Exception {
        super(abstractDatabase.getDataSource());
        this.abstractDatabase = abstractDatabase;
    }

    @Override
    public AbstractDatabase getAbstractDatabase() {
        return this.abstractDatabase;
    }

    @Override
    public Connection getConnection() {
        try {
            Connection connection = abstractDatabase.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (Exception ex) {
            _logger.error("Error getting database connection", ex);
        }
        return null;
    }

    public void close(Connection connection) {
        if (connection == null)
            return;
        try {
            if (connection.isClosed())
                return;
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException ex) {
            _logger.error("Error closing connection", ex);
        }
    }

    public void commit(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.commit();
        } catch (Exception ex) {
            _logger.error("Error in commit", ex);
            rollback(connection);
        }
    }

    public void rollback(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.rollback();
        } catch (SQLException ex) {
            _logger.error("Error in rollback", ex);
        }
    }

}
