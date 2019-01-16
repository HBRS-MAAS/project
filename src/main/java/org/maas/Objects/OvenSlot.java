package org.maas.Objects;

public class OvenSlot {
    public final static float STARTING_TEMP = 0;
    private float currentTemp;
    private String ovenGuid;
    private boolean available;

    public OvenSlot(String ovenGuid) {
        super();
        this.currentTemp = STARTING_TEMP;
        this.ovenGuid = ovenGuid;
        this.available = true;
    }

    public OvenSlot(String ovenGuid, boolean available) {
        super();
        this.currentTemp = STARTING_TEMP;
        this.ovenGuid = ovenGuid;
        this.available = available;
    }

    public OvenSlot(float currentTemp, String ovenGuid, boolean available) {
        super();
        this.currentTemp = currentTemp;
        this.ovenGuid = ovenGuid;
        this.available = available;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getOvenGuid() {
        return ovenGuid;
    }

    public void setOvenGuid(String ovenGuid) {
        this.ovenGuid = ovenGuid;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "OvenSlot [currentTemp=" + currentTemp + ", ovenGuid=" + ovenGuid + ", available=" + available + "]";
    }
}
