package twx.core.db;

import com.google.common.collect.Lists;
import com.thingworx.types.InfoTable;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BatchDatabaseTableHandler implements DatabaseTableHandler {
  private List<DatabaseTableHandler> databaseTableHandlers;
  
  protected BatchDatabaseTableHandler(List<DatabaseTableHandler> databaseTableHandlers) {
    this.databaseTableHandlers = databaseTableHandlers;
  }
  
  List<DatabaseTableHandler> getDatabaseTableHandlers() {
    if (this.databaseTableHandlers == null)
      this.databaseTableHandlers = Lists.newArrayList(); 
    return this.databaseTableHandlers;
  }
  
  public InfoTable buildInfoTable(QueryResult queryResult) {
    return null;
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    QueryResult queryResult = new QueryResult(0, true);
    for (DatabaseTableHandler databaseTableHandler : getDatabaseTableHandlers()) {
      Optional<QueryResult> found = databaseTableHandler.execute(connection);
      Objects.requireNonNull(queryResult);
      found.ifPresent(queryResult::append);
    } 
    return Optional.of(queryResult);
  }
  
  public Optional<DataChange> getDataChange() {
    return Optional.empty();
  }
}
