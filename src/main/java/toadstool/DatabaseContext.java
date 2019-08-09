package toadstool;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseContext {
    StatementBuilder prepareStatement(String sql) throws SQLException;

    Connection getConnection() throws SQLException;
}