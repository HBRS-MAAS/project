package org.maas.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedProduct implements java.io.Serializable {
    private String guid;
    private int coolingDuration;
    private int quantity;
    private int remainingDurationTime;

    public ProcessedProduct(){
        this.remainingDurationTime = -1;
    }
    
    public void setGuid(String id) {
        this.guid = id;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setCoolingDuration(int coolingDuration) {
        this.coolingDuration = coolingDuration;
    }

    public int getCoolingDuration() {
        return coolingDuration;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setRemainingTimeDuration(int remainingDurationTime) {
        this.remainingDurationTime = remainingDurationTime;
    }

    public int getRemainingTimeDuration() {
        return remainingDurationTime;
    }

}
