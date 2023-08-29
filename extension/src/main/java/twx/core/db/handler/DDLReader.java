package twx.core.db.handler;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.text.TableView.TableRow;

import twx.core.db.model.DbForeignKey;
import twx.core.db.model.DbModel;
import twx.core.db.model.DbTable;

public interface DDLReader {
    
    public DbModel readTables(String catalog, String schema, String[] tableTypes);

    public DbTable readTable(String catalog, String schema, String tableName);

    public List<String> getTableTypes();

    public List<String> getCatalogNames();

    public List<String> getSchemaNames(String catalog);

    public List<String> getTableNames(String catalog, String schema, String[] tableTypes);

    public List<String> getColumnNames(String catalog, String schema, String tableName);

    public Collection<DbForeignKey> getExportedKeys(DbTable table);
}
