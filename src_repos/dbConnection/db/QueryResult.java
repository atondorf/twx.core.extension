package twx.core.db;

import com.google.common.collect.Lists;
import java.util.List;

public class QueryResult {
  private int result;
  
  private boolean successful;
  
  private List<ResultRow> resultRows = Lists.newArrayList();
  
  private List<DataChange> dataChanges = Lists.newArrayList();
  
  protected QueryResult(int result, boolean successful) {
    this.result = result;
    this.successful = successful;
  }
  
  protected boolean updateSuccessful() {
    return (this.result == 1);
  }
  
  protected boolean isSuccessful() {
    return this.successful;
  }
  
  protected void addRow(ResultRow resultRow) {
    if (resultRow != null)
      this.resultRows.add(resultRow); 
  }
  
  protected List<ResultRow> getRows() {
    return Lists.newArrayList(this.resultRows);
  }
  
  protected void append(QueryResult queryResult) {
    this.result |= queryResult.result;
    for (ResultRow row : queryResult.getRows())
      addRow(row); 
    for (DataChange dataChange : queryResult.getDataChanges())
      addDataChange(dataChange); 
  }
  
  protected void merge(QueryResult queryResult) {
    if (getRows().size() > 0)
      for (int count = 0; count < getRows().size(); ) {
        ResultRow row = getRows().get(count);
        if (queryResult.getRows().size() > count) {
          ResultRow rowToMerge = queryResult.getRows().get(count);
          row.merge(rowToMerge);
          count++;
        } 
      }  
    for (DataChange dataChange : queryResult.getDataChanges())
      addDataChange(dataChange); 
  }
  
  protected void addDataChange(DataChange dataChange) {
    if (!this.dataChanges.contains(dataChange))
      this.dataChanges.add(dataChange); 
  }
  
  public List<DataChange> getDataChanges() {
    return this.dataChanges;
  }
}
