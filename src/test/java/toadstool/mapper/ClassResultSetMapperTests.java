package toadstool.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.Test;

public class ClassResultSetMapperTests {
    @Test
    public void ShouldBeConstructable() {
        // Given

        // When
        var mapper = new ClassResultSetMapper();

        // Then
        assertNotNull(mapper);
    }

    @Test
    public void ShouldMapResultSet() throws Exception {
        // Given
        var id = 777;
        var name = "Willy";
        var stockPrice = 77.77d;
        var createDate = new java.sql.Date(999);
        var resultSetMetadata = mock(ResultSetMetaData.class);
        when(resultSetMetadata.getColumnCount()).thenReturn(7);
        when(resultSetMetadata.getColumnName(1)).thenReturn("id");
        when(resultSetMetadata.getColumnName(2)).thenReturn("stock_price");
        when(resultSetMetadata.getColumnName(3)).thenReturn("name");
        when(resultSetMetadata.getColumnName(4)).thenReturn("create_date");
        when(resultSetMetadata.getColumnName(5)).thenReturn("cant_touch_this");
        when(resultSetMetadata.getColumnName(6)).thenReturn("integer_bob");
        when(resultSetMetadata.getColumnName(7)).thenReturn("nullable_bob");
        var resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(resultSetMetadata);
        when(resultSet.getInt("id")).thenReturn(id);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getDouble("stock_price")).thenReturn(stockPrice);
        when(resultSet.getDate("create_date")).thenReturn(createDate);
        when(resultSet.getString("cant_touch_this")).thenReturn("nope");
        when(resultSet.getObject("integer_bob")).thenReturn((Integer) 3);
        when(resultSet.getObject("nullable_bob")).thenReturn((Integer) null);
        var mapper = new ClassResultSetMapper();

        // When
        var result = mapper.MapResultSet(resultSet, Bar.class);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(stockPrice, result.getStockPrice(), 0.1d);
        assertEquals(createDate, result.getCreateDate());
        assertEquals("Stop. Hammer time.", result.getCantTouchThis());
        assertEquals((Integer) 3, result.getIntegerBob());
        assertEquals((Integer) null, result.getNullableBob());

    }
}