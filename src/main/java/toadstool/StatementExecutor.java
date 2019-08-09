package toadstool;

import java.sql.SQLException;
import java.util.List;

public interface StatementExecutor<E> {
    public List<E> ToList() throws SQLException;

    public E First() throws SQLException;

    public int Execute() throws SQLException;
}