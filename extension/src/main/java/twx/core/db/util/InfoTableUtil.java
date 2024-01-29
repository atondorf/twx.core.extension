package twx.core.db.util;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import liquibase.util.StringUtil;

public class InfoTableUtil {
    
    public static InfoTable createInfoTableFromIntArray(int[] array) {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("result", BaseTypes.INTEGER ));
        for( int i : array ) {
            ValueCollection values = new ValueCollection();
            values.put("result", new IntegerPrimitive(i) );
            table.addRow( values );
        }
        return table;
    }

    public static InfoTable createInfoTableFromResultset(ResultSet rs) throws SQLException {
        InfoTable table = null;
        try { 
            table = SQLToInfoTableConversion.createInfoTableFromResultset(rs, null);
        }   
        catch(SQLException ex ) {
            table = null;
            throw ex;
        } catch(Exception ex)  { 
            table = null;
            throw new SQLException(ex);
        }
        return table;
    }

    public static InfoTable createTypedInfoTableFromResultset(ResultSet rs, DataShapeDefinition dsDef) throws SQLException {
        InfoTable table = null;
        try { 
            table = SQLToInfoTableConversion.createTypedInfoTableFromResultset(rs,dsDef,null);
        }   
        catch(SQLException ex ) {
            table = null;
            throw ex;
        } catch(Exception ex)  { 
            table = null;
            throw new SQLException(ex);
        }
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
