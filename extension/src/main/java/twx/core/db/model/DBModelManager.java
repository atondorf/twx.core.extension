package twx.core.db.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import twx.core.concurrency.imp.AtomicManager;

public class DBModelManager {

    // the singleton instance ...
    private static final DBModelManager SINGLETON = new DBModelManager();

    private static final ConcurrentMap<String, DbModel> modelMap = new ConcurrentHashMap<String, DbModel>();

    public static DbModel getDBModelCached(String catalog) {
        DbModel model = modelMap.get(catalog);
        if (model == null) {
            model = modelMap.computeIfAbsent(catalog, k -> new DbModel(catalog) );
        }
        return model;
    }

    public static DbModel queryDBModel(Connection con) throws SQLException {
        DatabaseMetaData meta   = con.getMetaData();
        String dbName           = con.getCatalog();
        String productName      = meta.getDatabaseProductName();
        String productVersion   = meta.getDatabaseProductVersion();
        String driverName       = meta.getDriverName();
        String driverVersion    = meta.getDriverVersion();
        DbModel dbModel         = new DbModel(dbName);
        // return this.addSchemas(dbModel);
        return dbModel;
    }
}
