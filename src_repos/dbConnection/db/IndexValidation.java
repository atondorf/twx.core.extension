package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.collections.ValueCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public class IndexValidation extends DatabaseSchemaValidationHelper {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(IndexValidation.class);
  
  public static final String TYPE_INDEX = "Index";
  
  public List<ValueCollection> validateIndexes(DatabaseHandler databaseHandler, DataShape dataShape, String tableName, List<Index> indexList, DatabaseMetaDataManager databaseMetaDataManager) {
    List<ValueCollection> indexVcList = new ArrayList<>();
    indexList.forEach(index -> indexVcList.addAll(validateIndexField(databaseHandler, dataShape, tableName, index, databaseMetaDataManager)));
    return indexVcList;
  }
  
  private List<ValueCollection> validateIndexField(DatabaseHandler databaseHandler, DataShape dataShape, String tableName, Index index, DatabaseMetaDataManager databaseMetaDataManager) {
    List<ValueCollection> indexErrorMsg = new ArrayList<>();
    try {
      IndexInfo indexInfo = createIndexInfo(databaseHandler, dataShape, index);
      Optional<IndexInfo> dbFoundIndexInfo = databaseMetaDataManager.getIndexInfo(tableName, indexInfo.getName());
      if (dbFoundIndexInfo.isPresent()) {
        List<String> unmatchedIndexInfoMessages = compareIndexInfo(indexInfo, dbFoundIndexInfo.get());
        if (CollectionUtils.isNotEmpty(unmatchedIndexInfoMessages))
          for (String unmatchedIndexInfoMessage : unmatchedIndexInfoMessages)
            indexErrorMsg
              .add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, index
                  .getName(), indexInfo.getColumnNames().toString(), "Index", indexInfo
                  .getName(), unmatchedIndexInfoMessage));  
      } else {
        indexErrorMsg.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, index
              .getName(), indexInfo.getColumnNames().toString(), "Index", indexInfo
              .getName(), "Index not found in the database"));
        _logger.warn("The index " + indexInfo.getName() + " for the table " + tableName + " is not found in the database");
      } 
    } catch (Exception e) {
      String fieldName = index.getName();
      FieldDefinition fieldDefinition = dataShape.getFieldDefinition(fieldName);
      String identifierName = databaseHandler.getIndexName(dataShape, index, false);
      String columnName = databaseHandler.getColumnName(fieldDefinition);
      indexErrorMsg.add(DatabaseSchemaValidationHelper.buildValidationInfo(dataShape.getName(), tableName, fieldName, columnName, "Index", identifierName, e
            .getLocalizedMessage()));
    } 
    return indexErrorMsg;
  }
  
  private IndexInfo createIndexInfo(DatabaseHandler databaseHandler, DataShape dataShape, Index index) {
    String indexName = databaseHandler.getIndexName(dataShape, index);
    Optional<List<String>> indexFields = Optional.ofNullable(index.getFieldNames());
    if (indexFields.isPresent()) {
      // List<String> compositeIndexColumnNames = (List<String>)((List)indexFields.get()).stream().map(field -> databaseHandler.getColumnName(dataShape.getFieldDefinition(field))).collect(Collectors.toList());
      List<String> compositeIndexColumnNames = indexFields.get().stream().map(field -> databaseHandler.getColumnName(dataShape.getFieldDefinition(field))).collect(Collectors.toList());
      return new IndexInfo(indexName, compositeIndexColumnNames, index.isUnique());
    } 
    String columnName = databaseHandler.getColumnName(dataShape.getFieldDefinition(index.getName()));
    return new IndexInfo(indexName, Lists.newArrayList(new String[] { columnName } ), index.isUnique());
  }
  
  private List<String> compareIndexInfo(IndexInfo indexInfo, IndexInfo dbFoundIndexInfo) {
    List<String> unMatchedIndexMessages = new ArrayList<>();
    String indexName = indexInfo.getName();
    if (!indexName.equals(dbFoundIndexInfo.getName()))
      unMatchedIndexMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Name", indexName, dbFoundIndexInfo
            .getName())); 
    if (!dbFoundIndexInfo.getColumnNames().containsAll(indexInfo.getColumnNames()))
      unMatchedIndexMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("ColumnName", indexInfo
            .getColumnNames().toString(), dbFoundIndexInfo.getColumnNames().toString())); 
    if ((!indexInfo.isUnique()) == dbFoundIndexInfo.isUnique())
      unMatchedIndexMessages.add(DatabaseSchemaValidationHelper.getValidationMessage("Unique", 
            String.valueOf(indexInfo.isUnique()), String.valueOf(dbFoundIndexInfo.isUnique()))); 
    if (unMatchedIndexMessages.size() > 0)
      _logger
        .warn("The property values for index " + indexName + " are not same as in the database"); 
    return unMatchedIndexMessages;
  }
}
