package twx.core.db;

class ForeignKeyInfo {
  private String name;
  
  private String columnName;
  
  private String referencedTable;
  
  private String referencedColumn;
  
  ForeignKeyInfo(String name, String columnName, String referencedTable, String referencedColumn) {
    this.name = name;
    this.columnName = columnName;
    this.referencedTable = referencedTable;
    this.referencedColumn = referencedColumn;
  }
  
  String getName() {
    return this.name;
  }
  
  String getColumnName() {
    return this.columnName;
  }
  
  String getReferencedTable() {
    return this.referencedTable;
  }
  
  String getReferencedColumn() {
    return this.referencedColumn;
  }
}
