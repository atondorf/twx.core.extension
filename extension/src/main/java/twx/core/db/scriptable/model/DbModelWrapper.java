package twx.core.db.scriptable.model;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

import twx.core.db.scriptable.DBConnection;
import twx.core.db.util.DatabaseUtil;

public class DbModelWrapper extends ScriptableObject {

    // region ScriptableObject basics
    // --------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;

    public DbModelWrapper() {}

    public DbModelWrapper(String thingName) throws Exception {
      
    }

    @JSConstructor
    public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) throws Exception {
        if (args.length < 1 || args[0] == Context.getUndefinedValue())
            throw new IllegalArgumentException("DBConnection - First Param must be Name of an DBThing");
        String thingName = Context.toString(args[0]);
        return new DbModelWrapper(thingName);
    }

    @Override
    public String getClassName() {
        return "DbModelWrapper";
    }

    @Override
    protected void finalize() throws Throwable {
     
    }

}
