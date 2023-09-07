package twx.core.db.handler.mssql;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;

import com.thingworx.types.BaseTypes;

import twx.core.db.ConnectionManager;
import twx.core.db.TransactionManager;
import twx.core.db.handler.DDLBuilder;
import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.handler.impl.AbstractHandler;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTypeCategory;

public class MsSqlHandler extends AbstractHandler {
    public static final String DATABASENAME = "MsSql";
    public static final String DATABASE_KEY = "sqlserver";
    public static final String DATABASE_DEFAULT_SCHEMA = "dbo";

    public MsSqlHandler(ConnectionManager connectionManager) throws Exception {
        super(connectionManager);
    }

    @Override
    public void initialize() {
        super.initialize();
        
        // initialise the DbInfos, Typemaps & Co. 
        DbInfo info = this.getDbInfo();
        // set the default schema ... 
        info.setDefaultSchema(DATABASE_DEFAULT_SCHEMA);
        // system Schemas ...
        info.addSystemSchema("DB_ACCESSADMIN");
        info.addSystemSchema("DB_DATAREADER");
        info.addSystemSchema("DB_BACKUPOPERATOR");
        info.addSystemSchema("DB_DATAWRITER");
        info.addSystemSchema("DB_DDLADMIN");
        info.addSystemSchema("DB_DENYDATAREADER");
        info.addSystemSchema("DB_DENYDATAWRITER");
        info.addSystemSchema("DB_OWNER");
        info.addSystemSchema("DB_SECURITYADMIN");
        info.addSystemSchema("GUEST");
        info.addSystemSchema("INFORMATION_SCHEMA");
        info.addSystemSchema("SYS");
        // Type Mapping ...
 
        // Mapp all types of BaseType ...Name to varchar ... 
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PROPERTYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.SERVICENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.EVENTNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGGROUPNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGSHAPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGTEMPLATENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DATASHAPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MASHUPNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MENUNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.BASETYPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.USERNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.GROUPNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.CATEGORYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STATEDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STYLEDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MODELTAGVOCABULARYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DATATAGVOCABULARYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NETWORKNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MEDIAENTITYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.APPLICATIONKEYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.LOCALIZATIONTABLENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.ORGANIZATIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DASHBOARDNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PERSISTENCEPROVIDERPACKAGENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PERSISTENCEPROVIDERNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PROJECTNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NOTIFICATIONCONTENTNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NOTIFICATIONDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STYLETHEMENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH) );

/*
 * // bidirectional types ... 
        registerBiType( new TypeMapEntry(BaseTypes.NOTHING, JDBCType.NULL, "", DbTypeCategory.NULL) );
/*
        registerBiType(BaseTypes.STRING, JDBCType.NVARCHAR, MAX_STRING_LENGHT, DbTypeCategory.TEXTUAL);
        registerBiType(BaseTypes.NUMBER, JDBCType.DOUBLE, 0, DbTypeCategory.NUMERIC);
        registerBiType(BaseTypes.INTEGER, JDBCType.INTEGER, 0, DbTypeCategory.NUMERIC);
        registerBiType(BaseTypes.LONG, JDBCType.BIGINT, 0, DbTypeCategory.NUMERIC);
        registerBiType(BaseTypes.BOOLEAN, JDBCType.BOOLEAN, 0, DbTypeCategory.NUMERIC);
        registerBiType(BaseTypes.DATETIME, JDBCType.TIMESTAMP, 0, DbTypeCategory.DATETIME);

        registerBiType(BaseTypes.TEXT, JDBCType.LONGVARCHAR, MAX_STRING_LENGHT, DbTypeCategory.TEXTUAL);

        /*
        registerType(BaseTypes.IMAGE, JDBCType.NULL, 0, DbTypeCategory.NULL);

        registerType(BaseTypes.INFOTABLE, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.LOCATION, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.XML, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.JSON, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.QUERY, JDBCType.NULL, 0, DbTypeCategory.NULL);

        registerType(BaseTypes.HYPERLINK, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.IMAGELINK, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.PASSWORD, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.HTML, JDBCType.NULL, 0, DbTypeCategory.NULL);

        registerType(BaseTypes.TAGS, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.SCHEDULE, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.VARIANT, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.GUID, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.BLOB, JDBCType.NULL, 0, DbTypeCategory.NULL);

        registerType(BaseTypes.VEC2, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.VEC3, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.VEC4, JDBCType.NULL, 0, DbTypeCategory.NULL);
        registerType(BaseTypes.THINGCODE, JDBCType.NULL, 0, DbTypeCategory.NULL);

        // all the NAME Types => NVARCHAR(255)

        // registerType(BaseTypes.TIMESPAN, JDBCType.NULL, 0, DbTypeCategory.NULL);
 */


        // create the DB-Model ... 
        this.setDbModel( new DbModel("Test") );
    }

    // region TWX-Services Metadata Database ...
    // --------------------------------------------------------------------------------
    @Override
    public String getName() {
        return DATABASENAME;
    }

    @Override
    public String getKey() {
        return DATABASE_KEY;
    }
    
    @Override
    public String getDefaultCatalog() {
        return getConnectionManager().getCatalog();
    }

    @Override
    public String getDefaultSchema() {
        return DATABASE_DEFAULT_SCHEMA;
    }

    // endregion
    // region Connections & Transactions ...
    // --------------------------------------------------------------------------------


    // endregion

    // region DDL Handler ...
    // --------------------------------------------------------------------------------
    @Override
    public DDLBuilder getDDLBuilder() {
        return new MsSqlDDLBuilder(this);
    }

    @Override
    public DDLReader getDDLReader() {
        return new MsSqlDDLReader(this);
    }

    @Override
    public SQLBuilder getSqlBuilder() {
        return new MsSqlSQLBuilder(this);
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------
    
    // endregion

}
