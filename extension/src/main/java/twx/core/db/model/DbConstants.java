package twx.core.db.model;

public class DbConstants {

    public static final String DEFAULT_SCHEMA_NAME  = "dbo";

    public static final String MODEL_TAG_NAME                   = "name";
    public static final String MODEL_TAG_DESCRIPT               = "description";
    public static final String MODEL_TAG_SCHEMA_ARRAY           = "schemas";
    public static final String MODEL_TAG_TABLE_ARRAY            = "tables";
    public static final String MODEL_TAG_COLUMN_ARRAY           = "columns";
    public static final String MODEL_TAG_INDEX_ARRAY            = "indexes";
    public static final String MODEL_TAG_FKKEYS_ARRAY           = "foreignKeys";
	
    public static final String MODEL_TAG_TABLE_PRIMARY_KEY      = "primaryKey";

    public static final String MODEL_TAG_COLUMN_SQL_TYPE        = "sqlType";
    public static final String MODEL_TAG_COLUMN_TWX_TYPE        = "twxType";
    public static final String MODEL_TAG_COLUMN_SIZE	        = "size";
    public static final String MODEL_TAG_COLUMN_NULLABLE        = "nullable";
    public static final String MODEL_TAG_COLUMN_AUTOINCREMENT   = "autoIncrement";
    public static final String MODEL_TAG_COLUMN_PRIMARY_KEY     = "primaryKeySeq";

    public static final String MODEL_TAG_INDEX_LOCAL_COLUMN     = "name";
    public static final String MODEL_TAG_INDEX_UNIQUE           = "unique";
    public static final String MODEL_TAG_INDEX_ORDINAL          = "ordinal";
    public static final String MODEL_TAG_INDEX_FOREIGN_SCHEMA   = "foreignSchema";
    public static final String MODEL_TAG_INDEX_FOREIGN_TABLE    = "foreignTable";
    public static final String MODEL_TAG_INDEX_FOREIGN_COLUMN   = "foreignColumn";    
    public static final String MODEL_TAG_INDEX_ON_UPDATE        = "onUpdate";
    public static final String MODEL_TAG_INDEX_ON_DELETE        = "onDelete";
}
