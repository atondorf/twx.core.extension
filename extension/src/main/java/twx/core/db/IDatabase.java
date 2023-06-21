package twx.core.db;

import java.sql.SQLException;

import twx.core.db.model.DbModel;

public interface IDatabase {

    DbModel queryModelFromDB() throws SQLException;

}