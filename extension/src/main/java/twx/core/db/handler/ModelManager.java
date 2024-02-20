package twx.core.db.handler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.text.TableView.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thingworx.types.InfoTable;

import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTable;

public interface ModelManager {

    public DbModel getModel();
    
    public void clearModel();

    public DbModel queryModel() throws Exception;

    public DbModel updateModel(InfoTable tableDesc) throws Exception;

    public InfoTable getTablesDesc();

    public InfoTable getTableColumnsDesc(String fullTableName);

    public InfoTable getTableColumnsDesc(String schemaName, String tableName);
}
