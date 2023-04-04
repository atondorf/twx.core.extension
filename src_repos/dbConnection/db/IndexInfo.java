package twx.core.db;

import java.util.List;

class IndexInfo {
  String name;
  
  List<String> columnNames;
  
  boolean unique;
  
  IndexInfo(String name, List<String> columnNames, boolean unique) {
    this.name = name;
    this.columnNames = columnNames;
    this.unique = unique;
  }
  
  String getName() {
    return this.name;
  }
  
  List<String> getColumnNames() {
    return this.columnNames;
  }
  
  void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }
  
  boolean isUnique() {
    return this.unique;
  }
}
