package twx.core.db.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementUtil {
    private static String   REGEX = "(@\\w*)";
    private static Pattern  PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);

    private final List<String> fields = new ArrayList<>();
    private String originalSQL = "";
    private String parsedSQL = "";

    public StatementUtil(String sql) {
        this.originalSQL = sql;
        this.parsedSQL = this.parseSQL(sql);
    }

    protected String parseSQL(String sql) {
        final Matcher m = PATTERN.matcher(sql);
        while (m.find()) {
            String grp = m.group();
            fields.add( grp.substring(1));
        };
        return m.replaceAll("?");
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        // 
        json.put("originalSQL", this.originalSQL);
        json.put("parsedSQL", this.parsedSQL);
        // write Fields ... 
        var array = new JSONArray();
        for (var field : this.fields) {
            array.put(field);
        }
        json.put( "Fields", array);
        return json;
    }

}
