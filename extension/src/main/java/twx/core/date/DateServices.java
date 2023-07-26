package twx.core.date;

import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.data.util.InfoTableInstanceFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

public class DateServices extends Resource {

    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(DateServices.class);
    public static DateTimeZone _defaultTimeZone = DateTimeZone.getDefault();

    @ThingworxServiceDefinition(name = "getAvailableTimeZones", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {"isEntityDataShape:true", "dataShape:GenericStringList" })
    public InfoTable getAvailableTimeZones() throws Exception {
        InfoTable it = InfoTableInstanceFactory.createInfoTableFromDataShape("GenericStringList");
        for (var id : DateTimeZone.getAvailableIDs()) {
            ValueCollection row = new ValueCollection();
            row.put("item", new StringPrimitive(id));
            it.addRow(row);
        }
        return it;
    }

    @ThingworxServiceDefinition(name = "getDefaultTimeZone", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String getDefaultTimeZone() {
        return DateServices._defaultTimeZone.getID();
    }

    @ThingworxServiceDefinition(name = "setDefaultTimeZone", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void setDefaultTimeZone(
            @ThingworxServiceParameter(name = "tz", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String tz) {
        DateServices._defaultTimeZone = DateTimeZone.forID(tz);
    }

    @ThingworxServiceDefinition(name = "getTimeZoneOffset", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Offset to UTC in ms", baseType = "INTEGER", aspects = {})
    public Integer getTimeZoneOffset(
            @ThingworxServiceParameter(name = "dt", description = "Timestamp, if not given it's now()", baseType = "DATETIME") DateTime dt,
            @ThingworxServiceParameter(name = "tzId", description = "Id of the timezone, if not given selects default.", baseType = "STRING") String tzId
    ) {
        long current = System.currentTimeMillis();
        DateTimeZone tz = _defaultTimeZone;
        if (dt != null)
            current = dt.getMillis();
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        return tz.getOffset(current);
    }

    @ThingworxServiceDefinition(name = "hasTimeZoneTransition", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean hasTimeZoneTransition(
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId) {
        long current = System.currentTimeMillis();
        DateTimeZone tz = _defaultTimeZone;
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        long next = tz.nextTransition(current);
        if (current != next)
            return true;
        return false;
    }

    @ThingworxServiceDefinition(name = "getTimeZoneIsStdTransition", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean getTimeZoneIsStdTransition(
            @ThingworxServiceParameter(name = "dt", description = "", baseType = "DATETIME") DateTime dt,
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId
    ) {
        long            current = System.currentTimeMillis();
        DateTimeZone    tz = _defaultTimeZone;
        if( dt != null ) 
            current = dt.getMillis();
        if( tzId != null && tzId != "" )
            tz = DateTimeZone.forID( tzId );
        return tz.isStandardOffset( current );
    }

    @ThingworxServiceDefinition(name = "getTimeZoneNextTransition", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "DATETIME", aspects = {})
    public DateTime getTimeZoneNextTransition(
            @ThingworxServiceParameter(name = "dt", description = "", baseType = "DATETIME") DateTime dt,
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId
    ) {
        long current = System.currentTimeMillis();
        DateTimeZone tz = _defaultTimeZone;
        if (dt != null)
            current = dt.getMillis();
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        return new DateTime(tz.nextTransition(current));
    }

    @ThingworxServiceDefinition(name = "getTimeZonePrevTransition", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "DATETIME", aspects = {})
    public DateTime getTimeZonePrevTransition(
            @ThingworxServiceParameter(name = "dt", description = "", baseType = "DATETIME") DateTime dt,
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId
    ) {
        long current = System.currentTimeMillis();
        DateTimeZone tz = _defaultTimeZone;
        if (dt != null)
            current = dt.getMillis();
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        return new DateTime(tz.nextTransition(current));
    }

    @ThingworxServiceDefinition(name = "formatTimeZoneISO", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String formatTimeZoneISO(
            @ThingworxServiceParameter(name = "dt", description = "", baseType = "DATETIME", aspects = {"isRequired:true" }) DateTime dt,
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId
    ) {
        DateTimeZone tz = _defaultTimeZone;
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        return dt.withZone(tz).toString();
    }

    @ThingworxServiceDefinition(name = "formatTimeZone", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
    public String formatTimeZone(
            @ThingworxServiceParameter(name = "dt", description = "", baseType = "DATETIME", aspects = {"isRequired:true" }) DateTime dt,
            @ThingworxServiceParameter(name = "fmt", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String fmt,
            @ThingworxServiceParameter(name = "tzId", description = "", baseType = "STRING") String tzId
    ) {
        DateTimeZone tz = _defaultTimeZone;
        if (tzId != null && tzId != "")
            tz = DateTimeZone.forID(tzId);
        return dt.withZone(tz).toString(fmt);
    }

}
