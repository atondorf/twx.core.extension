package twx.core.utils;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import com.thingworx.dsl.utils.ValueConverter;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;
import org.json.JSONArray;

public class SQLBuilder extends ScriptableObject {

    @Override
    public String getClassName() {
        return "SQLBuilder";
    }

    @JSFunction
    public String getObjClassName(Object obj) {
        return obj.getClass().getName();
    }

    @JSFunction
    public String getObjToString(Object obj) {
        return obj.toString();
    }

}
