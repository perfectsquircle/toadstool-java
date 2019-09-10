package toadstool.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;

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
    public void ShouldCreateColumnToPropertyMap() throws Exception {
        // Given
        var resultSetMetadata = mock(ResultSetMetaData.class);
        when(resultSetMetadata.getColumnCount()).thenReturn(4);
        when(resultSetMetadata.getColumnName(1)).thenReturn("id");
        when(resultSetMetadata.getColumnName(2)).thenReturn("name");
        when(resultSetMetadata.getColumnName(3)).thenReturn("create_date");
        when(resultSetMetadata.getColumnName(4)).thenReturn("cant_touch_this");
        var mapper = new ClassResultSetMapper();
        var aSetter = Bar.class.getMethod("setId", int.class);
        var bSetter = Bar.class.getMethod("setName", String.class);
        var cSetter = Bar.class.getMethod("setCreateDate", Date.class);

        // When
        var result = mapper.createColumnToPropertyMap(Bar.class, resultSetMetadata);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(aSetter, result.get("id"));
        assertEquals(bSetter, result.get("name"));
        assertEquals(cSetter, result.get("create_date"));
        assertNull(result.get("cant_touch_this"));
    }

    @Test
    public void ShouldMapResultSet() throws Exception {
        // Given
        var id = 777;
        var name = "Willy";
        var stockPrice = (Double) null;
        var createDate = new Date();
        var resultSetMetadata = mock(ResultSetMetaData.class);
        when(resultSetMetadata.getColumnCount()).thenReturn(5);
        when(resultSetMetadata.getColumnName(1)).thenReturn("id");
        when(resultSetMetadata.getColumnName(2)).thenReturn("stock_price");
        when(resultSetMetadata.getColumnName(3)).thenReturn("name");
        when(resultSetMetadata.getColumnName(4)).thenReturn("create_date");
        when(resultSetMetadata.getColumnName(5)).thenReturn("cant_touch_this");
        var resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(resultSetMetadata);
        when(resultSet.getObject("id")).thenReturn(id);
        when(resultSet.getObject("name")).thenReturn(name);
        when(resultSet.getObject("stock_price")).thenReturn(stockPrice);
        when(resultSet.getObject("create_date")).thenReturn(createDate);
        when(resultSet.getObject("cant_touch_this")).thenReturn("nope");
        var mapper = new ClassResultSetMapper();

        // When
        var result = mapper.MapResultSet(resultSet, Bar.class);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(stockPrice, result.getStockPrice());
        assertEquals(createDate, result.getCreateDate());
        assertEquals("Stop. Hammer time.", result.getCantTouchThis());

    }
}