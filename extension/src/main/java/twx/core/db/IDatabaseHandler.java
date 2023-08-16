package twx.core.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.thingworx.things.database.AbstractDatabase;

import twx.core.db.model.DbModel;

public interface IDatabaseHandler {

    AbstractDatabase getAbstractDatabase() throws Exception;

    Connection getConnection() throws SQLException;

    DatabaseMetaData getMetaData() throws SQLException;

    String getCatalog() throws SQLException;

    String getApplication() throws SQLException;

    DbModel queryModel() throws SQLException;

    DbModel getModel() throws SQLException;

    Boolean isSystemSchema(String schemaName) throws SQLException;

    Integer createSchema(String schemaName) throws SQLException;

    Integer dropSchema(String schemaName) throws SQLException;

}