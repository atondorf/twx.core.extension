package twx.core.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;


class DatabaseInfo {
  private int defaultStringLength = 255;
  
  private List<DataShapeDatabaseInfo> dbInfo;
  
  @JsonIgnore
  private Map<String, DataShapeDatabaseInfo> dataShapeDatabaseInfoMap;
  
  public int getDefaultStringLength() {
    return this.defaultStringLength;
  }
  
  public void setDefaultStringLength(int defaultStringLength) {
    this.defaultStringLength = defaultStringLength;
  }
  
  public List<DataShapeDatabaseInfo> getDbInfo() {
    return this.dbInfo;
  }
  
  public void setDbInfo(List<DataShapeDatabaseInfo> dbInfo) {
    this.dbInfo = dbInfo;
  }
  
  protected Optional<DataShapeDatabaseInfo> getDataShapeDatabaseInfo( String dataShapeName) {
    if (this.dataShapeDatabaseInfoMap == null) {
      this.dataShapeDatabaseInfoMap = Maps.newHashMap();
      List<DataShapeDatabaseInfo> dataShapeDatabaseInfoList = getDbInfo();
      if (dataShapeDatabaseInfoList != null)
        for (DataShapeDatabaseInfo dataShapeDatabaseInfo : dataShapeDatabaseInfoList)
          this.dataShapeDatabaseInfoMap.put(dataShapeDatabaseInfo.getDataShapeName(), dataShapeDatabaseInfo);  
    } 
    return Optional.ofNullable(this.dataShapeDatabaseInfoMap.get(dataShapeName));
  }
}
