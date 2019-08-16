package toadstool;

import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;

public class SimpleDatabaseContextTests {
    @Test
    public void ShouldBeConstructable() {
        // Given
        var url = "foobar";

        // When
        var context = new SimpleDatabaseContext(url);

        // Then
        assertNotNull(context);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldNotAllowNullUri() {
        // Given
        String url = null;

        // When
        var context = new SimpleDatabaseContext(url);

        // Then
        assertNull(context);
    }

    @Test
    public void ShouldPrepareStatement() throws SQLException {
        // Given
        var context = new SimpleDatabaseContext("nuffin");
        var sql = "select yo mama";

        // When
        var preparedStatement = context.prepareStatement(sql);

        // Then
        assertNotNull(preparedStatement);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldNotAllowNullSql() throws SQLException {
        // Given
        var context = new SimpleDatabaseContext("nuffin");
        String sql = null;

        // When
        var preparedStatement = context.prepareStatement(sql);

        // Then
        assertNull(preparedStatement);
    }
}