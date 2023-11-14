package twx.core.db.handler.impl;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.SQLBuilder;

public class AbstractSQLBuilder implements SQLBuilder {
    private DbHandler dbHandler = null;

    public AbstractSQLBuilder(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
}
