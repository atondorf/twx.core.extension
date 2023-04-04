package twx.core.db;

import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public interface DatabaseHandler {
  DatabaseTableHandlerFactory getDatabaseTableHandlerFactory();
  
  Optional<DataTypeConverter> getDataTypeConverter(BaseTypes paramBaseTypes);
  
  Optional<Pair<Integer, String>> getSqlType(BaseTypes paramBaseTypes);
  
  Optional<Integer> getSqlTypeKey(BaseTypes paramBaseTypes);
  
  String getPrimaryKeySqlType(BaseTypes paramBaseTypes, boolean paramBoolean);
  
  JdbcQuery getJdbcQuery();
  
  String getTableName(DataShape paramDataShape);
  
  String getColumnName(FieldDefinition paramFieldDefinition);
  
  int getDefaultStringLength();
  
  default String getIndexName(DataShape dataShape, Index index) {
    return getIndexName(dataShape, index, true);
  }
  
  String getIndexName(DataShape paramDataShape, Index paramIndex, boolean paramBoolean);
  
  default String getForeignKeyName(DataShape dataShape, FieldDefinition fieldDefinition, ForeignKey foreignKey) {
    return getForeignKeyName(dataShape, fieldDefinition, foreignKey, true);
  }
  
  String getForeignKeyName(DataShape paramDataShape, FieldDefinition paramFieldDefinition, ForeignKey paramForeignKey, boolean paramBoolean);
  
  String getNotNullConstraintName(DataShape paramDataShape, FieldDefinition paramFieldDefinition);
  
  String getUniqueConstraintName(DataShape paramDataShape, FieldDefinition paramFieldDefinition);
  
  String getDefaultConstraintName(DataShape paramDataShape, FieldDefinition paramFieldDefinition);
  
  DatabaseMetaDataManager getDatabaseMetaDataManager();
  
  DatabaseInfoManager getDatabaseInfoManager();
  
  QueryResult execute(DatabaseTableHandler paramDatabaseTableHandler);
  
  List<Pair<DatabaseTableHandler, QueryResult>> executeList(List<DatabaseTableHandler> paramList);
  
  boolean tableExist(DataShape paramDataShape, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  boolean columnExist(DataShape paramDataShape, FieldDefinition paramFieldDefinition, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  boolean indexExist(DataShape paramDataShape, Index paramIndex, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  boolean isNotNull(DataShape paramDataShape, FieldDefinition paramFieldDefinition, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  boolean foreignKeyExist(DataShape paramDataShape, FieldDefinition paramFieldDefinition, ForeignKey paramForeignKey, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  boolean isUnique(DataShape paramDataShape, FieldDefinition paramFieldDefinition, DatabaseMetaDataManager paramDatabaseMetaDataManager);
  
  void startTransaction() throws Exception;
  
  void commitTransaction();
  
  void rollbackTransaction();
  
  void closeTransaction();
  
  void startTransactionLookUp();
  
  void stopTransactionLookup();
  
  <T> T executeHandler(ExecuteHandler<T> paramExecuteHandler);
}
