package toadstool.mapper;

import java.util.Date;

public class Bar {
    private int id;
    private String name;
    private double stockPrice;
    private String cantTouchThis = "Stop. Hammer time.";
    private Date createDate;
    private Integer nullableBob;
    private Integer integerBob;

    public int getId() {
        return id;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
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

    public Integer getNullableBob() {
        return nullableBob;
    }

    public void setNullableBob(Integer nullableBob) {
        this.nullableBob = nullableBob;
    }

    public Integer getIntegerBob() {
        return integerBob;
    }

    public void setIntegerBob(Integer integerBob) {
        this.integerBob = integerBob;
    }
}