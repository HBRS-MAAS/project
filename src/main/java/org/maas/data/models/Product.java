package org.maas.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String guid;
    private Batch batch;
    private Recipe recipe;
    private Packaging packaging;
    private float salesPrice;
    private float productionCost;

    public Product() {
        this.batch = new Batch();
        this.recipe = new Recipe();
        this.packaging = new Packaging();
    }

    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public Batch getBatch() {
        return batch;
    }
    public void setBatch(Batch batch) {
        this.batch = batch;
    }
    public Packaging getPackaging() {
        return packaging;
    }
    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }
    public Recipe getRecipe() {
        return recipe;
    }
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    public float getSalesPrice() {
        return salesPrice;
    }
    public void setSalesPrice(float salesPrice) {
        this.salesPrice = salesPrice;
    }
    public float getProductionCost() {
        return productionCost;
    }
    public void setProductionCost(float productionCost) {
        this.productionCost = productionCost;
    }
}
