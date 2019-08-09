package toadstool;

import java.sql.SQLException;

public interface DatabaseContext {
    StatementBuilder prepareStatement(String sql) throws SQLException;
}