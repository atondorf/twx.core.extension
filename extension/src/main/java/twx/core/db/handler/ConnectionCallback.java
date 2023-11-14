package twx.core.db.handler;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionCallback<T> {
    public T execute(Connection con) throws SQLException;
}
