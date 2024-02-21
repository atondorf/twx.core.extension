package twx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.NamedPreparedStatementHandler;
import twx.core.db.util.StatementUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementTest {
    final static Logger logger = LoggerFactory.getLogger(DbModelTests.class);

    private DbHandler db = null;

    public StatementTest(DbHandler dbHandler) {
        this.db = dbHandler;
    }

    public void runTests() {
        String sql = "select @val1, @val2 from tab_1";
        try ( var stmt = new NamedPreparedStatementHandler(db.getConnection(), sql); ) {

            logger.info( stmt.toJSON().toString(3) );
            logger.info( "index 1: {}, {}", stmt.hasField("val1"), stmt.getFieldIdx("val1") );
            logger.info( "index 2: {}, {}", stmt.hasField("val2"), stmt.getFieldIdx("val2") );
            logger.info( "index 3: {}, {}", stmt.hasField("val3"), stmt.getFieldIdx("val3") );
        } 
        catch( Exception ex ) {
            logger.error( "Caught Exception: {}", ex.getMessage() );
        }

    }
}
