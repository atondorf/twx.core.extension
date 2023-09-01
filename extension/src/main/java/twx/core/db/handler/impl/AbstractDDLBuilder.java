package twx.core.db.handler.impl;

import twx.core.db.handler.DDLBuilder;
import twx.core.db.handler.DbHandler;

public class AbstractDDLBuilder implements DDLBuilder {
       private DbHandler dbHandler = null;

    public AbstractDDLBuilder(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    } 
}
