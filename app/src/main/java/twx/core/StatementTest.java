package twx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twx.core.db.handler.DbHandler;
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
        var util = new StatementUtil(sql);

        logger.info( util.toJSON().toString(3) );


        final String regex = "(@\\w*)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(sql);

        String result1 = matcher.replaceAll("?");
        

        String result2 = sql.replace(regex,"?");

        logger.info( "Parsed String: {}", result1);
        logger.info( "Parsed String: {}", result2);


    }
}
