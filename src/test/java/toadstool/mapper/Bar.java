package toadstool.mapper;

import java.util.Date;

public class Bar {
    private int id;
    private String name;
    private Double stockPrice;
    private String cantTouchThis = "Stop. Hammer time.";
    private Date createDate;

    public int getId() {
        return id;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getName() {
        return name;
    }

    public String getCantTouchThis() {
        return cantTouchThis;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}