package twx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.metadata.DataShapeDefinition;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.PreparedStatementHandler;
import twx.core.db.util.StatementUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementTest {
    final static Logger logger = LoggerFactory.getLogger(DbModelTests.class);

    private DbHandler db = null;
    private DataShapeDefinition dsDef = null;

    public StatementTest(DbHandler dbHandler) throws Exception {
        this.db = dbHandler;
        this.dsDef = InfotableIOUtil.getTestShape();            
    }

    public void runTests() throws Exception {
        test_2();
    }

    public void test_1() throws Exception {
        String sql = "INSERT INTO dbo.tab_1 (valBool, valTinyInt, valSmallInt, valInt, valBigInt, valReal, valFloat, valDecimal, valDateTime, valFixStr, valStr, valFixBinary, valBinary, valImage, valJSON, valXML)";
        sql += "VALUES (@valBool, @valTinyInt, @valSmallInt, @valInt, @valBigInt, @valReal, @valFloat, @valDecimal, @valDateTime, @valFixStr, @valStr, @valFixBinary, @valBinary, @valImage, @valJSON, @valXML)";

        try ( 
            var conn = db.getConnection();
            var prepStmt = new PreparedStatementHandler( conn, sql, dsDef ); 
            var purgeStmt = conn.createStatement();
        ) {
            logger.info( prepStmt.toJSON().toString(3) );

            purgeStmt.executeUpdate("TRUNCATE TABLE dbo.tab_1");
            prepStmt.set( InfotableIOUtil.getTestCollection_1() );
            prepStmt.executUpdate();
            conn.commit();
        } 
    }

    public void test_2() throws Exception {

        String sql = "INSERT INTO dbo.tab_1 (valBool, valTinyInt, valSmallInt, valInt, valBigInt, valReal, valFloat, valDecimal )";
        sql += "VALUES (@valBool, @valTinyInt, @valTinyInt,  @valSmallInt, @valSmallInt, @valReal, @valReal, @valReal)";

        try ( 
            var conn = db.getConnection();
            var prepStmt = new PreparedStatementHandler( conn, sql, this.dsDef ); 
        ) {
            logger.info(prepStmt.toJSON().toString(3));
            prepStmt.set( InfotableIOUtil.getTestCollection_1() );
            prepStmt.executUpdate();
            conn.commit();
        }
    }
}
