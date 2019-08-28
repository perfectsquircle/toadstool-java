package toadstool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class SimpleDatabaseContext implements DatabaseContext {
    private final String url;
    private final Optional<String> user;
    private final Optional<String> password;

    public SimpleDatabaseContext(String url) {
        super();
        Objects.requireNonNull(url);
        this.url = url;
        this.user = Optional.empty();
        this.password = Optional.empty();
    }

    public SimpleDatabaseContext(String url, String user, String password) {
        super();
        Objects.requireNonNull(url);
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        this.url = url;
        this.user = Optional.of(user);
        this.password = Optional.of(password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (user.isPresent() && password.isPresent()) {
            return DriverManager.getConnection(url, user.get(), password.get());
        } else {
            return DriverManager.getConnection(url);
        }
    }

    @Override
    public StatementBuilder prepareStatement(String sql) throws SQLException {
        Objects.requireNonNull(sql);
        return new PreparedStatementBuilder(sql).withContext(this);
    }
}