package twx.core.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.thingworx.logging.LogUtilities;

import ch.qos.logback.classic.Logger;
import twx.core.db.ConnectionManager;

public class DataSourceConnectionManager implements ConnectionManager {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(AbstractDatabaseConnectionManager.class);

    protected DataSource    dataSource;

    public DataSourceConnectionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        try {
            if ( _logger.isDebugEnabled() )
                _logger.debug("Try to get database Connection.");
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            if (_logger.isDebugEnabled())
                _logger.debug("Connection to database acquire.");
            return connection;
        } catch (SQLException e) {
            _logger.error("Error getting database connection", e);
            return null;
        }
    }

    public void close(Connection connection) {
        if (connection == null)
            return;
        try {
            if (connection.isClosed())
                return;
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            _logger.error("Error closing connection", e);
        }
    }

    public void commit(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.commit();
        } catch (Exception e) {
            _logger.error("Error in commit", e);
            rollback(connection);
        }
    }

    public void rollback(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.rollback();
        } catch (SQLException e) {
            _logger.error("Error in rollback", e);
        }
    }

}
