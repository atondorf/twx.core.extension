package twx.core.db;

import com.thingworx.types.InfoTable;
import java.sql.Connection;
import java.util.Optional;

public interface DatabaseTableHandler {
  InfoTable buildInfoTable(QueryResult paramQueryResult) throws Exception;
  
  Optional<QueryResult> execute(Connection paramConnection) throws Exception;
  
  Optional<DataChange> getDataChange();
}
