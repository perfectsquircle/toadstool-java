package toadstool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleDatabaseContext implements DatabaseContext {
    private String url;

    public SimpleDatabaseContext(String url) {
        super();
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection(url);
        return connection;
    }

    @Override
    public StatementBuilder prepareStatement(String sql) throws SQLException {
        if (sql == null) {
            throw new IllegalArgumentException("sql cannot be null");
        }
        return new PreparedStatementBuilder(sql).withContext(this);
    }
}