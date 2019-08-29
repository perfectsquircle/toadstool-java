package toadstool;

public class StockItem {
    private int stockItemID;
    private String stockItemName;
    private String brand;
    private boolean isChillerStock;
    private byte[] photo;

    public int getStockItemID() {
        return stockItemID;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public boolean isChillerStock() {
        return isChillerStock;
    }

    public void setIsChillerStock(boolean isChillerStock) {
        this.isChillerStock = isChillerStock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStockItemName() {
        return stockItemName;
    }

    public void setStockItemName(String stockItemName) {
        this.stockItemName = stockItemName;
    }

    public void setStockItemID(int stockItemID) {
        this.stockItemID = stockItemID;
    }
}