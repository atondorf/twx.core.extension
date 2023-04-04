package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.types.InfoTable;
import com.thingworx.types.NamedObject;
import com.thingworx.types.collections.ValueCollection;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

class ExecuteActionHandler extends AbstractExecuteHandler<InfoTable> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ExecuteActionHandler.class);
  
  private DataChangeDispatcher dataChangeDispatcher = new DataChangeDispatcher();
  
  private ActionType actionType;
  
  private String dataShapeName;
  
  protected InfoTable infoTable;
  
  ExecuteActionHandler( DatabaseHandler databaseHandler, ActionType actionType, String dataShapeName, InfoTable infoTable) {
    this(databaseHandler, actionType, dataShapeName);
    this.infoTable = infoTable;
  }
  
  ExecuteActionHandler( DatabaseHandler databaseHandler, ActionType actionType, String dataShapeName) {
    super(databaseHandler);
    this.actionType = actionType;
    this.dataShapeName = dataShapeName;
  }
  
  public InfoTable execute() throws Exception {
    long start = 0L;
    if (_logger.isDebugEnabled()) {
      _logger.debug(this.actionType.getValueString() + ":" + this.actionType.getValueString());
      start = System.currentTimeMillis();
    } 
    this.dataShapeUtils.validateDataShapeName(this.dataShapeName);
    DataShape dataShape = this.dataShapeUtils.getDataShape(this.dataShapeName);
    validateInfoTable(this.infoTable, dataShape, this.actionType);
    Pair<List<DatabaseTableHandler>, List<DataChange>> databaseTableHandlerList = getDatabaseTableHandlerList(this.actionType, dataShape, this.infoTable);
    this.dataChangeDispatcher.preDispatchDataChange((List<DataChange>)databaseTableHandlerList.getValue());
    DatabaseTableHandler databaseTableHandler = new BatchDatabaseTableHandler((List<DatabaseTableHandler>)databaseTableHandlerList.getKey());
    QueryResult queryResult = getDatabaseHandler().execute(databaseTableHandler);
    InfoTable resultInfoTable = (new InfoTableUtils()).buildInfoTableFromDataChanges(dataShape, queryResult.getDataChanges());
    if (_logger.isDebugEnabled()) {
      long stop = System.currentTimeMillis();
      long time = stop - start;
      _logger.debug(this.actionType.getValueString() + " Time:" + this.actionType.getValueString());
    } 
    return resultInfoTable;
  }
  
  private Pair<List<DatabaseTableHandler>, List<DataChange>> getDatabaseTableHandlerList(ActionType actionType, DataShape dataShape, InfoTable infoTable) {
    List<DatabaseTableHandler> databaseTableHandlerList = Lists.newArrayList();
    List<DataChange> dataChanges = Lists.newArrayList();
    for (ValueCollection valueCollection : infoTable.getRows()) {
      DatabaseTableHandler databaseTableHandler = getActionHandler(actionType, dataShape, valueCollection);
      databaseTableHandlerList.add(databaseTableHandler);
      Optional<DataChange> foundDataChange = databaseTableHandler.getDataChange();
      Objects.requireNonNull(dataChanges);
      foundDataChange.ifPresent(dataChanges::add);
    } 
    return Pair.of(databaseTableHandlerList, dataChanges);
  }
  
  private DatabaseTableHandler getActionHandler(ActionType actionType, DataShape dataShape, ValueCollection valueCollection) {
    switch (actionType) {
      case CREATE:
        return getFactory().getInsertHandler(dataShape, valueCollection);
      case UPDATE:
        return getFactory().getUpdateHandler(dataShape, valueCollection);
      case DELETE:
        return getFactory().getDeleteHandler(dataShape, valueCollection);
    } 
    throw new ThingworxRuntimeException("Action undefined:" + actionType);
  }
  
  private void validateInfoTable(InfoTable infoTable, DataShape dataShape, ActionType actionType) {
    if (infoTable == null)
      throw new ThingworxRuntimeException("Info table is null."); 
    if (infoTable.getRows() == null || infoTable.getRows().isEmpty())
      throw new ThingworxRuntimeException("Info table is empty."); 
    DataShapeDefinition dataShapeDefinition = infoTable.getDataShape();
    if (dataShapeDefinition == null)
      throw new ThingworxRuntimeException("No data shape definition associated to the info table"); 
    DataShape dataShapeAp = null;
    Optional<DataShape> foundAp = this.dataShapeUtils.getDataShapeAp(dataShape);
    if (foundAp.isPresent())
      dataShapeAp = foundAp.get(); 
    List<String> fieldNames = (List<String>)dataShape.getFields().values().stream().map(NamedObject::getName).collect(Collectors.toList());
    if (actionType.equals(ActionType.UPDATE))
      fieldNames.add("__Fields"); 
    fieldNames.add("__TimeStamp");
    if (dataShapeAp != null)
      fieldNames.addAll((Collection<? extends String>)dataShapeAp.getFields().values().stream().map(NamedObject::getName)
          .collect(Collectors.toList())); 
    List<String> dataShapeFieldNames = (List<String>)dataShapeDefinition.getFields().values().stream().map(NamedObject::getName).collect(Collectors.toList());
    if (!fieldNames.containsAll(dataShapeFieldNames))
      throw new ThingworxRuntimeException("Incompatible Datashape"); 
  }
}
