package twx.core.db.dataimport;

import ch.qos.logback.classic.Logger;
import twx.core.db.DatabaseUtility;
import twx.core.db.ExecuteHandler;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;

public class DataImportManager {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DataImportManager.class);
  
  @ThingworxServiceDefinition(name = "ImportFromBundle", description = "Imports data into the database from a bundle containing CSV files. ", isAllowOverride = true)
  @ThingworxServiceResult(name = "result", description = "Path of the directory that has been created for the import", baseType = "STRING")
  public String ImportFromBundle(@ThingworxServiceParameter(name = "sourceFileRepository", description = "The source file repository", baseType = "THINGNAME", aspects = {"isRequired:true"}) String sourceFileRepository, @ThingworxServiceParameter(name = "destinationFileRepository", description = "The destination file repository", baseType = "THINGNAME", aspects = {"isRequired:true"}) String destinationFileRepository, @ThingworxServiceParameter(name = "bundleZipPath", description = "The path to the bundled zip in the source file repository", baseType = "STRING", aspects = {"isRequired:true"}) String bundleZipPath) throws Exception {
    _logger.debug("ImportFromBundle:" + bundleZipPath);
    long start = System.currentTimeMillis();
    ExecuteHandler<String> importExecuteHandler = new ImportExecuteHandle(sourceFileRepository, destinationFileRepository, bundleZipPath);
    String result = (String)DatabaseUtility.executeHandler(importExecuteHandler);
    long stop = System.currentTimeMillis();
    long time = stop - start;
    _logger.debug("ImportFromBundle Time:" + time);
    return result;
  }
}
