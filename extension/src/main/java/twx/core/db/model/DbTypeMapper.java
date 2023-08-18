package twx.core.db.model;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.sql.JDBCType;

import com.thingworx.connectors.ParameterDefinition.In;
import com.thingworx.types.BaseTypes;

public class DbTypeMapper {

    public class TypeMapEntry {

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.defaultLength = 0;
        }

        public TypeMapEntry(BaseTypes twxType, JDBCType jdbcType, Integer defaultLength) {
            this.twxType = twxType;
            this.jdbcType = jdbcType;
            this.defaultLength = defaultLength;
        }

        public BaseTypes twxType;
        public JDBCType jdbcType;
        public Integer defaultLength;
    }

    public static TypeMapEntry get(BaseTypes twxType) {
        return twx2sqlMap.get(twxType);
    }

    public static TypeMapEntry get(JDBCType sqlType) {
        return sql2twxMap.get(sqlType);
    }

    private static Map<BaseTypes, TypeMapEntry> twx2sqlMap;
    private static Map<JDBCType, TypeMapEntry>  sql2twxMap;

    private static void buildMaps() {
        twx2sqlMap = new HashMap<BaseTypes,TypeMapEntry>();
        sql2twxMap = new HashMap<JDBCType,TypeMapEntry>();
    }


}
