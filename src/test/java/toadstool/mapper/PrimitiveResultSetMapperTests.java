package toadstool.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

public class PrimitiveResultSetMapperTests {
    @Test
    public void ShouldBeConstructable() {
        // Given

        // When
        var mapper = new PrimitiveResultSetMapper();

        // Then
        assertNotNull(mapper);
    }

    @Test
    public void ShouldMapString() throws Exception {
        // Given
        var value = "Willy";
        var resultSet = buildResultSet();
        when(resultSet.getString(1)).thenReturn(value);
        var mapper = new PrimitiveResultSetMapper();

        // When
        var result = mapper.MapResultSet(resultSet, String.class);

        // Then
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void ShouldMapBoolean() throws Exception {
        // Given
        var value = true;
        var resultSet = buildResultSet();
        when(resultSet.getBoolean(1)).thenReturn(value);
        var mapper = new PrimitiveResultSetMapper();

        // When
        var result = mapper.MapResultSet(resultSet, boolean.class);

        // Then
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void ShouldMapInteger() throws Exception {
        // Given
        var value = 777;
        var resultSet = buildResultSet();
        when(resultSet.getInt(1)).thenReturn(value);
        var mapper = new PrimitiveResultSetMapper();

        // When
        int result = mapper.MapResultSet(resultSet, int.class);

        // Then
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void ShouldMapDouble() throws Exception {
        // Given
        var value = 77.77d;
        var resultSet = buildResultSet();
        when(resultSet.getDouble(1)).thenReturn(value);
        var mapper = new PrimitiveResultSetMapper();

        // When
        double result = mapper.MapResultSet(resultSet, double.class);

        // Then
        assertNotNull(result);
        assertEquals(value, result, 0.001);
    }

    private ResultSet buildResultSet() throws SQLException {
        var resultSetMetadata = mock(ResultSetMetaData.class);
        when(resultSetMetadata.getColumnCount()).thenReturn(1);
        when(resultSetMetadata.getColumnName(1)).thenReturn("first_column");
        var resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(resultSetMetadata);
        return resultSet;
    }
}