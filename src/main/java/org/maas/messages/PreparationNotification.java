package org.maas.messages;

import java.util.Vector;

public class PreparationNotification extends GenericGuidMessage {
    private Vector<Integer> productQuantities;
    public PreparationNotification(Vector<String> guids, String productType, Vector<Integer> productQuantities) {
        super(guids, productType);
        this.productQuantities = productQuantities;
    }

    public Vector<Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(Vector<Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }


}
