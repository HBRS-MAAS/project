package org.maas.Objects;

public class OvenSlot {
    public final static float STARTING_TEMP = 0;
    private float currentTemp;
    private String ovenGuid;
    private String guid;
    private Integer quantity;
    private boolean available;
    private boolean readyToBake;
    private String productType;
    private int coolingRate;
    private int heatingRate;
    private float bakingTime;
    private float bakingTemp;
    private int bakingCounter;

    public OvenSlot(String ovenGuid) {
        super();
        this.currentTemp = STARTING_TEMP;
        this.ovenGuid = ovenGuid;
        this.guid = null;
        this.quantity = 0;
        this.available = true;
        this.readyToBake = false;
        this.productType = null;
        this.coolingRate = 0;
        this.heatingRate = 0;
        this.bakingTime = 0;
        this.bakingTemp = 0;
        this.bakingCounter = 0;
    }

    public OvenSlot(String ovenGuid, boolean available) {
        super();
        this.currentTemp = STARTING_TEMP;
        this.ovenGuid = ovenGuid;
        this.guid = null;
        this.quantity = 0;
        this.available = available;
        this.readyToBake = false;
        this.productType = null;
        this.coolingRate = 0;
        this.heatingRate = 0;
        this.bakingTime = 0;
        this.bakingTemp = 0;
        this.bakingCounter = 0;
    }

    public OvenSlot(float currentTemp, String ovenGuid, boolean available) {
        super();
        this.currentTemp = currentTemp;
        this.ovenGuid = ovenGuid;
        this.guid = null;
        this.quantity = 0;
        this.available = available;
        this.readyToBake = false;
        this.productType = null;
        this.coolingRate = 0;
        this.heatingRate = 0;
        this.bakingTime = 0;
        this.bakingTemp = 0;
        this.bakingCounter = 0;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getBakingCounter() {
        return bakingCounter;
    }

    public void setBakingCounter(int bakingCounter) {
        this.bakingCounter = bakingCounter;
    }

    public float getBakingTime() {
        return bakingTime;
    }

    public void setBakingTime(float bakingTime) {
        this.bakingTime = bakingTime;
    }

    public int getCoolingRate() {
        return coolingRate;
    }

    public void setCoolingRate(int coolingRate) {
        this.coolingRate = coolingRate;
    }

    public int getHeatingRate() {
        return heatingRate;
    }

    public void setHeatingRate(int heatingRate) {
        this.heatingRate = heatingRate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    public float getBakingTemp() {
        return bakingTemp;
    }

    public void setBakingTemp(float bakingTemp) {
        this.bakingTemp = bakingTemp;
    }

    public String getOvenGuid() {
        return ovenGuid;
    }

    public void setOvenGuid(String ovenGuid) {
        this.ovenGuid = ovenGuid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isReadyToBake() {
        return readyToBake;
    }

    public void setReadyToBake(boolean ready) {
        this.readyToBake = ready;
    }

    @Override
    public String toString() {
        return "OvenSlot [currentTemp=" + currentTemp + ", ovenGuid=" + ovenGuid + ", available=" + available + "]";
    }
}
