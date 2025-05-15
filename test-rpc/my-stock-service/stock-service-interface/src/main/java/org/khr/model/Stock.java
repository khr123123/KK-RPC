package org.khr.model;

public class Stock {
    private Long id;
    private String product;
    private Integer stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", stock=" + stock +
                '}';
    }
}
