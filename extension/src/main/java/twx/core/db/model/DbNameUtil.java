package twx.core.db.model;

import java.util.List;

public class DbNameUtil {

    public static String of(final DbSchema schema, final String table) {
        return ofTable(schema.toString(), table);
    }

    public static String of(final DbTable table, final String column) {
        return table + "." + column;
    }

    public static String of(final DbEnum anEnum, final String value) {
        return anEnum + "." + value;
    }

    public static String ofTable(final String schema, final String table) {
        return schema.equals(DbSchema.DEFAULT_SCHEMA_NAME) ? table : schema + '.' + table;
    }

    public static String ofColumn(final String schema, final String table, final String column) {
        return ofTable(schema, table) + '.' + column;
    }

    public static String ofColumns(final String schema, final String table, final List<String> columns) {
        return ofTable(schema, table) + ".(" + String.join(", ", columns) + ')';
    }

}
