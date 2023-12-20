package twx.core.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.thingworx.metadata.DataShapeDefinition;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.things.database.SQLToInfoTableConversion;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

public class InfoTableUtil {
    
    public static InfoTable createInfoTableFromIntArray(int[] array) {
        InfoTable table = new InfoTable();
        table.addField(new FieldDefinition("rows", BaseTypes.INTEGER ));
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
}
