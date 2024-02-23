package twx.core;

import com.thingworx.connectors.ParameterDefinition.In;
import com.thingworx.datashape.DataShape;
import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import liquibase.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.*;

import javax.sound.midi.MidiDevice.Info;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfotableIOUtil {
    final static Logger logger = LoggerFactory.getLogger(InfotableIOUtil.class);

    public static InfoTable getBatchSQLTable() {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("sql", BaseTypes.STRING ));
        table.addField(new FieldDefinition("result", BaseTypes.INTEGER));

        ValueCollection values = new ValueCollection();
        values.put("sql", new StringPrimitive("INSERT INTO dbo.tab_1 (valBool,valTinyInt,vaDateTime) VALUES (0,11,'2023-12-20T11:00:00Z')") );
        table.addRow(values);
        values = new ValueCollection();
        values.put("sql", new StringPrimitive("INSERT INTO dbo.tab_1 (valBool,valTinyInt,vaDateTime) VALUES (1,12,'2023-12-20T12:00:00Z')") );
        table.addRow(values);
        values = new ValueCollection();
        values.put("sql", new StringPrimitive("UPDATE dbo.tab_1 SET valStr='Hallo'") );
        table.addRow(values);
        return table;
    }

    public static InfoTable getTestTable() throws Exception {

        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("id", BaseTypes.LONG ));
        table.addField(new FieldDefinition("valBool", BaseTypes.BOOLEAN ));
        table.addField(new FieldDefinition("valTinyInt", BaseTypes.INTEGER ));
        table.addField(new FieldDefinition("valSmallInt", BaseTypes.INTEGER ));
        table.addField(new FieldDefinition("valInt", BaseTypes.INTEGER ));
        table.addField(new FieldDefinition("valBigInt", BaseTypes.LONG ));
        table.addField(new FieldDefinition("valReal", BaseTypes.NUMBER ));
        table.addField(new FieldDefinition("valFloat", BaseTypes.NUMBER ));
        table.addField(new FieldDefinition("valDecimal", BaseTypes.NUMBER ));
        table.addField(new FieldDefinition("vaDateTime", BaseTypes.DATETIME ));
        table.addField(new FieldDefinition("valFixStr", BaseTypes.STRING ));
        table.addField(new FieldDefinition("valStr", BaseTypes.STRING ));
        table.addField(new FieldDefinition("valFixBinary", BaseTypes.BLOB));
        table.addField(new FieldDefinition("valBinary", BaseTypes.BLOB));
        table.addField(new FieldDefinition("valImage", BaseTypes.IMAGE));
        table.addField(new FieldDefinition("valJSON", BaseTypes.JSON));
        table.addField(new FieldDefinition("valXML", BaseTypes.XML));


        table.addRow(getTestCollection_1());
        return table;
    }


    public static DataShapeDefinition getTestShape() throws Exception {
        DataShapeDefinition dsDef = new DataShapeDefinition();
        dsDef.addFieldDefinition(new FieldDefinition("id", BaseTypes.LONG ));
        dsDef.addFieldDefinition(new FieldDefinition("valBool", BaseTypes.BOOLEAN ));
        dsDef.addFieldDefinition(new FieldDefinition("valTinyInt", BaseTypes.INTEGER ));
        dsDef.addFieldDefinition(new FieldDefinition("valSmallInt", BaseTypes.INTEGER ));
        dsDef.addFieldDefinition(new FieldDefinition("valInt", BaseTypes.INTEGER ));
        dsDef.addFieldDefinition(new FieldDefinition("valBigInt", BaseTypes.LONG ));
        dsDef.addFieldDefinition(new FieldDefinition("valReal", BaseTypes.NUMBER ));
        dsDef.addFieldDefinition(new FieldDefinition("valFloat", BaseTypes.NUMBER ));
        dsDef.addFieldDefinition(new FieldDefinition("valDecimal", BaseTypes.NUMBER ));
        dsDef.addFieldDefinition(new FieldDefinition("valDateTime", BaseTypes.DATETIME ));
        dsDef.addFieldDefinition(new FieldDefinition("valFixStr", BaseTypes.STRING ));
        dsDef.addFieldDefinition(new FieldDefinition("valStr", BaseTypes.STRING ));
        dsDef.addFieldDefinition(new FieldDefinition("valFixBinary", BaseTypes.BLOB));
        dsDef.addFieldDefinition(new FieldDefinition("valBinary", BaseTypes.BLOB));
        dsDef.addFieldDefinition(new FieldDefinition("valImage", BaseTypes.IMAGE));
        dsDef.addFieldDefinition(new FieldDefinition("valJSON", BaseTypes.JSON));
        dsDef.addFieldDefinition(new FieldDefinition("valXML", BaseTypes.XML));
        return dsDef;
    }

    public static ValueCollection getTestCollection_1() throws Exception {

        ValueCollection values = new ValueCollection();
        values.SetBooleanValue("valBool", true);
        values.SetIntegerValue("valTinyInt", 1);
        values.SetIntegerValue("valSmallInt", 2);
        values.SetIntegerValue("valInt", 3);
        values.SetLongValue("valBigInt", 4);
        values.SetNumberValue("valReal", Math.PI );
        values.SetNumberValue("valFloat", Math.E );
        values.SetNumberValue("valDecimal", Math.PI);
        values.SetDateTimeValue("valDateTime", new DateTime() );
        values.SetStringValue("valFixStr", "FIX: Hallo Welt!");
        values.SetStringValue("valStr", "VAR: Hallo Welt");
/*
        values.SetBlo("valFixBinary", 1);
        values.SetIntegerValue("valBinary", 1);
        values.SetIntegerValue("valImage", 1);
        values.SetIntegerValue("valJSON", 1);
        values.SetIntegerValue("valXML", 1);
*/      
        return values;
    }

    public static String formatInfotable(InfoTable table) {
        // get the information of the table ... 
        List<FieldDefinition> fields = new ArrayList<FieldDefinition>(table.getDataShape().getFields().values());
        fields.sort( (f1, f2) -> { return f1.getOrdinal() - f2.getOrdinal(); } );
        String[]    colNames = fields.stream().map( field -> field.getName()).toArray(String[]::new);
        BaseTypes[] colTypes = fields.stream().map( field -> field.getBaseType()).toArray(BaseTypes[]::new);
        Integer[]   colSize  = fields.stream().map( field -> field.getName().length() ).toArray(Integer[]::new);
        // Iterate all rows / cols to find size of columns ... 
        for( var row : table.getRows() ) {
            for( int i = 0; i < colNames.length; i++ ) {
                String name = row.getStringValue(colNames[i]);
                colSize[i] =  Math.max( colSize[i], ( name != null ) ?  name.length() : 0 );
            }
        }
		StringWriter 	sw = new StringWriter();
        sw.append( "InfoTable:" );
        sw.append(System.lineSeparator());
        // write header ... 
        for( int i = 0; i < colNames.length; i++ ) {
            sw.append(" ");
            sw.append( StringUtil.pad(colNames[i], colSize[i] ));
            if( i < colNames.length - 1 )
                sw.append(" |");
        }
        sw.append(System.lineSeparator());
        for( int i = 0; i < colNames.length; i++ ) {
            sw.append( "-".repeat( colSize[i] + 2 ) );
            if( i < colNames.length - 1 )
                sw.append("+");
        }
        sw.append(System.lineSeparator());
        // write rows ... 
        for( var row : table.getRows() ) {
            for( int i = 0; i < colNames.length; i++ ) {
                String val = row.getStringValue(colNames[i]);
                if( val == null )
                    val = "Null";
                sw.append(" ");
                sw.append( StringUtil.pad( val, colSize[i] ));
                if( i < colNames.length - 1 )
                    sw.append(" |");
            }
            sw.append(System.lineSeparator());            
        }
        return sw.toString();
    }

}
