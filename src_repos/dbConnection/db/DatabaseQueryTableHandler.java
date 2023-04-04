package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.types.InfoTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseQueryTableHandler extends AbstractDatabaseTableHandler {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseQueryTableHandler.class);
  
  private int offset;
  
  private int limit;
  
  private JsonQueryAdapter queryAdapter;
  
  DatabaseQueryTableHandler(DataShape dataShape, JSONObject filter, DatabaseHandler databaseHandler, int offset, int limit) {
    super(dataShape, databaseHandler);
    this.offset = offset;
    this.limit = limit;
    this.queryAdapter = new JsonQueryAdapter(databaseHandler, dataShape, "*", filter, null);
  }
  
  protected String createStatement(DataShape dataShape) {
    return getJdbcQuery().build();
  }
  
  protected JdbcQuery createJdbcQuery(DataShape dataShape, DatabaseHandler databaseHandler) {
    try {
      JdbcQuery jdbcQuery = this.queryAdapter.createQuery(databaseHandler.getJdbcQuery());
      jdbcQuery.offset(this.offset);
      jdbcQuery.limit(this.limit);
      return jdbcQuery;
    } catch (JSONException e) {
      throw new ThingworxRuntimeException(e);
    } 
  }
  
  public Optional<QueryResult> execute(Connection connection) throws Exception {
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = getJdbcQuery().prepareStatement(connection);
      if (_logger.isDebugEnabled())
        _logger.debug("SQL Query : " + preparedStatement.toString()); 
      resultSet = preparedStatement.executeQuery();
      QueryResult queryResult = buildQueryResult(resultSet, this.queryAdapter.getSelect().getPrimaryColumnName());
      return Optional.of(queryResult);
    } catch (SQLException sqlex) {
      _logger.error("Error in sql query: " + getJdbcQuery().build());
      throw sqlex;
    } finally {
      if (preparedStatement != null)
        preparedStatement.close(); 
      if (resultSet != null)
        resultSet.close(); 
    } 
  }
  
  public InfoTable buildInfoTable(QueryResult queryResult) throws Exception {
    if (this.queryAdapter.getSelect() == null)
      return buildInfoTableFromQueryResult(queryResult); 
    return buildInfoTableFromQueryWithSelect(this.queryAdapter.getSelect(), queryResult);
  }
}
