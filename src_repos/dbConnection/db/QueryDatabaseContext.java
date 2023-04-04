package twx.core.db;

import java.util.List;
import java.util.Map;

public class QueryDatabaseContext {
  private String customConfigurationThingName;
  
  private String databaseThingName;
  
  private DatabaseHandlerLocator databaseHandlerLocator;
  
  private DatabaseHandler databaseHandler;
  
  private String historicalDataManagerName;
  
  private Boolean historicalDataLogEnable;
  
  private Map<String, Map<String, List<ActionType>>> historicalDataLogMap;
  
  public String getDatabaseThingName() {
    return this.databaseThingName;
  }
  
  public String getCustomConfigurationThingName() {
    return this.customConfigurationThingName;
  }
  
  public void setCustomConfigurationThingName(String customConfigurationThingName) {
    this.customConfigurationThingName = customConfigurationThingName;
  }
  
  public void setDatabaseThingName(String databaseThingName) {
    this.databaseThingName = databaseThingName;
  }
  
  public DatabaseHandlerLocator getDatabaseHandlerLocator() {
    return this.databaseHandlerLocator;
  }
  
  public void setDatabaseHandlerLocator(DatabaseHandlerLocator databaseHandlerLocator) {
    this.databaseHandlerLocator = databaseHandlerLocator;
  }
  
  public DatabaseHandler getDatabaseHandler() {
    return this.databaseHandler;
  }
  
  public void setDatabaseHandler(DatabaseHandler databaseHandler) {
    this.databaseHandler = databaseHandler;
  }
  
  public String getHistoricalDataManagerName() {
    return this.historicalDataManagerName;
  }
  
  public void setHistoricalDataManagerName(String historicalDataManagerName) {
    this.historicalDataManagerName = historicalDataManagerName;
  }
  
  public Boolean isHistoricalDataLogEnable() {
    return this.historicalDataLogEnable;
  }
  
  public void setHistoricalDataLogEnable(Boolean historicalDataLogEnable) {
    this.historicalDataLogEnable = historicalDataLogEnable;
  }
  
  public Map<String, Map<String, List<ActionType>>> getHistoricalDataLogMap() {
    return this.historicalDataLogMap;
  }
  
  public void setHistoricalDataLogMap(Map<String, Map<String, List<ActionType>>> historicalDataLogMap) {
    this.historicalDataLogMap = historicalDataLogMap;
  }
}
