package toadstool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class SimpleDatabaseContext implements DatabaseContext {
    private String url;

    public SimpleDatabaseContext(String url) {
        super();
        Objects.requireNonNull(url);
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection(url);
        return connection;
    }

    @Override
    public StatementBuilder prepareStatement(String sql) throws SQLException {
        Objects.requireNonNull(sql);
        return new PreparedStatementBuilder(sql).withContext(this);
    }
}