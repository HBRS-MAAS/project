package org.right_brothers.data.messages;

import java.util.Vector;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnbakedProductMessage implements java.io.Serializable {
    private String productType;
    private Vector<String> guids; 
    private Vector<Integer> productQuantities;
    
	public UnbakedProductMessage() {
        this.guids = new Vector<String> ();
        this.productQuantities = new Vector<Integer> ();
	}

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductType() {
        return productType;
    }

    public void setGuids(Vector<String> guids) {
        this.guids = guids;
    }

    public Vector<String> getGuids() {
        return guids;
    }

    public void setProductQuantities(Vector<Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    public Vector<Integer> getProductQuantities() {
        return productQuantities;
    }
}
