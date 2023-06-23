package twx.core.db.imp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class MsSQLDatabaseHandler extends AbstractDatabaseHandler {

    public MsSQLDatabaseHandler(Connection conn) {
        super(conn);
        String[] MSSQL = {
                "DB_ACCESSADMIN",
                "DB_BACKUPOPERATOR",
                "DB_DATAREADER",
                "DB_DATAWRITER",
                "DB_DDLADMIN",
                "DB_DENYDATAREADER",
                "DB_DENYDATAWRITER",
                "DB_OWNER",
                "DB_SECURITYADMIN",
                "GUEST",
                "INFORMATION_SCHEMA",
                "SYS" };
    }

}
