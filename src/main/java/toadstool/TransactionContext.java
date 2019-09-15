package toadstool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import toadstool.builder.PreparedStatementBuilder;
import toadstool.builder.StatementBuilder;

class TransactionContext implements DatabaseContext, AutoCloseable {
    private final Connection connection;

    public TransactionContext(Connection connection) {
        super();
        Objects.requireNonNull(connection);
        this.connection = connection;
    }

    @Override
    public StatementBuilder prepareStatement(String sql) throws SQLException {
        Objects.requireNonNull(sql);
        return new PreparedStatementBuilder(sql)
                .withContext(this, false);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public TransactionContext beginTransaction() throws SQLException {
        return this;
    }

    @Override
    public void close() throws SQLException {
        if (!connection.isClosed()) {
            connection.rollback();
            connection.close();
        }
    }
}