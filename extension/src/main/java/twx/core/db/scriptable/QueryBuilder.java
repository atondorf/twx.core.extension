package twx.core.db.scriptable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSStaticFunction;

import com.thingworx.dsl.engine.adapters.ThingworxEntityAdapter;
import com.thingworx.dsl.engine.adapters.ThingworxJSONObjectAdapter;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;

public class QueryBuilder extends ScriptableObject {

    // Implementation ...
    // --------------------------------------------------------------------------------
    private StringBuilder sqlBuilder;
    private List<String> parameters = new LinkedList<>();
    private List<Map<String, Object>> results = null;
    private int updateCount = -1;
    private List<Object> generatedKeys = null;

    private static final long serialVersionUID = -3833489899933339159L;

    @Override
    public String getClassName() {
        return "QueryBuilder";
    }

    public QueryBuilder() {}

    @JSConstructor
    public QueryBuilder(String sql) {
        sqlBuilder = new StringBuilder();
        append(sql);
    }

    private QueryBuilder(StringBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    public static QueryBuilder select(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException();
        }
        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ");
        var queryBuilder = new QueryBuilder(sqlBuilder);
        for (var i = 0; i < columns.length; i++) {
            if (i > 0) {
                queryBuilder.sqlBuilder.append(", ");
            }
            queryBuilder.append(columns[i]);
        }
        return queryBuilder;
    }

    @JSFunction
    public static QueryBuilder select(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in select"); 

        Object[] arg = { new String("select ") };
        var queryBuilder = (QueryBuilder)cx.newObject(me, "QueryBuilder", arg );
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                queryBuilder.sqlBuilder.append(", ");
            }
            queryBuilder.append( Context.toString(args[i]) );
        }
        return queryBuilder;
    }

    public QueryBuilder from(String... tables) {
        if (tables == null || tables.length == 0) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" from ");
        sqlBuilder.append(String.join(", ", tables));
        return this;
    }

    public QueryBuilder from(QueryBuilder queryBuilder, String alias) {
        if (queryBuilder == null || alias == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" from (");
        sqlBuilder.append(queryBuilder.getSQL());
        sqlBuilder.append(") ");
        sqlBuilder.append(alias);
        parameters.addAll(queryBuilder.parameters);
        return this;
    }

    @JSFunction
    public static QueryBuilder from(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in from"); 

        var meBuilder = (QueryBuilder)me;
        if( args[0] instanceof QueryBuilder ) {
            var otherBuilder = (QueryBuilder)args[0];
            meBuilder.sqlBuilder.append(" from (");
            meBuilder.sqlBuilder.append(otherBuilder.getSQL());
            meBuilder.sqlBuilder.append(") ");
            meBuilder.sqlBuilder.append(Context.toString(args[1]));
            meBuilder.parameters.addAll(otherBuilder.parameters);
        } else {
            meBuilder.sqlBuilder.append(" from ");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    meBuilder.sqlBuilder.append(", ");
                }
                meBuilder.append(Context.toString(args[i]));
            }
        }
        return (QueryBuilder)me;
    }

    public QueryBuilder join(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" join ");
        sqlBuilder.append(table);
        return this;
    }

    public QueryBuilder join(QueryBuilder queryBuilder, String alias) {
        if (queryBuilder == null || alias == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" join (");
        sqlBuilder.append(queryBuilder.getSQL());
        sqlBuilder.append(") ");
        sqlBuilder.append(alias);
        parameters.addAll(queryBuilder.parameters);
        return this;
    }

    @JSFunction
    public static QueryBuilder join(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in join"); 

        var meBuilder = (QueryBuilder)me;
        if( args[0] instanceof QueryBuilder ) {
            var otherBuilder = (QueryBuilder)args[0];
            meBuilder.sqlBuilder.append(" join (");
            meBuilder.sqlBuilder.append(otherBuilder.getSQL());
            meBuilder.sqlBuilder.append(") ");
            meBuilder.sqlBuilder.append(Context.toString(args[1]));
            meBuilder.parameters.addAll(otherBuilder.parameters);
        } else {
            meBuilder.sqlBuilder.append(" join ");
            meBuilder.sqlBuilder.append(Context.toString(args[0]));
        }
        return (QueryBuilder)me;
    }

    @JSFunction
    public QueryBuilder leftJoin(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" left join ");
        sqlBuilder.append(table);
        return this;
    }

    @JSFunction
    public QueryBuilder rightJoin(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" right join ");
        sqlBuilder.append(table);
        return this;
    }

    public QueryBuilder on(String... predicates) {
        return filter("on", predicates);
    }

    public QueryBuilder where(String... predicates) {
        return filter("where", predicates);
    }

    @JSFunction
    public static QueryBuilder on(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in on"); 
        var meBuilder = (QueryBuilder)me;
        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return meBuilder.filter("on", predicates);
    }

    @JSFunction
    public static QueryBuilder where(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in on"); 
        var meBuilder = (QueryBuilder)me;
        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return meBuilder.filter("where", predicates);
    }

    private QueryBuilder filter(String clause, String... predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" ");
        sqlBuilder.append(clause);
        sqlBuilder.append(" ");
        for (var i = 0; i < predicates.length; i++) {
            if (i > 0) {
                sqlBuilder.append(" ");
            }
            append(predicates[i]);
        }
        return this;
    }

    public static String and(String... predicates) {
        return conditional("and", predicates);
    }

    public static String or(String... predicates) {
        return conditional("or", predicates);
    }

    @JSStaticFunction
    public static String and(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in select"); 

        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return conditional("and", predicates);
    }

    @JSStaticFunction
    public static String or(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in select"); 

        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return conditional("or", predicates);
    }

    private static String conditional(String operator, String... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException();
        }
        var stringBuilder = new StringBuilder();
        stringBuilder.append(operator);
        stringBuilder.append(" ");
        if (predicates.length > 1) {
            stringBuilder.append("(");
        }
        for (var i = 0; i < predicates.length; i++) {
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(predicates[i]);
        }
        if (predicates.length > 1) {
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }
    
    public static String allOf(String... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException();
        }
        return conditionalGroup("and", predicates);
    }

    public static String anyOf(String... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException();
        }
        return conditionalGroup("or", predicates);
    }

    @JSStaticFunction
    public static String allOf(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in select"); 

        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return conditionalGroup("or", predicates);
    }

    @JSStaticFunction
    public static String anyOf(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in select"); 

        String[] predicates = Arrays.copyOf(args, args.length, String[].class );
        return conditionalGroup("or", predicates);
    }

    private static String conditionalGroup(String operator, String... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException();
        }
        var stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (var i = 0; i < predicates.length; i++) {
            if (i > 0) {
                stringBuilder.append(" ");
                stringBuilder.append(operator);
                stringBuilder.append(" ");
            }
            stringBuilder.append(predicates[i]);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @JSStaticFunction
    public static String equalTo(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("= (%s)", queryBuilder);
    }

    @JSStaticFunction
    public static String notEqualTo(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("!= (%s)", queryBuilder);
    }

    @JSStaticFunction
    public static String in(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("in (%s)", queryBuilder);
    }

    @JSStaticFunction
    public static String notIn(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("not in (%s)", queryBuilder);
    }

    @JSStaticFunction
    public static String exists(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("exists (%s)", queryBuilder);
    }

    @JSStaticFunction
    public static String notExists(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        return String.format("not exists (%s)", queryBuilder);
    }

    public QueryBuilder orderBy(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" order by ");
        sqlBuilder.append(String.join(", ", columns));
        return this;
    }

    @JSFunction
    public static QueryBuilder orderBy(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in on"); 

        var meBuilder = (QueryBuilder)me;
        String[] columns = Arrays.copyOf(args, args.length, String[].class );
        meBuilder.sqlBuilder.append(" order by ");
        meBuilder.sqlBuilder.append(String.join(", ", columns));
        return meBuilder;
    }

    @JSFunction
    public QueryBuilder limit(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" limit ");
        sqlBuilder.append(count);
        return this;
    }

    @JSFunction
    public QueryBuilder forUpdate() {
        sqlBuilder.append(" for update");
        return this;
    }

    @JSFunction
    public QueryBuilder union(QueryBuilder queryBuilder) {
        if (queryBuilder == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" union ");
        sqlBuilder.append(queryBuilder.getSQL());
        parameters.addAll(queryBuilder.parameters);
        return this;
    }

    public static QueryBuilder insertInto(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }

        var sqlBuilder = new StringBuilder();

        sqlBuilder.append("insert into ");
        sqlBuilder.append(table);

        return new QueryBuilder(sqlBuilder);
    }

    @JSStaticFunction
    public static QueryBuilder insertInto(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {  
        if (args.length != 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in insertInto"); 

        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into ");
        sqlBuilder.append(Context.toString(args[0]));
        Object[] arg = { new String(sqlBuilder.toString()) };
        var queryBuilder = (QueryBuilder)cx.newObject(me, "QueryBuilder", arg );
        return queryBuilder;
    }

    /**
     * Appends column values to an "insert into" query.
     * @param values    The values to insert.
     * @return          The {@link QueryBuilder} instance.
     */
    public QueryBuilder values(Map<String, ?> values) {
        if (values == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" (");
        List<String> columns = new ArrayList<>(values.keySet());
        var n = columns.size();
        for (var i = 0; i < n; i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(columns.get(i));
        }
        sqlBuilder.append(") values (");
        for (var i = 0; i < n; i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            encode(values.get(columns.get(i)));
        }
        sqlBuilder.append(")");
        return this;
    }

    @JSFunction
    public static QueryBuilder values(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in join"); 

        var meBuilder = (QueryBuilder)me;
       
        return meBuilder;
    }


    private QueryBuilder values( JSONObject values ) {
        
        return this;
    }

    private QueryBuilder values( InfoTable values ) {
        
        return this;
    }

    private QueryBuilder values( ValueCollection values ) {
     
        return this;
    }

    /**
     * Appends an "on duplicate key update" clause to a query.
     * @param columns   The columns to update.
     * @return          The {@link QueryBuilder} instance.
     */
    public QueryBuilder onDuplicateKeyUpdate(String... columns) {
        if (columns == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" on duplicate key update ");
        for (var i = 0; i < columns.length; i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            var column = columns[i];
            sqlBuilder.append(column);
            sqlBuilder.append(" = value(");
            sqlBuilder.append(column);
            sqlBuilder.append(")");
        }
        return this;
    }

    /**
     * Creates an "update" query.
     * @param table     The table name.
     * @return          The new {@link QueryBuilder} instance.
     */
    public static QueryBuilder update(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("update ");
        sqlBuilder.append(table);
        return new QueryBuilder(sqlBuilder);
    }

    @JSStaticFunction
    public static QueryBuilder update(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {  
        if (args.length != 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in update"); 

        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("update ");
        sqlBuilder.append(Context.toString(args[0]));
        Object[] arg = { new String(sqlBuilder.toString()) };
        var queryBuilder = (QueryBuilder)cx.newObject(me, "QueryBuilder", arg );
        return queryBuilder;
    }

    /**
     * Appends column values to an "update" query.
     * @param values    The values to update.
     * @return          The {@link QueryBuilder} instance.
     */
    public QueryBuilder set(Map<String, ?> values) {
        if (values == null) {
            throw new IllegalArgumentException();
        }
        sqlBuilder.append(" set ");
        var i = 0;
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(entry.getKey());
            sqlBuilder.append(" = ");
            encode(entry.getValue());
            i++;
        }
        return this;
    }

    @JSFunction
    public static QueryBuilder set(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception { 
        if (args.length < 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in join"); 

        var meBuilder = (QueryBuilder)me;
       
        return meBuilder;
    }


    private QueryBuilder set( JSONObject values ) {
        
        return this;
    }

    private QueryBuilder set( InfoTable values ) {
        
        return this;
    }

    private QueryBuilder set( ValueCollection values ) {
     
        return this;
    }

    /**
     * Creates a "delete from" query.
     * @param table     The table name.
     * @return          The new {@link QueryBuilder} instance.
     */
    public static QueryBuilder deleteFrom(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }
        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("delete from ");
        sqlBuilder.append(table);
        return new QueryBuilder(sqlBuilder);
    }

    @JSStaticFunction
    public static QueryBuilder deleteFrom(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {  
        if (args.length != 1)
            throw new IllegalArgumentException("Invalid Number of Arguments in deleteFrom"); 

        var sqlBuilder = new StringBuilder();
        sqlBuilder.append("delete from  ");
        sqlBuilder.append(Context.toString(args[0]));
        Object[] arg = { new String(sqlBuilder.toString()) };
        var queryBuilder = (QueryBuilder)cx.newObject(me, "QueryBuilder", arg );
        return queryBuilder;
    }

    /**
     * Returns the result of executing a query that is expected to return at
     * most a single row.
     * @return
     * The query result, or {@code null} if the query either did not produce a
     * result set or did not return any rows.
     */
    public Map<String, Object> getResult() {
        if (results == null) {
            return null;
        }
        switch (results.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return results.get(0);
            }
            default: {
                throw new IllegalStateException("Unexpected result count.");
            }
        }
    }

    /**
     * Returns the results of executing a query.
     * @return
     * The query results, or {@code null} if the query did not produce a result
     * set.
     */
    public List<Map<String, Object>> getResults() {
        return results;
    }

    /**
     * Returns the number of rows that were affected by the query.
     * @return
     * The number of rows that were affected by the query, or -1 if the query
     * did not produce an update count.
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * Returns the keys that were generated by the query.
     * @return
     * The keys that were generated by the query, or {@code null} if the query
     * did not produce any generated keys.
     */
    public List<Object> getGeneratedKeys() {
        return generatedKeys;
    }

    /**
     * Prepares a query for execution.
     * @param connection    The connection on which the query will be executed.
     * @return              A prepared statement that can be used to execute the query.
     * @throws              SQLException If an error occurs while preparing the query.
     */
    public PreparedStatement prepare(Connection connection) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException();
        }
        return connection.prepareStatement(getSQL(), Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Executes a query.
     * @param statement The statement that will be used to execute the query.
     * @param arguments The query arguments.
     * @return          The query results.
     * @throws          SQLException If an error occurs while executing the query.
     */
    public ResultSet executeQuery(PreparedStatement statement, Map<String, ?> arguments) throws SQLException {
        if (statement == null || arguments == null) {
            throw new IllegalArgumentException();
        }
        apply(statement, arguments);
        return statement.executeQuery();
    }

    /**
     * Executes a query.
     * @param statement The statement that will be used to execute the query.
     * @param arguments The query arguments.
     * @return          The number of rows that were affected by the query.
     * @throws SQLException If an error occurs while executing the query.
     */
    public int executeUpdate(PreparedStatement statement, Map<String, ?> arguments) throws SQLException {
        if (statement == null || arguments == null) {
            throw new IllegalArgumentException();
        }
        apply(statement, arguments);
        return statement.executeUpdate();
    }

    private void apply(PreparedStatement statement, Map<String, ?> arguments) throws SQLException {
        var i = 1;
        for (var parameter : parameters) {
            if (parameter == null) {
                continue;
            }
            statement.setObject(i++, arguments.get(parameter));
        }
    }

    public Collection<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    @JSFunction
    public String getSQL() {
        return sqlBuilder.toString();
    }

    private void append(String sql) {
        var quoted = false;
        var n = sql.length();
        var i = 0;
        while (i < n) {
            var c = sql.charAt(i++);
            if (c == ':' && !quoted) {
                var parameterBuilder = new StringBuilder();
                while (i < n) {
                    c = sql.charAt(i);
                    if (!Character.isJavaIdentifierPart(c)) {
                        break;
                    }
                    parameterBuilder.append(c);
                    i++;
                }
                if (parameterBuilder.length() == 0) {
                    throw new IllegalArgumentException("Missing parameter name.");
                }
                parameters.add(parameterBuilder.toString());
                sqlBuilder.append("?");
            } else if (c == '?' && !quoted) {
                parameters.add(null);
                sqlBuilder.append(c);
            } else {
                if (c == '\'') {
                    quoted = !quoted;
                }
                sqlBuilder.append(c);
            }
        }
    }

    private void encode(Object value) {
        if (value instanceof QueryBuilder) {
            var queryBuilder = (QueryBuilder) value;
            sqlBuilder.append("(");
            sqlBuilder.append(queryBuilder.getSQL());
            sqlBuilder.append(")");
            parameters.addAll(queryBuilder.parameters);
        } else if (value instanceof String) {
            var string = (String) value;
            if (string.startsWith(":") || string.equals("?")) {
                append(string);
            } else {
                sqlBuilder.append("'");
                for (int i = 0, n = string.length(); i < n; i++) {
                    var c = string.charAt(i);
                    if (c == '\'') {
                        sqlBuilder.append(c);
                    }
                    sqlBuilder.append(c);
                }
                sqlBuilder.append("'");
            }
        } else {
            sqlBuilder.append(value);
        }
    }

    @JSFunction
    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        var parameterIterator = parameters.iterator();
        for (int i = 0, n = sqlBuilder.length(); i < n; i++) {
            var c = sqlBuilder.charAt(i);
            if (c == '?') {
                var parameter = parameterIterator.next();
                if (parameter == null) {
                    stringBuilder.append(c);
                } else {
                    stringBuilder.append(':');
                    stringBuilder.append(parameter);
                }
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    // --------------------------------------------------------------------------------
    // Test Functions ...
    // --------------------------------------------------------------------------------

    @JSFunction
    public String getObjClassName(Object obj) {
        if (obj instanceof ThingworxEntityAdapter) {
            return "Wrapped: " + ((ThingworxEntityAdapter) obj).unwrap().getClass().getName();
        }
        return obj.getClass().getName();
    }

    @JSFunction
    public String getObjToString(Object obj) {
        return obj.toString();
    }

    @JSFunction
    public String testDataShape(Object obj) throws Exception {
        if (!(obj instanceof ThingworxEntityAdapter)) {
            throw new Exception("obj is not a DataShape ... ");
        }
        return ((ThingworxEntityAdapter) obj).unwrap().getClass().getName();
    }

    @JSFunction
    public String testJSON(Object obj) throws Exception {
        if (!(obj instanceof ThingworxJSONObjectAdapter)) {
            throw new Exception("obj is not a JSON ... ");
        }
        return ((ThingworxJSONObjectAdapter) obj).getJSONObject().toString();
    }
    
    @JSFunction
    public JSONObject getJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("Hallo", 2);
        return obj;
    }

    @JSFunction
    public NativeObject getNativeObject() {
        NativeObject obj = new NativeObject();
        
        obj.put("Hallo", 2);
        return obj;
    }




}
