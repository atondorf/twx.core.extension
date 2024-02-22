package twx.core.db.handler;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.thingworx.types.BaseTypes;

import twx.core.db.model.DbTypeCategory;

public class DbInfo {
    public  static final Integer DEFAULT_STRING_LENGTH = 255;
    public  static final Integer MAX_STRING_LENGHT = -1;

    private final Set<String> systemSchemas = new HashSet<>();
    private final Set<String> systemTables = new HashSet<>();
    private String defaultSchema = null;
    private Map<BaseTypes, TypeMapEntry> twx2sqlMap = new HashMap<>();
    private Map<JDBCType, TypeMapEntry>  sql2twxMap = new HashMap<>();
    
    // region Schema Info ... 
    // -------------------------------------------------------------------------------- 
    public void addSystemSchema(String schema) {
        systemSchemas.add(schema);
    }

    public Boolean isSystemSchema(String schema) {
        String upperSchema = schema.toUpperCase();
        return this.systemSchemas.contains(upperSchema);
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setDefaultSchema(String schema) {
        this.defaultSchema = schema;
    }

    public Boolean isDefaultSchema(String schema) {
        return schema.equals(this.defaultSchema);
    }

    public void addSystemTable(String schema) {
        systemTables.add(schema);
    }

    public Boolean isSystemTable(String table) {
        String upperTable = table.toUpperCase();
        return this.systemTables.contains(upperTable); 
    }
    // endregion Schema Info
    // region Type Conversion ... 
    // --------------------------------------------------------------------------------  
    public class TypeMapEntry {
        public BaseTypes twxType;
        public JDBCType jdbcType;
        public String dbType;
        public Integer size;
        public DbTypeCategory typeCat;

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType, String nativeType, DbTypeCategory catgory) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.dbType = nativeType;
            this.typeCat = catgory;
            this.size = 0;
        }

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType, String nativeType, DbTypeCategory catgory, Integer size) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.typeCat = catgory;
            this.size = size;
        }
    }

    public void registerBiType(TypeMapEntry entry) {
        twx2sqlMap.putIfAbsent(entry.twxType, entry);
        sql2twxMap.putIfAbsent(entry.jdbcType, entry);
    }

    public void registerTwxType(TypeMapEntry entry) {
        twx2sqlMap.putIfAbsent(entry.twxType, entry);
    }

    public void registerJdbcType(TypeMapEntry entry) {
        sql2twxMap.putIfAbsent(entry.jdbcType, entry);
    }

    public BaseTypes jdbc2Base(JDBCType jdbcType) {
        BaseTypes type = BaseTypes.NOTHING;
        if( sql2twxMap.containsKey(jdbcType) )
            type = sql2twxMap.get(jdbcType).twxType;
        return type;
    }


    // endregion Type Conversion ... 

}
