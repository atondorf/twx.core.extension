package twx.core;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twx.core.db.handler.DbHandler;

public class DbModelTests {
    final static Logger logger = LoggerFactory.getLogger(DbModelTests.class);

    private DbHandler db = null;

    public DbModelTests(DbHandler dbHandler) {
        this.db = dbHandler;
    }

    public void runTests() throws Exception {
        logger.info("---------- queryModel ----------");
        var model = db.getModelManager().updateModel();
        model.setNote("This is a note at the model"); 

        logger.info( model.toJSON().toString() );
    }

}
