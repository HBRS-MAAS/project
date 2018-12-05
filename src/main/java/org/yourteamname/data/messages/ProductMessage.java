package org.maas.data.messages;

import java.util.Hashtable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductMessage implements java.io.Serializable {
    private Hashtable<String,Integer> products;
    
    public ProductMessage() {
        this.setProducts(new Hashtable<>());
    }

    public void setProducts(Hashtable<String,Integer> products) {
        this.products = products;
    }

    public Hashtable<String,Integer> getProducts() {
        return products;
    }
}
