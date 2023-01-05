package toadstool;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WideWorldImportersTests {
    @Parameters(name = "connectionStrings")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "jdbc:postgresql://localhost:54321/wide_world_importers_pg", "toadstool", "toadstool" },
                { "jdbc:sqlserver://localhost:1433;Database=WideWorldImporters", "SA", "Toadstool123" },
        });
    }

    private String connectionString;
    private String user;
    private String password;
    private boolean isPostgres;

    public WideWorldImportersTests(String connectionString, String user, String password) {
        this.connectionString = connectionString;
        this.user = user;
        this.password = password;
        this.isPostgres = connectionString.contains("postgresql");
    }

    @Test
    public void shouldSelectFromStockItems() throws Exception {
        // Given
        var query = isPostgres
                ? "SELECT stock_item_id, stock_item_name FROM warehouse.stock_items ORDER BY stock_item_id LIMIT 10"
                : "SELECT TOP 10 StockItemID, StockItemName FROM Warehouse.StockItems ORDER BY StockItemID";
        var context = new SimpleDatabaseContext(connectionString, user, password);

        // When
        var results = context.prepareStatement(query).toListOf(StockItem.class);

        // Then
        assertNotNull(results);
        assertEquals(10, results.size());
        var first = results.get(0);
        assertEquals(1, first.getStockItemID());
        assertEquals("USB missile launcher (Green)", first.getStockItemName());
        var last = results.get(9);
        assertEquals(10, last.getStockItemID());
        assertEquals("USB food flash drive - chocolate bar", last.getStockItemName());
        assertEquals("USB food flash drive - chocolate bar", last.getStockItemName());
    }

    @Test

    public void ShouldSelectFromStockItemsWithParameters() throws Exception {
        // Given
        var query = isPostgres
                ? "SELECT stock_item_id, stock_item_name FROM warehouse.stock_items WHERE supplier_id = @supplierId AND tax_rate = @taxRate ORDER BY stock_item_id"
                : "SELECT StockItemID, StockItemName FROM Warehouse.StockItems WHERE SupplierId = @supplierId AND TaxRate = @taxRate ORDER BY StockItemID";
        var context = new SimpleDatabaseContext(connectionString, user, password);

        // When
        var results = context
                .prepareStatement(query)
                .withParameter("supplierId", 2)
                .withParameter("brand", null)
                .withParameter("taxRate", 15.0)
                .toListOf(StockItem.class);

        // Then
        assertNotNull(results);
        assertEquals(3, results.size());
        var first = results.get(0);
        assertEquals(150, first.getStockItemID());
        assertEquals("Pack of 12 action figures (variety)", first.getStockItemName());
        var last = results.get(2);
        assertEquals(152, last.getStockItemID());
        assertEquals("Pack of 12 action figures (female)", last.getStockItemName());
    }

    @Test
    public void ShouldSelectBoolean() throws Exception {
        // Given
        var query = isPostgres
                ? "SELECT stock_item_id, stock_item_name, is_chiller_stock FROM warehouse.stock_items WHERE is_chiller_stock = true"
                : "SELECT StockItemID, StockItemName, IsChillerStock FROM Warehouse.StockItems WHERE IsChillerStock = 1";
        var context = new SimpleDatabaseContext(connectionString, user, password);

        // When
        var results = context.prepareStatement(query).toListOf(StockItem.class);

        // Then
        assertNotNull(results);
        for (var result : results) {
            assertEquals(true, result.isChillerStock());
        }
    }
}
