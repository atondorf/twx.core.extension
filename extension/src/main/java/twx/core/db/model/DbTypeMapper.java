package twx.core.db.model;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.sql.JDBCType;

import com.thingworx.types.BaseTypes;

public abstract class DbTypeMapper {

    public class TypeMapEntry {

        public BaseTypes        twxType;
        public JDBCType         jdbcType;
        public String           nativeType;
        public Integer          size;
        public DbTypeCategory   typeCat;

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType, String nativeType, DbTypeCategory catgory) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.typeCat = catgory;
            this.size = 0;
        }

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType, DbTypeCategory catgory, Integer size) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.typeCat = catgory;
            this.size = size;
        }
    }

    private static final Integer DEFAULT_STRING_LENGTH = 255;
    private static final Integer MAX_STRING_LENGHT = -1;
    private static Map<BaseTypes, TypeMapEntry> twx2sqlMap;
    private static Map<JDBCType, TypeMapEntry> sql2twxMap;

    static {
        // bidirectional types ... 
        registerBiType(BaseTypes.NOTHING, JDBCType.NULL, 0, DbTypeCategory.NULL);
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
*/
        // all the NAME Types => NVARCHAR(255)
        registerTwxType(BaseTypes.PROPERTYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.SERVICENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.EVENTNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.THINGGROUPNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.THINGNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.THINGSHAPENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.THINGTEMPLATENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.DATASHAPENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.MASHUPNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.MENUNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.BASETYPENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.USERNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.GROUPNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.CATEGORYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.STATEDEFINITIONNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.STYLEDEFINITIONNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.MODELTAGVOCABULARYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.DATATAGVOCABULARYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.NETWORKNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.MEDIAENTITYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.APPLICATIONKEYNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.LOCALIZATIONTABLENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.ORGANIZATIONNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.DASHBOARDNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.PERSISTENCEPROVIDERPACKAGENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.PERSISTENCEPROVIDERNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.PROJECTNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.NOTIFICATIONCONTENTNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.NOTIFICATIONDEFINITIONNAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);
        registerTwxType(BaseTypes.STYLETHEMENAME, JDBCType.NVARCHAR, DEFAULT_STRING_LENGTH, DbTypeCategory.TEXTUAL);


        // registerType(BaseTypes.TIMESPAN, JDBCType.NULL, 0, DbTypeCategory.NULL);
    }

    protected static void registerBiType(BaseTypes twxType, JDBCType jdbcType, Integer defSize, DbTypeCategory catgory) {

    }

    protected static void registerTwxType(BaseTypes twxType, JDBCType jdbcType, Integer defSize, DbTypeCategory catgory) {

    }

    protected static void registerJdbcType(BaseTypes twxType, JDBCType jdbcType, Integer defSize, DbTypeCategory catgory) {

    }


}
