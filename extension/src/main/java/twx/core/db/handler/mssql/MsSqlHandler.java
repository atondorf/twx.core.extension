package twx.core.db.handler.mssql;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;

import com.thingworx.types.BaseTypes;

import twx.core.db.handler.ConnectionManager;
import twx.core.db.handler.ModelManager;
import twx.core.db.handler.DbInfo;
import twx.core.db.handler.DbInfo.TypeMapEntry;
import twx.core.db.handler.SQLBuilder;
import twx.core.db.handler.TransactionManager;
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
        // system & liquibase tables ...
        info.addSystemTable("DATABASECHANGELOG");
        info.addSystemTable("DATABASECHANGELOGLOCK");

        /*
         * Unsupported, yet
         * TIMESPAN((byte)4, "Timespan"),
         * INFOTABLE((byte)5, "InfoTable"),
         * LOCATION((byte)6, "Location"),
         * QUERY((byte)9, "Query"),
         * SCHEDULE((byte)17, "Schedule"),
         * VARIANT((byte)18, "Variant"),
         * VEC2((byte)124, "Vec2"),
         * VEC3((byte)125, "Vec3"),
         * VEC4((byte)126, "Vec4"),
         * PASSWORD((byte)13, "Password"),
         * THINGCODE((byte)127, "ThingCode"),
         */

        // Type Mapping TWX <=> SQL 
        info.registerBiType(info.new TypeMapEntry(BaseTypes.NOTHING, JDBCType.NULL, "", DbTypeCategory.NULL));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.BOOLEAN, JDBCType.BIT, "bit", DbTypeCategory.NUMERIC));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.INTEGER, JDBCType.INTEGER, "int", DbTypeCategory.NUMERIC));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.LONG, JDBCType.BIGINT, "bigint", DbTypeCategory.NUMERIC));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.NUMBER, JDBCType.DOUBLE, "float", DbTypeCategory.NUMERIC));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.STRING, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.MAX_STRING_LENGHT));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.DATETIME, JDBCType.TIMESTAMP, "datetime2", DbTypeCategory.DATETIME));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.BLOB, JDBCType.VARBINARY, "varbinary", DbTypeCategory.BINARY));
        info.registerBiType(info.new TypeMapEntry(BaseTypes.XML, JDBCType.LONGNVARCHAR, "xml", DbTypeCategory.OTHER));

        // additional Types from SQL => Thingworx only ...
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.INTEGER, JDBCType.TINYINT, "tinyint", DbTypeCategory.NUMERIC));
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.INTEGER, JDBCType.SMALLINT, "smallint", DbTypeCategory.NUMERIC));
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.NUMBER, JDBCType.REAL, "real", DbTypeCategory.NUMERIC));
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.NUMBER, JDBCType.DECIMAL, "decimal", DbTypeCategory.NUMERIC));
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.STRING, JDBCType.NCHAR, "decimal", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerJdbcType(info.new TypeMapEntry(BaseTypes.BLOB, JDBCType.BINARY, "tinyint", DbTypeCategory.BINARY));

        // Additional Types, that map from TWX => SQL
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.IMAGELINK, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.HYPERLINK, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.HTML, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.MAX_STRING_LENGHT));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.TAGS, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.MAX_STRING_LENGHT));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.GUID, JDBCType.CHAR, "uniqueidentifier", DbTypeCategory.OTHER));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.TEXT, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.MAX_STRING_LENGHT));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.JSON, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.MAX_STRING_LENGHT));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.IMAGE, JDBCType.BLOB, "varbinary", DbTypeCategory.BINARY));

        // Mapp all types of BaseType ... Name to varchar ...TWX => SQL
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PROPERTYNAME, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.SERVICENAME, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.EVENTNAME, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGGROUPNAME, JDBCType.NVARCHAR, "nvarchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGSHAPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.THINGTEMPLATENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DATASHAPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MASHUPNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MENUNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.BASETYPENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.USERNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.GROUPNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.CATEGORYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STATEDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STYLEDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MODELTAGVOCABULARYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DATATAGVOCABULARYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NETWORKNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.MEDIAENTITYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.APPLICATIONKEYNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.LOCALIZATIONTABLENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.ORGANIZATIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.DASHBOARDNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PERSISTENCEPROVIDERPACKAGENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PERSISTENCEPROVIDERNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.PROJECTNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NOTIFICATIONCONTENTNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.NOTIFICATIONDEFINITIONNAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
        info.registerTwxType(info.new TypeMapEntry(BaseTypes.STYLETHEMENAME, JDBCType.NVARCHAR, "varchar", DbTypeCategory.TEXTUAL, DbInfo.DEFAULT_STRING_LENGTH));
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
    public SQLBuilder getSqlBuilder() {
        return new MsSqlSQLBuilder(this);
    }

    // endregion
    // region DSL Handler ...
    // --------------------------------------------------------------------------------

    // endregion

}
