package twx.core.db.handler.mssql;

import java.util.Collection;
import java.util.List;

import twx.core.db.handler.DDLReader;
import twx.core.db.handler.DbHandler;
import twx.core.db.handler.impl.AbstractDDLReader;
import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTable;

public class MsSqlDDLReader extends  AbstractDDLReader {

    public MsSqlDDLReader(DbHandler dbHandler) {
        super(dbHandler);
    }

    
    
}
