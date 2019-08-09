package toadstool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DefaultDatabaseContext implements DatabaseContext {
    private String url;

    public DefaultDatabaseContext(String url) {
        super();
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        var connection = DriverManager.getConnection(url);
        return connection;
    }

    @Override
    public StatementBuilder prepareStatement(String sql) throws SQLException {
        return new PreparedStatementBuilder(sql).withContext(this);
    }
}