package twx.core.db.scriptable;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

public class SqlDbSpec extends ScriptableObject {

    @Override
    public String getClassName() {
        return "SqlDbSpec";
    } 

    @JSFunction
    public String test_toString(Object param) {
        return param.toString();
    }

    @JSFunction
    public String test_className(Object param) {
        return param.getClass().getName();
    }

    
    
}
