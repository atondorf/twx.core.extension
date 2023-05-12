package twx.core.db;

import org.mozilla.javascript.*;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.security.authentication.AuthenticationUtilities;

import twx.core.db.scriptable.QueryBuilder;

public class DatabaseScriptLibrary {
	
    // Scriptable Interface for contrsution
    // --------------------------------------------------------------------------------
    public static void require_core_db(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        // Check if the class is already registered ...
        var obj = ScriptableObject.getProperty(me, "QueryBuilder");
        if (obj == Scriptable.NOT_FOUND)
            ScriptableObject.defineClass(me, QueryBuilder.class);
    }
    
    
}
