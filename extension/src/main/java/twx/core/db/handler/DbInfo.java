package twx.core.db.handler;

import java.util.HashSet;

public class DbInfo {
        
    private final HashSet<String>   systemSchemas = new HashSet<>();
    private final String            defaultSchema = null;

    public void addSystemSchema(String schema) {
        systemSchemas.add(schema);
    }

    public Boolean isSystemSchema(String schema) {
        String upperSchema = schema.toUpperCase();
        return this.systemSchemas.contains(upperSchema);
    }
    
}
