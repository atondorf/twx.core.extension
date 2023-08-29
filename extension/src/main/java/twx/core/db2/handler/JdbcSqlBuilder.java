package twx.core.db2.handler;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcSqlBuilder {

    final static Logger logger = LoggerFactory.getLogger(JdbcModelManager.class);
    
    private DbHandler handler = null;
    private Connection connection = null;

    public JdbcSqlBuilder(DbHandler handler) {
        this.handler = handler;
    }

}
