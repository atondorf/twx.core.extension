package twx.core.imp;

import org.joda.time.DateTime;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.things.Thing;

public class JSArgUtils {

    public static DateTime getDateTime(Object[] args, int index, Scriptable scope) throws Exception {
        if ( !args[index].getClass().getName().endsWith("NativeDate") ) {  
            throw new Exception("Argument [" + index + "] must be of type Date ... ");
        }
        long epoch = (long)ScriptRuntime.toNumber( args[index] ); 
        return new DateTime(epoch);
    }

    public static String getString(Object[] args, int index, Scriptable scope) throws Exception {
        if ( ! ( args[index] instanceof String ) ) {  
            throw new Exception("Argument [" + index + "] must be of type String ... ");
        }
        return (String)args[index];
    }

    public static Double getDouble(Object[] args, int index, Scriptable scope) throws Exception {
        if( args[index] instanceof Double )
            return (Double)args[index];
        if( args[index] instanceof Long )
            return Double.valueOf((Long)args[index] );
        if( args[index] instanceof Integer )
            return Double.valueOf((Integer)args[index] );              
        throw new Exception("Invalid arg is not an Number: Type is: " + args[index].getClass().getName() );
    }

    public static Long getLong(Object[] args, int index, Scriptable scope) throws Exception {
        if( args[index] instanceof Double )
            return ((Double)args[index]).longValue();
        if( args[index] instanceof Long )
            return (Long)args[index];
        if( args[index] instanceof Integer )
            return (Long)args[index];               
        throw new Exception("Invalid arg is not an Number: Type is: " + args[index].getClass().getName() );
    }

    protected static Integer getInteger(Object[] args, int index, Scriptable scope) throws Exception {
        if( args[index] instanceof Double )
            return ((Double)args[index]).intValue();
        if( args[index] instanceof Long )
            return ((Long)args[index]).intValue();
        if( args[index] instanceof Integer )
            return (Integer)args[index];            
        throw new Exception("Invalid arg is not an Number: Type is: " + args[index].getClass().getName() );
    }

    public static Object passDateTime(DateTime date, Context cx, Scriptable scope) {
        if (date == null)
            return null; 
        Object[] args = { date.getMillis() };
        return cx.newObject(scope, "Date", args);
    }

    // ThingworxValueCollectionAdapter => ValueCollection
    // ThingworxInfoTableAdapter => InfoTable
    // NativeDate => long => DateTime 
    // SandboxNativeJavaArray => byte[]
    // ThingworxJSONObjectAdapter => JSONObject
    // NativeObject => JSONObject
    // 

}
