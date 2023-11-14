package twx.core.db.handler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.text.TableView.TableRow;

import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTable;

public interface DDLReader {
    
    public DbModel queryModel() throws SQLException;
 

}
