package toadstool;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(Theories.class)
public class PreparedStatmentBuilderTests {
    @Test
    public void ShouldBeConstructable() {
        // Given

        // When
        var builder = new PreparedStatementBuilder();

        // Then
        assertNotNull(builder);
    }

    @Test
    public void ShouldBeConstructableWithSql() {
        // Given
        var sql = "select yo mama";

        // When
        var builder = new PreparedStatementBuilder(sql);

        // Then
        assertNotNull(builder);
    }

    @Test(expected = NullPointerException.class)
    public void DoesNotAllowNullSql() {
        // Given
        String sql = null;

        // When
        var builder = new PreparedStatementBuilder(sql);

        // Then
        assertNull(builder);
    }

    @Test(expected = NullPointerException.class)
    public void DoesNotAllowNullContext() {
        // Given
        DatabaseContext context = null;

        // When
        var builder = new PreparedStatementBuilder().withContext(context);

        // Then
        assertNull(builder);
    }

    @DataPoints
    public static List<String> GOOD_PARAMETERS = List.of("foo", "foo_BAR", "foo_BAR_123", "7");
    @DataPoints
    public static List<String> BAD_PARAMETERS = List.of("foo ", "foo bar", "foo-bar", "foo/bar",
            "'; drop table students;");

    @Theory
    public void ShoulAllowValidParameterNames(String parameterName) {
        // Given
        assumeThat(parameterName, not(anyOf(containsString("/"), containsString(" "), containsString("-"))));
        var builder = new PreparedStatementBuilder();
        var value = 777;

        // When
        builder.withParameter(parameterName, value);

        // Then
    }

    @Theory
    public void ShouldNotAllowBadParameterNames(String parameterName) {
        // Given
        assumeThat(parameterName, anyOf(containsString("/"), containsString(" "), containsString("-")));
        var builder = new PreparedStatementBuilder();
        var value = 777;

        // When
        try {
            builder.withParameter(parameterName, value);
            assertFalse(true); // should not get here
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Then
    }

    @Test
    public void ShouldBeBuildable() throws SQLException {
        // Given
        var sql = "select yo mama";
        var builder = new PreparedStatementBuilder(sql);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(sql)).thenReturn(mock(PreparedStatement.class));

        // When
        var result = builder.build(connection);

        // Then
        assertNotNull(result);
    }

    public void ShouldFormatSql() throws SQLException {
        // Given
        var sql = "select yo mama";
        var builder = new PreparedStatementBuilder(sql);

        // When
        var result = builder.formatSql();

        // Then
        assertNotNull(result);
        assertEquals(sql, result.left);
        assertEquals(0, result.right.size());
    }

    public void ShouldFormatSqlWithParameters() throws SQLException {
        // Given
        var sql = "select 1 as a, 2 as b, 3 as c, 4 as d where 'bar' = @foo or 'bat' = @baz";
        var builder = new PreparedStatementBuilder(sql);
        builder.withParameter("foo", "bar").withParameter("baz", 7);

        // When
        var result = builder.formatSql();

        // Then
        assertNotNull(result);
        assertEquals("select 1 as a, 2 as b, 3 as c, 4 as d where 'bar' = ? or 'bat' = ?", result.left);
        assertEquals(2, result.right.size());
        assertEquals("bar", result.right.get(0));
        assertEquals(7, result.right.get(1));
    }

    public void ShouldFormatSqlWithRepeatedParameter() throws SQLException {
        // Given
        var sql = "select 1 as a, 2 as b, 3 as c, 4 as d where 'bar' = @foo or 'bat' = @foo or 'baz' = @foo or 'bag' = '@banana'";
        var builder = new PreparedStatementBuilder(sql);
        builder.withParameter("foo", "bar");

        // When
        var result = builder.formatSql();

        // Then
        assertNotNull(result);
        assertEquals(
                "select 1 as a, 2 as b, 3 as c, 4 as d where 'bar' = ? or 'bat' = ? or 'baz' = ? or 'bag' = '@banana'",
                result.left);
        assertEquals(3, result.right.size());
        assertEquals("bar", result.right.get(0));
        assertEquals("bar", result.right.get(1));
        assertEquals("bar", result.right.get(2));
    }
}