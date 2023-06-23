package twx.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import twx.core.db.model.DbModel;

public interface IDatabaseHandler {

    DbModel queryModelFromDB() throws SQLException;

    Boolean isSystemSchema(String schemaName);

}