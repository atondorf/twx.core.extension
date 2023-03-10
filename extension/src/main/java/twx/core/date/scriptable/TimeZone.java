package twx.core.date.scriptable;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TimeZone extends ScriptableObject {
    
    public DateTimeZone timeZone;

    @Override
    public String getClassName() {
        return "TimeZone";
    } 

    // The zero-argument constructor used by Rhino runtime to create instances
    @JSConstructor    
    public TimeZone() { 
        this.timeZone = DateTimeZone.getDefault();
    };

    // @JSConstructor annotation defines the JavaScript constructor
    @JSConstructor
    public TimeZone(String id) { 
        this.timeZone = DateTimeZone.forID( id );
    }
    
}
