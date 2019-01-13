package org.maas.messages;
import java.util.Vector;

public class BakingRequest extends GenericGuidMessage {
    private int bakingTemp;
    private float bakingTime;
    private Vector<Integer> productQuantities;

    public BakingRequest(Vector<String> guids, String productType, int bakingTemp, float bakingTime, Vector<Integer> productQuantities) {
        super(guids, productType);
        this.bakingTemp = bakingTemp;
        this.bakingTime = bakingTime;
        this.productQuantities = productQuantities;
    }

    public int getBakingTemp() {
        return bakingTemp;
    }

    public void setBakingTemp(int bakingTemp) {
        this.bakingTemp = bakingTemp;
    }

    public float getBakingTime() {
        return bakingTime;
    }

    public void setBakingTime(float bakingTime) {
        this.bakingTime = bakingTime;
    }

    public Vector<Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(Vector<Integer> quantities) {
        this.productQuantities = quantities;
    }

    @Override
    public String toString() {
        return "BakingRequest [bakingTemp=" + bakingTemp + ", bakingTime=" + bakingTime + ", quantities=" + productQuantities
                + "]";
    }
}
