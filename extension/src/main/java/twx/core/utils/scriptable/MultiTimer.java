package twx.core.utils.scriptable;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

public class MultiTimer extends ScriptableObject {
    private static final long serialVersionUID = 1L; 
   
    private JSONArray   array = new JSONArray();

    // The zero-argument constructor used by Rhino runtime to create instances
    public MultiTimer() {
        this.push("Start");
    };
    // @JSConstructor annotation defines the JavaScript constructor
    @JSConstructor
    public MultiTimer(String desc) { 
        this.push(desc);
    }    
    // The class name is defined by the getClassName method
    @Override
    public String getClassName() { return "MultiTimer"; }

    @JSFunction
    public void push(String desc) {
        var json    = new JSONObject();
        var now     = new Date();
        long elapsed = (long)0;
        if( this.array.length() > 0 ) {
            elapsed = now.getTime() - this.array.getJSONObject(this.array.length() - 1 ).getLong("TS");
        }
        json.put("TS", now.getTime() );
        json.put("Elapsed", elapsed );
        json.put("Desc", desc );
        array.put(json);
    }

    @JSFunction
    public void reset() {
        array = new JSONArray();
        this.push("Reset");
    }

    @JSFunction
    public JSONArray getArray() {
        return array;
    }

    @JSFunction
    public String toString() {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String str = "";
        long totalElapsed = 0;
        for (int i = 0; i < this.array.length(); i++) {
            var obj  = this.array.getJSONObject(i);
            var ts   = new Date( obj.getLong("TS") );
            var el   = obj.getLong("Elapsed");
            str += obj.getString("Desc") + " - " + sdf.format( ts ) + " - " + el + " ms\n";
            totalElapsed += el;
        }
        str += "Total-Elapsed: " + totalElapsed + " ms\n";
        return str;
    }
}
