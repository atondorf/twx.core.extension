package twx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twx.core.db.handler.DbHandler;

public class DbHandlerTest {
    
    final static Logger logger = LoggerFactory.getLogger(DbHandlerTest.class);

    private DbHandler db = null;

    public DbHandlerTest(DbHandler dbHandler) {
        this.db = dbHandler;
    }

    public void runTests() throws Exception {
        this.query();
    }

    public void query() throws Exception {
        var table = db.executeQuery("SELECT * FROM dbo.tab_1");
        
        logger.info( InfotableIOUtil.formatInfotable(table) );
    }

}
