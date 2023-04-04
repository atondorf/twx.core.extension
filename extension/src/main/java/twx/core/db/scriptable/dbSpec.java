package twx.core.db.scriptable;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

public class dbSpec extends ScriptableObject {

    @Override
    public String getClassName() {
        return "dbSpec";
    }

    // The zero-argument constructor used by Rhino runtime to create instances
    @JSConstructor    
    public SqlDbSpec() { };

    @JSFunction
    public String test_toString(Object param) {
        return param.toString();
    }

    @JSFunction
    public String test_className(Object param) {
        return param.getClass().getName();
    }

}
