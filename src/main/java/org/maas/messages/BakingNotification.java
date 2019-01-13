package org.maas.messages;

import java.util.Vector;

public class BakingNotification extends GenericGuidMessage {
    private Vector<Integer> productQuantities;

    public BakingNotification(Vector<String> guids, String productType, Vector<Integer> productQuantities) {
        super(guids, productType);
        this.productQuantities = productQuantities;
    }

    public Vector<Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setQuantities(Vector<Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    @Override
    public String toString() {
        return "BakingNotification [quantities=" + productQuantities + "]";
    }
}
