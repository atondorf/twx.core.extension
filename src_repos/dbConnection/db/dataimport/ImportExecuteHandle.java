package twx.core.db.dataimport;

import ch.qos.logback.classic.Logger;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import twx.core.db.ActionType;
import twx.core.db.DataShapeUtils;
import twx.core.db.ExecuteHandler;
import twx.core.db.InfoTableUtils;
import twx.core.db.utils.DatabaseCommonUtilities;
import twx.core.db.utils.ValueCollectionBuilder;
import com.thingworx.common.exceptions.ThingworxRuntimeException;
import com.thingworx.datashape.DataShape;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.resources.localization.RuntimeLocalizationFunctions;
import com.thingworx.things.Thing;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

class ImportExecuteHandle implements ExecuteHandler<String> {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ImportExecuteHandle.class);
  
  private static final String FILE_REPOSITORY = "FileRepository";
  
  private static final String FILE_PREFIX = "file://";
  
  private static final String FILE_BINARIES_PREFIX = "file://Binaries/";
  
  private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
  
  private static final String TMP = "tmp";
  
  private static final String ARCHIVE = "archive";
  
  private static final char SLASH = '/';
  
  private static final String CSV = ".csv";
  
  private static final String EXTRACTED = "/tmp/extracted";
  
  private static final String BUNDLE_ZIP = "/tmp/bundle.zip";
  
  private static final char DOT = '.';
  
  private static final String DOWNLOAD_LINK = "downloadLink";
  
  private static final String DATASHAPE = "DataShape";
  
  private static final String COMMA_ROW_WITH = ", row with";
  
  private static final String COLON = ": ";
  
  private static final String PROP_COLON = ":";
  
  private static final String DATA_IMPORT_MANAGER_THINGNAME = "PTC.DBConnection.DataImportManager";
  
  private static final String GET_ORDERED_DATASHAPE_LIST_FOR_IMPORT = "GetOrderedDatashapeListForImport";
  
  private static final String SERVICE_CALLER_UTILS = "PTC.DBConnection.ServiceCallerUtils";
  
  private static final String FILE_REPOSITORY_ERROR = "PTC.DBConnection.DataImportManager.FileRepositoryErrorMessage";
  
  private DataShapeUtils dataShapeUtils = new DataShapeUtils();
  
  private final UidResolver uidResolver = new UidResolver();
  
  private final Thing serviceCallerUtils = ThingUtilities.findThing("PTC.DBConnection.ServiceCallerUtils");
  
  private String sourceFileRepository;
  
  private String destinationFileRepository;
  
  private String bundleZipPath;
  
  private FileRepositoryThing sourceFileRepoThing;
  
  private FileRepositoryThing destinationFileRepoThing;
  
  private String destinationBundleFolderName;
  
  private static final String ACTION_CREATE = "" + ActionType.CREATE + ":";
  
  private final InfoTableUtils infoTableUtils = new InfoTableUtils();
  
  ImportExecuteHandle(String sourceFileRepository, String destinationFileRepository, String bundleZipPath) throws Exception {
    this.sourceFileRepository = sourceFileRepository;
    this.destinationFileRepository = destinationFileRepository;
    this.bundleZipPath = bundleZipPath;
    this.sourceFileRepoThing = getSourceFileRepoThing();
    this.destinationFileRepoThing = getDestinationFileRepoThing();
    this.destinationBundleFolderName = getDestinationBundleFolderName();
  }
  
  public String execute() throws Exception {
    _logger.debug("Import:" + this.bundleZipPath);
    try {
      Map<String, String> fileNameToPath = getFileNameToPathMap(this.sourceFileRepoThing, this.destinationFileRepoThing);
      if (fileNameToPath.size() == 0)
        throw new ThingworxRuntimeException("No csv file found under the imported bundle"); 
      List<String> importOrdered = dataImportOrder();
      for (String dataShapeName : importOrdered) {
        String cvsDataShapeFilePath = fileNameToPath.get(dataShapeName);
        if (cvsDataShapeFilePath == null)
          continue; 
        processDataShape(dataShapeName, cvsDataShapeFilePath);
      } 
      this.destinationFileRepoThing.DeleteFolder(this.destinationBundleFolderName + "/tmp");
      return this.destinationBundleFolderName;
    } catch (Exception exception) {
      this.destinationFileRepoThing.DeleteFolder(this.destinationBundleFolderName);
      _logger.error("Import failed");
      throw exception;
    } 
  }
  
  private void processDataShape(String dataShapeName, String cvsDataShapeFilePath) throws Exception {
    DataShape dataShape = this.dataShapeUtils.getDataShape(dataShapeName);
    FieldDefinition primaryKey = this.dataShapeUtils.getPrimaryKeyField(dataShape);
    byte[] csvContent = this.destinationFileRepoThing.LoadBinary(cvsDataShapeFilePath);
    Reader reader = new StringReader(new String(csvContent, StandardCharsets.UTF_8));
    CSVParser records = CSVFormat.DEFAULT.withHeader(new String[0]).parse(reader);
    for (CSVRecord record : records.getRecords())
      processRecord(dataShape, primaryKey, record); 
  }
  
  private void processRecord(DataShape dataShape, FieldDefinition primaryKey, CSVRecord record) throws Exception {
    ValueCollection row = new ValueCollection();
    Map<String, String> columns = record.toMap();
    String primaryKeyName = primaryKey.getName();
    String uidValue = getInternalUidValue(dataShape.getName(), primaryKeyName, columns.get(primaryKeyName));
    columns.put(primaryKeyName, uidValue);
    for (Map.Entry<String, String> entry : columns.entrySet()) {
      String columnName = entry.getKey();
      String columnValue = entry.getValue();
      if (columnValue.startsWith("file://Binaries/")) {
        entry.setValue(getDestinationFilePath(dataShape, primaryKeyName, uidValue, columnName, columnValue, this.destinationBundleFolderName, this.destinationFileRepoThing));
      } else if (Strings.isNullOrEmpty(columnValue)) {
        entry.setValue(null);
      } else if (this.uidResolver.isReferenceField(dataShape.getName(), columnName)) {
        entry.setValue(getReferenceValue(dataShape, primaryKey, columnName, columnValue));
      } 
      row.SetStringValue(columnName, entry.getValue());
    } 
    create(dataShape, primaryKey, row);
  }
  
  private String getReferenceValue(DataShape dataShape, FieldDefinition primaryKey, String columnName, String columnValue) throws Exception {
    if (this.uidResolver.isBundleReference(columnValue)) {
      String internalUid = this.uidResolver.getInternalUid(dataShape.getName(), columnName, 
          removePrefix(columnValue, "BUNDLE:"));
      if (internalUid != null)
        return internalUid; 
      String rowUid = null;
      if (columnName.equals(primaryKey.getName()))
        rowUid = columnValue; 
      throw new Exception("DataShape: " + dataShape.getName() + ", row with" + primaryKey
          .getName() + ": " + rowUid + " , fieldName: " + columnName + " with value: " + columnValue + " is referencing " + this.uidResolver
          
          .getForeignKey(dataShape.getName(), columnName).getReferenceDataShapeName() + " that does not exist");
    } 
    return columnValue;
  }
  
  private String getInternalUidValue(String dataShapeName, String primaryKeyName, String internalUid) throws Exception {
    if (internalUid.startsWith(ACTION_CREATE)) {
      String internalUidValue = removePrefix(internalUid, ACTION_CREATE);
      if (internalUidValue.isEmpty())
        throw new Exception("DataShape: " + dataShapeName + ", row with" + primaryKeyName + ": " + internalUid + " , the value after prefix " + ACTION_CREATE + " cannot be empty"); 
      return internalUidValue;
    } 
    throw new Exception("DataShape: " + dataShapeName + ", row with" + primaryKeyName + ": " + internalUid + " is missing the prefix action primary key value.");
  }
  
  private void create(DataShape dataShape, FieldDefinition primaryKey, ValueCollection row) throws Exception {
    String internalUid = row.getStringValue(primaryKey.getName());
    InfoTable infoTable = this.infoTableUtils.getInfoTable(dataShape);
    infoTable.addRow(row);
    ValueCollection params = (new ValueCollectionBuilder()).put("infoTable", infoTable).put("dataShapeName", dataShape.getName()).get();
    if (this.serviceCallerUtils != null) {
      InfoTable result = this.serviceCallerUtils.processAPIServiceRequest("Create", params);
      String externalUid = result.getRow(0).getStringValue(primaryKey.getName());
      this.uidResolver.setExternalUid(dataShape.getName(), internalUid, externalUid);
    } else {
      throw new ThingworxRuntimeException("Service callerPTC.DBConnection.ServiceCallerUtils is not found.");
    } 
  }
  
  private Map<String, String> getFileNameToPathMap(FileRepositoryThing sourceFileRepoThing, FileRepositoryThing destinationFileRepoThing) throws Exception {
    destinationFileRepoThing.CreateFolder(this.destinationBundleFolderName);
    destinationFileRepoThing.CreateFolderInParent(this.destinationBundleFolderName, "tmp");
    destinationFileRepoThing.CreateFolderInParent(this.destinationBundleFolderName, "archive");
    byte[] content = sourceFileRepoThing.LoadBinary(this.bundleZipPath);
    destinationFileRepoThing.SaveBinary(this.destinationBundleFolderName + "/tmp/bundle.zip", content);
    destinationFileRepoThing.ExtractZipArchive(this.destinationBundleFolderName + "/tmp/bundle.zip", this.destinationBundleFolderName + "/tmp/extracted");
    Map<String, String> fileNameToPath = Maps.newHashMap();
    InfoTable fileList = destinationFileRepoThing.ListFiles(this.destinationBundleFolderName + "/tmp/extracted", null);
    for (ValueCollection row : fileList.getRows()) {
      String filePath = row.getStringValue("path");
      if (filePath.endsWith(".csv")) {
        int startIndex = filePath.lastIndexOf('/');
        int endIndex = filePath.lastIndexOf('.');
        String fileName = filePath.substring(startIndex + 1, endIndex);
        fileNameToPath.put(fileName, filePath);
      } 
    } 
    return fileNameToPath;
  }
  
  private FileRepositoryThing getSourceFileRepoThing() throws Exception {
    FileRepositoryThing sourceFileRepoThing;
    try {
      sourceFileRepoThing = (FileRepositoryThing)ThingUtilities.findThing(this.sourceFileRepository);
    } catch (Exception e) {
      throw new Exception(RuntimeLocalizationFunctions.getEffectiveToken("PTC.DBConnection.DataImportManager.FileRepositoryErrorMessage"));
    } 
    if (!sourceFileRepoThing.IsDerivedFromTemplate("FileRepository").booleanValue())
      throw new Exception(RuntimeLocalizationFunctions.getEffectiveToken("PTC.DBConnection.DataImportManager.FileRepositoryErrorMessage")); 
    return sourceFileRepoThing;
  }
  
  private FileRepositoryThing getDestinationFileRepoThing() throws Exception {
    FileRepositoryThing destinationFileRepoThing;
    try {
      destinationFileRepoThing = (FileRepositoryThing)ThingUtilities.findThing(this.destinationFileRepository);
    } catch (Exception e) {
      throw new Exception(RuntimeLocalizationFunctions.getEffectiveToken("PTC.DBConnection.DataImportManager.FileRepositoryErrorMessage"));
    } 
    if (!destinationFileRepoThing.IsDerivedFromTemplate("FileRepository").booleanValue())
      throw new Exception(RuntimeLocalizationFunctions.getEffectiveToken("PTC.DBConnection.DataImportManager.FileRepositoryErrorMessage")); 
    return destinationFileRepoThing;
  }
  
  private String getDestinationFilePath(DataShape dataShape, String primaryKeyName, String uid, String columnName, String columnValue, String destinationBundleFolderName, FileRepositoryThing destinationFileRepoThing) throws Exception {
    String sourceFilePathInBundle = columnValue.substring("file://".length());
    String destinationFilePathRelativeToArchive = columnValue.substring("file://Binaries/".length());
    String sourceFilePath = destinationBundleFolderName + "/tmp/extracted/" + destinationBundleFolderName;
    String destinationFilePath = destinationBundleFolderName + "/archive/" + destinationBundleFolderName;
    destinationFileRepoThing.MoveFile(sourceFilePath, destinationFilePath, Boolean.valueOf(false));
    File file = new File(destinationFilePath);
    String fileName = file.getName();
    if (StringUtils.isNotEmpty(fileName)) {
      String destinationFolderPath = file.getParent();
      InfoTable destinationFileListsWithLinks = destinationFileRepoThing.GetFileListingWithLinks(destinationFolderPath, fileName);
      if (DatabaseCommonUtilities.isNotEmpty(destinationFileListsWithLinks)) {
        String downloadLink = destinationFileListsWithLinks.getFirstRow().getStringValue("downloadLink");
        if (StringUtils.isNotEmpty(downloadLink))
          return downloadLink; 
      } 
    } 
    throw new ThingworxRuntimeException("DataShape: " + dataShape
        .getName() + ", row with" + primaryKeyName + ": " + uid + " fieldName: " + columnName + " has invalid file path " + columnValue);
  }
  
  private String getDestinationBundleFolderName() {
    String timestamp = (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date());
    Random random = new Random();
    String nonce = String.format("%016x", new Object[] { Long.valueOf(random.nextLong()) }).substring(0, 8);
    return timestamp + "-" + timestamp;
  }
  
  private List<String> dataImportOrder() throws Exception {
    List<String> datashapeNames = Lists.newArrayList();
    Thing dim = ThingUtilities.findThing("PTC.DBConnection.DataImportManager");
    InfoTable it = dim.processServiceRequestDirect("GetOrderedDatashapeListForImport", new ValueCollection());
    JSONObject result = (JSONObject)it.getRows().getFirstRow().getValue("result");
    JSONArray array = result.getJSONArray("array");
    for (int i = 0; i < array.length(); i++)
      datashapeNames.add(array.getString(i)); 
    return datashapeNames;
  }
  
  private String removePrefix(String actualString, String prefix) {
    return StringUtils.removeStart(actualString, prefix);
  }
}
