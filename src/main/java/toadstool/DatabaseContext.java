package toadstool;

import java.sql.Connection;
import java.sql.SQLException;

import toadstool.builder.StatementBuilder;

public interface DatabaseContext {
    StatementBuilder prepareStatement(String sql) throws SQLException;

    Connection getConnection() throws SQLException;

    TransactionContext beginTransaction() throws SQLException;
}