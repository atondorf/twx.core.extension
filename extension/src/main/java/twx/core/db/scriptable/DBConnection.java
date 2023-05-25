package twx.core.db.scriptable;

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
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.datashape.DataShape;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter;
import com.thingworx.things.database.AbstractDatabase;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSStaticFunction;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.Connection;
import twx.core.db.imp.DBUtil;

public class DBConnection extends ScriptableObject {
    private static final long serialVersionUID = 1L;

    AbstractDatabase    databaseThing   = null;
    Connection          connection      = null;

    @Override
    public String getClassName() { return "DBConnection"; }
    
    @Override
    protected void finalize() throws Throwable {
        if( connection != null)
            connection.close();
        connection = null;
    }

    public DBConnection() {}

    public DBConnection(String dbThingName) throws Exception {
        this.databaseThing = DBUtil.getAbstractDatabaseDirect(dbThingName);
        if( this.databaseThing == null ) 
            throw new ThingworxRuntimeException("Thing:" + dbThingName + " is not a database");
        connection = databaseThing.getConnection();
        connection.setAutoCommit(false);
    }

    @JSFunction
    public void commit() throws SQLException {
        if( connection != null)
            connection.commit();
    }

    @JSFunction
    public void rollback() throws SQLException {
        if( connection != null)
            connection.rollback();
    }

    @JSFunction
    public void close() throws SQLException {
        if( connection != null)
            connection.close();
        connection = null;
    }




}
