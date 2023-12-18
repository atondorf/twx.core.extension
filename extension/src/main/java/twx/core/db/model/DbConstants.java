package twx.core.db.model;

public class DbConstants {
    // TAG-Names for JSON IO of Model ... 
    public static final String MODEL_TAG_NAME                   = "name";       // used for all names in JSON
    public static final String MODEL_TAG_NOTE                   = "note";       // used for all notes in JSON

    public static final String MODEL_TAG_SCHEMA_ARRAY           = "schemas";    // used for schema in JSON    
    public static final String MODEL_TAG_TABLE_ARRAY            = "tables";     // used for tables in JSON  
    public static final String MODEL_TAG_COLUMN_ARRAY           = "columns";    // used for columsn in JSON
    public static final String MODEL_TAG_INDEX_ARRAY            = "indexes";    // used for indexes in JSON
    public static final String MODEL_TAG_FKKEYS_ARRAY           = "foreignKeys";

    public static final String MODEL_TAG_TYPE_NAME              = "typeName";
    public static final String MODEL_TAG_SQL_TYPE               = "sqlType";
    public static final String MODEL_TAG_TYPE_SIZE              = "size";
    public static final String MODEL_TAG_ORDINAL                = "ordinal";
    public static final String MODEL_TAG_UNIQUE                 = "unique";
    public static final String MODEL_TAG_NULLABLE               = "nullable";
    public static final String MODEL_TAG_PRIMARY_KEY            = "pk";
    public static final String MODEL_TAG_AUTOINCREMENT          = "autoIncrement";
    public static final String MODEL_TAG_DEFAULT_VALUE          = "defaultValue";
    // 
    public static final String MODEL_TAG_TWX_BASETYPE           = "twxType";
    public static final String MODEL_TAG_TWX_DATASHAPE          = "twxType";

    // foreign key special tags ... 
    public static final String MODEL_TAG_FOREIGN_SCHEMA         = "foreignSchema";
    public static final String MODEL_TAG_FOREIGN_TABLE          = "foreignTable";
    public static final String MODEL_TAG_FOREIGN_COLUMN         = "foreignColumn"; 

    public static final String MODEL_TAG_FOREIGN_ON_UPDATE      = "onUpdate";
    public static final String MODEL_TAG_FOREIGN_ON_DELETE      = "onDelete";

    public static final String MODEL_VALUE_FOREIGN_CASCADE      = "cascade";
    public static final String MODEL_VALUE_FOREIGN_RESTRICT     = "restrict";
    public static final String MODEL_VALUE_FOREIGN_SETNULL      = "setNull";
    public static final String MODEL_VALUE_FOREIGN_NOACTION     = "noAction";
    public static final String MODEL_VALUE_FOREIGN_SETDEFAULT   = "setDefault";



}
