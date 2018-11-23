package org.maas.data.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BakedProductMessage implements java.io.Serializable {
    private String guid;
    private int coolingRate;
    private int coolingDuration;
    private int quantity;
    
    public void setGuid(String id) {
        this.guid = id;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setCoolingRate(int coolingRate) {
        this.coolingRate = coolingRate;
    }

    public int getCoolingRate() {
        return coolingRate;
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

}
