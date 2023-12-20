package twx.core;

import com.thingworx.connectors.ParameterDefinition.In;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import liquibase.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.*;

import javax.sound.midi.MidiDevice.Info;

import java.io.StringWriter;
import java.io.PrintWriter;

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

    public static InfoTable getTestTable() {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("id", BaseTypes.INTEGER ));
        table.addField(new FieldDefinition("string", BaseTypes.STRING));
        table.addField(new FieldDefinition("ts", BaseTypes.DATETIME));

        ValueCollection values = new ValueCollection();
        values.put("id", new IntegerPrimitive(1) );
        values.put("string", new StringPrimitive("Hallo Test"));
        values.put("ts", new DatetimePrimitive() );
        table.addRow(values);
        return table;
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
