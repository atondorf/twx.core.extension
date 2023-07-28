package twx.core.date.scriptable;

import org.joda.time.DateTimeZone;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;

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

    @JSFunction
    public String test_toString(Object param) {
        return param.toString();
    }

    @JSFunction
    public String test_className(Object param) {
        return param.getClass().getName();
    }

    @JSFunction
    public String test_simpleName(Object param) {
        return param.getClass().getSimpleName();
    }

    @JSFunction
    public String test_format(Object param) {
		/*
	        if( param instanceof IdScriptableObject ) {
	            IdScriptableObject obj = (IdScriptableObject)param;
	
	        }
		*/
        return param.getClass().getSimpleName();
    }

    
    
}
