package twx.core.date;

import com.thingworx.common.utils.DateUtilities;
import com.thingworx.dsl.engine.DSLConverter;
import org.mozilla.javascript.*;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.security.authentication.AuthenticationUtilities;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateScriptLibrary {
    
	//// Require  ////

    public static void requrire_core_date(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        //  ScriptableObject.defineClass(me, MultiTimer.class);
    }

    //// String Format and Tools  ////

    public static Object core_getAvailableTimeZones(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        InfoTable it = InfoTableInstanceFactory.createInfoTableFromDataShape("GenericStringList");
        for (var id : DateTimeZone.getAvailableIDs()) {
            ValueCollection row = new ValueCollection();
            row.put("item", new StringPrimitive(id));
            it.addRow(row);
        }
        return it;
    }

    public static Object core_getDefaultTimeZone(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        return DateServices._defaultTimeZone.getID();
    }

    public static Object date_setDefaultTimeZone(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in date_setDefaultTimeZone"); 
        StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
        DateServices._defaultTimeZone = DateTimeZone.forID( stringVal.getValue() );
        return null;
    }

    public static Object date_getTimeZoneOffset(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);
        long            current = System.currentTimeMillis();
        DateTimeZone    tz      = DateServices._defaultTimeZone;
        if ( args.length > 0 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        if( args.length > 1 ) {
            DatetimePrimitive dtVal = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.DATETIME );
            current = dtVal.getValue().getMillis();
        }
        return tz.getOffset( current );
    }

    public static Object date_getTimeZoneHasTransition(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);        
        long            current = System.currentTimeMillis();
        DateTimeZone    tz      = DateServices._defaultTimeZone;
        if ( args.length > 0 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        long next = tz.nextTransition(current);
        if( current != next )
            return true;
        return false;
    }

    public static Object date_getTimeZoneIsStdTransition(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);        
        long            current = System.currentTimeMillis();
        DateTimeZone    tz      = DateServices._defaultTimeZone;
        if ( args.length > 0 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        if( args.length > 1 ) {
            DatetimePrimitive dtVal = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.DATETIME );
            current = dtVal.getValue().getMillis();
        }
        return tz.isStandardOffset(current);
    }

    public static Object date_getTimeZoneNextTransition(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);
        long            current = System.currentTimeMillis();
        DateTimeZone    tz      = DateServices._defaultTimeZone;
        if ( args.length > 0 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        if( args.length > 1 ) {
            DatetimePrimitive dtVal = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.DATETIME );
            current = dtVal.getValue().getMillis();
        }
        DateTime result = new DateTime( tz.nextTransition(current) );
        return convertDate(cx, me, result);
    }

    public static Object date_getTimeZonePrevTransition(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DSLConverter.convertValues(args, me);
        long            current = System.currentTimeMillis();
        DateTimeZone    tz      = DateServices._defaultTimeZone;
        if ( args.length > 0 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        if( args.length > 1 ) {
            DatetimePrimitive dtVal = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.DATETIME );
            current = dtVal.getValue().getMillis();
        }
        DateTime result = new DateTime( tz.previousTransition(current) );
        return convertDate(cx, me, result);
    }

    public static Object date_formatTimeZoneISO(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DateTimeZone    tz = DateServices._defaultTimeZone;
        if (args.length < 1)
            throw new Exception("Invalid Number of Arguments in date_formatTimeZoneISO"); 
        DSLConverter.convertValues(args, me); 
        DatetimePrimitive dtVal = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.DATETIME );
        if ( args.length > 1 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        return dtVal.getValue().withZone(tz).toString();
    }

    public static Object date_formatTimeZone(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        DateTimeZone    tz = DateServices._defaultTimeZone;
        if (args.length < 2)
            throw new Exception("Invalid Number of Arguments in date_formatTimeZoneISO"); 
        DSLConverter.convertValues(args, me); 
        DatetimePrimitive   dtVal       = (DatetimePrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.DATETIME );
        StringPrimitive     dtFormat    = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[1], BaseTypes.STRING);
        if ( args.length > 2 ) {
            StringPrimitive stringVal = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[2], BaseTypes.STRING);
            tz = DateTimeZone.forID( stringVal.getValue() );
        }
        return dtVal.getValue().withZone(tz).toString(dtFormat.getValue());
    }


    protected static Object convertDate(Context cx, Scriptable scope, DateTime date) {
        try {
          AuthenticationUtilities.validateUserSecurityContext();
        } catch (Exception e) {
          throw new RuntimeException(e);
        } 
        if (date == null)
          return null; 
        Object[] args = { date.getMillis() };
        return cx.newObject(scope, "Date", args);
    }
}
