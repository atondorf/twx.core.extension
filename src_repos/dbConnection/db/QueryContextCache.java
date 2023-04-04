package twx.core.db;

import com.thingworx.valuestreams.QueryContext;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class QueryContextCache {
  private static Map<QueryContext, QueryDatabaseContext> queryContextMap = Collections.synchronizedMap(new WeakHashMap<>());
  
  static QueryDatabaseContext getQueryDatabaseContext(QueryContext queryContext) {
    return new QueryDatabaseContext();
  }
  
  static void removeQueryContext(QueryContext queryContext) {
    queryContextMap.remove(queryContext);
  }
}
