package toadstool;

import java.sql.SQLException;
import java.util.List;

public interface StatementExecutor {
    public <E> List<E> ToListOf(Class<E> targetClass) throws SQLException;

    public <E> E First() throws SQLException;

    public int Execute() throws SQLException;
}