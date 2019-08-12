package toadstool;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StatementExecutor {
    public <E> List<E> toListOf(Class<E> targetClass) throws SQLException;

    public <E> Optional<E> first(Class<E> targetClass) throws SQLException;

    public int execute() throws SQLException;
}