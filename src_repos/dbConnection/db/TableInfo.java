package twx.core.db;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

class TableInfo {
  private String name;
  
  private Map<String, ColumnInfo> columnMap = Maps.newHashMap();
  
  private Map<String, IndexInfo> indexMap = Maps.newHashMap();
  
  private Map<String, ForeignKeyInfo> foreignKeyMap = Maps.newHashMap();
  
  TableInfo(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  protected Map<String, ColumnInfo> getColumnInfos() {
    return Maps.newHashMap(this.columnMap);
  }
  
  protected Optional<ColumnInfo> getColumnInfo(String name) {
    return Optional.ofNullable(this.columnMap.get(name));
  }
  
  protected Optional<IndexInfo> getIndexInfo(String name) {
    return Optional.ofNullable(this.indexMap.get(name));
  }
  
  protected Optional<ForeignKeyInfo> getForeignKeyInfo(String name) {
    return Optional.ofNullable(this.foreignKeyMap.get(name));
  }
  
  protected void addColumn(ColumnInfo columnInfo) {
    this.columnMap.put(columnInfo.getName(), columnInfo);
  }
  
  protected void addToIndexMap(String indexName, IndexInfo indexInfo) {
    this.indexMap.put(indexName, indexInfo);
  }
  
  protected void addForeignKey(ForeignKeyInfo foreignKeyInfo) {
    this.foreignKeyMap.put(foreignKeyInfo.getName(), foreignKeyInfo);
  }
  
  protected void addColumns(Collection<ColumnInfo> columnInfos) {
    for (ColumnInfo columnInfo : columnInfos)
      addColumn(columnInfo); 
  }
  
  protected void addIndexes(Map<String, IndexInfo> indexInfos) {
    indexInfos.entrySet().forEach(indexEntry -> {
          String indexName = (String)indexEntry.getKey();
          addToIndexMap(indexName, (IndexInfo)indexInfos.get(indexName));
        });
  }
  
  protected void addForeignKeys(Collection<ForeignKeyInfo> foreignKeyInfos) {
    for (ForeignKeyInfo foreignKeyInfo : foreignKeyInfos)
      addForeignKey(foreignKeyInfo); 
  }
}
