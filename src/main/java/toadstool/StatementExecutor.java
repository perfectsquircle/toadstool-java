package toadstool;

import java.sql.SQLException;
import java.util.List;

public interface StatementExecutor {
    public <E> List<E> toListOf(Class<E> targetClass) throws SQLException;

    public <E> E first() throws SQLException;

    public int execute() throws SQLException;
}