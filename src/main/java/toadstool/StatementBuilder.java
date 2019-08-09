package toadstool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementBuilder extends StatementExecutor {
    public StatementBuilder withParameter(String key, Object value);

    public PreparedStatement build(Connection connection) throws SQLException;
}