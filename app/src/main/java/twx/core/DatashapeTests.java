package twx.core;

import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.types.primitives.BlobPrimitive;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.primitives.TimespanPrimitive;

import twx.core.db.handler.DbHandler;
import twx.core.db.handler.NamedPreparedStatementHandler;

public class DatashapeTests {
    final static Logger logger = LoggerFactory.getLogger(DbModelTests.class);

    private DbHandler db = null;

    public DatashapeTests(DbHandler dbHandler) {
        this.db = dbHandler;
    }

    public void runTests() throws Exception {
        this.test_3();
        
        this.test_ts();        
    }

    public void test_ts() {
        var dt = DateTime.now();
        Timestamp ts = new Timestamp( dt.getMillis() ); 

        logger.info("Joda-DT: {}", dt.withZone(DateTimeZone.UTC) );
        logger.info("SQL--TS: {}", ts );

    }

    public void test_1() throws Exception  {
        var tab = InfotableIOUtil.getTestTable();

        var vc = tab.getRow(0);
        for ( String key : vc.keySet() ) {
            var prim = vc.getPrimitive(key);
            logger.info("Key: {}, Idx: {}, BaseType: {}, Value: {}", key, 0, prim.getBaseType(), prim.getStringValue() );
        }
    }

    public void test_2() throws Exception {
/*
        String sql = "INSERT INTO dbo.tab_1 (valBool, valTinyInt, valSmallInt, valInt, valBigInt, valFloat, valDateTime, valTimeOff, valFixStr, valStr, valBinary, valJSON, valXML)";
        sql += "VALUES (@valBool, @valTinyInt, @valSmallInt, @valInt, @valBigInt, @valFloat, @valDateTime, @valTimeOff, @valFixStr, @valStr, @valBinary, @valJSON, @valXML)";
*/        
        String sql = "INSERT INTO dbo.tab_1 (valBool, valTinyInt, valSmallInt, valInt, valBigInt, valFloat, valDateTime, valTimeOff, valFixStr, valStr, valBinary, valJSON, valXML)";
        sql += "VALUES (@valBool, @valTinyInt, @valSmallInt, @valInt, @valBigInt, @valFloat, @valDateTime, @valTimeOff, @valFixStr, @valStr, @valBinary, @valJSON, @valXML)";

        try ( 
            var conn = db.getConnection();
            var stmt = new NamedPreparedStatementHandler( conn, sql ); 
            var purgeStmt = conn.createStatement();
        ) {
            purgeStmt.executeUpdate("TRUNCATE TABLE dbo.tab_1");

            // var vc = InfotableIOUtil.getTestCollection_1();
            // stmt.setFrom(vc);
            // stmt.executUpdate();
            stmt.setFrom(1, new BooleanPrimitive(true) );   //  valBool
            stmt.setFrom(2, new IntegerPrimitive(2) );      //  valTinyInt
            stmt.setFrom(3, new IntegerPrimitive(3) );      //  valSmallInt
            stmt.setFrom(4, new IntegerPrimitive(4) );      //  valInt
            stmt.setFrom(5, new IntegerPrimitive(5) );      //  valBigInt
            stmt.setFrom(6, new NumberPrimitive(Math.PI) ); //  valFloat
            stmt.setFrom(7, new DatetimePrimitive() );      //  valDateTime
            stmt.setFrom(8, new DatetimePrimitive() );      //  valTimeOff
            stmt.setFrom(9, new StringPrimitive("Hallo") );      //  valFixStr
            stmt.setFrom(10, new StringPrimitive("Hallo") );     //  valStr
            stmt.setFrom(11, null );     //  valBinary
            stmt.setFrom(12, null );     //  valJSON
            stmt.setFrom(13, null );     //  valXML
            stmt.executUpdate();

            stmt.setFrom(1, new BooleanPrimitive(false) );  //  valBool
            stmt.setFrom(2, new IntegerPrimitive(2) );      //  valTinyInt
            stmt.setFrom(3, new IntegerPrimitive(-3) );     //  valSmallInt
            stmt.setFrom(4, new IntegerPrimitive(-4) );     //  valInt
            stmt.setFrom(5, new IntegerPrimitive(-5) );     //  valBigInt
            stmt.setFrom(6, new NumberPrimitive(Math.E) );  //  valFloat
            stmt.setFrom(7, new DatetimePrimitive() );      //  valDateTime
            stmt.executUpdate();

            // check null values ... 
            stmt.setFrom(1, null );   //  valBool
            stmt.setFrom(2, null );   //  valTinyInt
            stmt.setFrom(3, null );   //  valSmallInt
            stmt.setFrom(4, null );   //  valInt
            stmt.setFrom(5, null );   //  valBigInt
            stmt.setFrom(6, null );   //  valFloat
            stmt.setFrom(7, null );   //  valDateTime
            stmt.setFrom(8, null );   //  valTimeOff
            stmt.setFrom(9, null );   //  valFixStr
            stmt.setFrom(10, null );  //  valStr            
            
            stmt.executUpdate();

            conn.commit();
        }
        catch( Exception ex ) {
            logger.error("Exception: {}", ex );
        }
    }

    public void test_3() throws Exception {
   /*
        String sql = "INSERT INTO dbo.tab_1 (valBool, valTinyInt, valSmallInt, valInt, valBigInt, valFloat, valDateTime, valTimeOff, valFixStr, valStr, valBinary, valJSON, valXML)";
        sql += "VALUES (@valBool, @valTinyInt, @valSmallInt, @valInt, @valBigInt, @valFloat, @valDateTime, @valTimeOff, @valFixStr, @valStr, @valBinary, @valJSON, @valXML)";
    */   
                String sql = "INSERT INTO dbo.tab_1 (valBinary)";
                sql += "VALUES (@valBinary)";
        
                try ( 
                    var conn = db.getConnection();
                    var stmt = new NamedPreparedStatementHandler( conn, sql ); 
                    var purgeStmt = conn.createStatement();
                ) {
                    purgeStmt.executeUpdate("TRUNCATE TABLE dbo.tab_1");
                    stmt.setFrom(1, new BlobPrimitive(  ) );      
                    stmt.executUpdate();
                    conn.commit();
                }
                catch( Exception ex ) {
                    logger.error("Exception: {}", ex );
                }
            }
}
