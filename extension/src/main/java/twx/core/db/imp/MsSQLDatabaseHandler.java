package twx.core.db.imp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thingworx.things.database.AbstractDatabase;

public class MsSQLDatabaseHandler extends AbstractDatabaseHandler {

    static class DBConstants {
        
        protected Set<String>    systemSchemas;

        public DBConstants() {
            String[] MS_SYS_SCHEMAS = {
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
                "SYS" 
            };  
            systemSchemas = new HashSet<>(Arrays.asList(MS_SYS_SCHEMAS) );
        }
    }

    final static DBConstants DBCONST = new DBConstants();

    public MsSQLDatabaseHandler(AbstractDatabase dbThing, String appName ) throws Exception {
        super(dbThing, appName );
    }

    public MsSQLDatabaseHandler(Connection connection, String appName) throws Exception {
        super(connection, appName );
    }

    @Override
    public Boolean isSystemSchema(String schemaName) {
        String upperSchema = schemaName.toUpperCase();
        Boolean contains = DBCONST.systemSchemas.contains(upperSchema);
        return contains;
    }


}
