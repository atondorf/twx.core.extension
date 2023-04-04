package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.logging.LogUtilities;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DatabaseMetaDataManager {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DatabaseMetaDataManager.class);
  
  private static final String TABLE = "TABLE";
  
  private static final String TABLE_NAME = "TABLE_NAME";
  
  private static final String COLUMN_NAME = "COLUMN_NAME";
  
  private static final String TYPE_NAME = "TYPE_NAME";
  
  private static final String NULLABLE = "NULLABLE";
  
  private static final String INDEX_NAME = "INDEX_NAME";
  
  private static final String NON_UNIQUE = "NON_UNIQUE";
  
  private static final String FK_NAME = "FK_NAME";
  
  private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
  
  private static final String PKTABLE_NAME = "PKTABLE_NAME";
  
  private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
  
  private static final String DATA_TYPE = "DATA_TYPE";
  
  private static final String COLUMN_SIZE = "COLUMN_SIZE";
  
  private Map<String, TableInfo> tableInfos;
  
  protected static DatabaseMetaDataManager getInstance() {
    return new DatabaseMetaDataManager();
  }
  
  private void buildDatabaseMetaDataManager(DatabaseConnectionManager databaseConnectionManager) {
    _logger.debug("Build database metadata");
    long start = System.currentTimeMillis();
    Connection connection = null;
    ResultSet resultSet = null;
    try {
      connection = databaseConnectionManager.getConnection();
      DatabaseMetaData databaseMetaData = connection.getMetaData();
      String catalog = null;
      String schema = null;
      String tableNamePattern = "%";
      String columnNamePattern = null;
      boolean unique = false;
      boolean approximate = false;
      resultSet = databaseMetaData.getTables(catalog, schema, tableNamePattern, new String[] { "TABLE" });
      this.tableInfos = Maps.newHashMap();
      while (resultSet.next()) {
        String tableName = resultSet.getString("TABLE_NAME");
        TableInfo tableInfo = new TableInfo(tableName);
        this.tableInfos.put(tableInfo.getName(), tableInfo);
        ResultSet columns = null;
        ResultSet indexes = null;
        ResultSet foreignKeys = null;
        try {
          columns = databaseMetaData.getColumns(catalog, schema, tableName, columnNamePattern);
          tableInfo.addColumns(getColumnInfos(columns));
          indexes = databaseMetaData.getIndexInfo(catalog, schema, tableName, unique, approximate);
          tableInfo.addIndexes(getIndexInfos(indexes));
          foreignKeys = databaseMetaData.getImportedKeys(catalog, schema, tableName);
          tableInfo.addForeignKeys(getForeignKeyInfos(foreignKeys));
        } finally {
          close(columns);
          close(indexes);
          close(foreignKeys);
        } 
      } 
      long stop = System.currentTimeMillis();
      long time = stop - start;
      _logger.debug("Database metadata Build Time:" + time);
    } catch (Exception ex) {
      _logger.error(ex.getMessage(), ex);
      throw new ThingworxRuntimeException("An error has occurred. Contact your administrator for further details.");
    } finally {
      close(resultSet);
      databaseConnectionManager.close(connection);
    } 
  }
  
  private void close(ResultSet resultSet) {
    try {
      if (resultSet != null)
        resultSet.close(); 
    } catch (SQLException e) {
      _logger.error(e.getMessage(), e);
      throw new ThingworxRuntimeException("An error has occurred. Contact your administrator for further details.");
    } 
  }
  
  Map<Integer, String> buildTypeMap(Set<String> supportedSqlType, DatabaseConnectionManager databaseConnectionManager) {
    _logger.debug("Build type map");
    long start = System.currentTimeMillis();
    Connection connection = null;
    Map<Integer, String> typeMap = Maps.newHashMap();
    ResultSet typeResultSets = null;
    try {
      connection = databaseConnectionManager.getConnection();
      DatabaseMetaData databaseMetaData = connection.getMetaData();
      typeResultSets = databaseMetaData.getTypeInfo();
      while (typeResultSets.next()) {
        String typeName = typeResultSets.getString("TYPE_NAME");
        int dataType = typeResultSets.getInt("DATA_TYPE");
        if (!typeMap.containsKey(Integer.valueOf(dataType)) && supportedSqlType.contains(typeName))
          typeMap.put(Integer.valueOf(dataType), typeName); 
      } 
    } catch (Exception ex) {
      _logger.error("Error", ex);
      throw new ThingworxRuntimeException("An error has occurred. Contact your administrator for further details.");
    } finally {
      close(typeResultSets);
      databaseConnectionManager.close(connection);
    } 
    long stop = System.currentTimeMillis();
    long time = stop - start;
    _logger.debug("Type map build Time:" + time);
    return typeMap;
  }
  
  private List<ColumnInfo> getColumnInfos(ResultSet resultSet) throws SQLException {
    List<ColumnInfo> columnInfos = Lists.newArrayList();
    while (resultSet.next()) {
      String columnName = resultSet.getString("COLUMN_NAME");
      String typeName = resultSet.getString("TYPE_NAME");
      boolean notNull = !resultSet.getBoolean("NULLABLE");
      int length = resultSet.getInt("COLUMN_SIZE");
      if (columnName != null)
        columnInfos.add(new ColumnInfo(columnName, typeName, notNull, length)); 
    } 
    return columnInfos;
  }
  
  private Map<String, IndexInfo> getIndexInfos(ResultSet resultSet) throws SQLException {
    Map<String, IndexInfo> indexInfos = Maps.newHashMap();
    while (resultSet.next()) {
      String indexName = resultSet.getString("INDEX_NAME");
      boolean unique = !resultSet.getBoolean("NON_UNIQUE");
      String columnName = resultSet.getString("COLUMN_NAME");
      if (indexName != null) {
        if (indexInfos.containsKey(indexName)) {
          IndexInfo compositeIndex = indexInfos.get(indexName);
          ArrayList<String> compositeIndexColumn = Lists.newArrayList(compositeIndex.getColumnNames());
          compositeIndexColumn.add(columnName);
          compositeIndex.setColumnNames(compositeIndexColumn);
          indexInfos.put(indexName, compositeIndex);
          continue;
        } 
        indexInfos.put(indexName, new IndexInfo(indexName, Arrays.asList(new String[] { columnName } ), unique));
      } 
    } 
    return indexInfos;
  }
  
  private List<ForeignKeyInfo> getForeignKeyInfos(ResultSet resultSet) throws SQLException {
    List<ForeignKeyInfo> foreignKeyInfos = Lists.newArrayList();
    while (resultSet.next()) {
      String foreignKeyName = resultSet.getString("FK_NAME");
      String columnName = resultSet.getString("FKCOLUMN_NAME");
      String referencedTableName = resultSet.getString("PKTABLE_NAME");
      String referencedColumnName = resultSet.getString("PKCOLUMN_NAME");
      if (foreignKeyName != null)
        foreignKeyInfos.add(new ForeignKeyInfo(foreignKeyName, columnName, referencedTableName, referencedColumnName)); 
    } 
    return foreignKeyInfos;
  }
  
  protected Optional<TableInfo> getTableInfo(String tableName) {
    if (this.tableInfos == null)
      buildDatabaseMetaDataManager(new DatabaseConnectionManager()); 
    return Optional.ofNullable(this.tableInfos.get(tableName));
  }
  
  protected Optional<ColumnInfo> getColumnInfo(String tableName, String columnName) {
    Optional<TableInfo> found = getTableInfo(tableName);
    if (found.isPresent())
      return ((TableInfo)found.get()).getColumnInfo(columnName); 
    return Optional.empty();
  }
  
  protected Optional<IndexInfo> getIndexInfo(String tableName, String indexName) {
    Optional<TableInfo> found = getTableInfo(tableName);
    if (found.isPresent())
      return ((TableInfo)found.get()).getIndexInfo(indexName); 
    return Optional.empty();
  }
  
  protected Optional<ForeignKeyInfo> getForeignKeyInfo(String tableName, String foreignKeyName) {
    Optional<TableInfo> found = getTableInfo(tableName);
    if (found.isPresent())
      return ((TableInfo)found.get()).getForeignKeyInfo(foreignKeyName); 
    return Optional.empty();
  }
  
  protected boolean tableExist(String tableName) {
    return getTableInfo(tableName).isPresent();
  }
  
  protected boolean columnExist(String tableName, String columnName) {
    return getColumnInfo(tableName, columnName).isPresent();
  }
  
  protected boolean indexExist(String tableName, String indexName) {
    return getIndexInfo(tableName, indexName).isPresent();
  }
  
  protected boolean foreignKeyExist(String tableName, String foreignKeyName) {
    return getForeignKeyInfo(tableName, foreignKeyName).isPresent();
  }
}
