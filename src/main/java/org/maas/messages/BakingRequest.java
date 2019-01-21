package org.maas.messages;
import java.util.Vector;

public class BakingRequest extends GenericGuidMessage {
    private int bakingTemp;
    private float bakingTime;
    private Vector<Integer> productQuantities;
    private Vector<Integer> slotsNeeded;
    private int productPerSlot;

    public BakingRequest(Vector<String> guids, String productType, int bakingTemp, Vector<Integer> slotsNeeded, float bakingTime, Vector<Integer> productQuantities, int productPerSlot) {
        super(guids, productType);
        this.bakingTemp = bakingTemp;
        this.bakingTime = bakingTime;
        this.productQuantities = productQuantities;
        this.slotsNeeded = slotsNeeded;
        this.productPerSlot = productPerSlot;
    }

    public int getBakingTemp() {
        return bakingTemp;
    }

    public void setBakingTemp(int bakingTemp) {
        this.bakingTemp = bakingTemp;
    }

    public int getProductPerSlot() {
        return productPerSlot;
    }

    public void setProductPerSlot(int productPerSlot) {
        this.productPerSlot = productPerSlot;
    }

    public Vector<Integer> getSlotsNeeded() {
        return slotsNeeded;
    }

    public void setSlotsNeeded(Vector<Integer> slotsNeeded) {
        this.slotsNeeded = slotsNeeded;
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
