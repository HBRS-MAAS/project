package org.maas.objects;

public class ProductStatus {
    private String guid;
    private String status;
    private int amount;
    private ProductMas product;

    public ProductStatus(String guid, String status, int amount, ProductMas product) {
        this.guid = guid;
        this.status = status;
        this.amount = amount;
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getGuid() {
        return guid;
    }

    public int getAmount() {
        return amount;
    }

    public ProductMas getProduct() {
        return product;
    }

}
