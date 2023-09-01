package twx.core.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.thingworx.logging.LogUtilities;
import com.thingworx.things.database.AbstractDatabase;

import ch.qos.logback.classic.Logger;
import twx.core.db.ConnectionManager;

public class DataSourceConnectionManager implements ConnectionManager {
    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DataSourceConnectionManager.class);

    protected DataSource dataSource = null;
    protected String catalog = null;

    public DataSourceConnectionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void queryMeta() {
        Connection connection = null;
        try {
            connection = this.getConnection();
            this.catalog = connection.getCatalog();
        } catch (SQLException ex) {
            _logger.error("Exception {}", ex.getMessage());
        } finally {
            this.close(connection);
        }
    }

    public String getCatalog() {
        if (this.catalog == null)
            this.queryMeta();
        return this.catalog;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public AbstractDatabase getAbstractDatabase() {
        return null;
    }

    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex) {
            _logger.error("Error getting database connection", ex);
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
