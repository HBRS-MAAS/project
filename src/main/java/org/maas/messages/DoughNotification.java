package org.maas.messages;

import java.util.Vector;

public class DoughNotification extends GenericGuidMessage {
    Vector<Integer> productQuantities;

    public DoughNotification(Vector<String> guids, String productType, Vector<Integer> productQuantities) {
        super(guids, productType);
        this.productQuantities = productQuantities;
    }

    public Vector<Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(Vector<Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    @Override
    public String toString() {
        return "DoughNotification [productQuantities=" + productQuantities + "]";
    }
}
