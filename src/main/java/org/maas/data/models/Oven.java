package org.maas.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Oven {
    private String guid;
    private int coolingRate;
    private int heatingRate;

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setCoolingRate(int coolingRate) {
        this.coolingRate = coolingRate;
    }

    public int getCoolingRate() {
        return coolingRate;
    }

    public void setHeatingRate(int heatingRate) {
        this.heatingRate = heatingRate;
    }

    public int getHeatingRate() {
        return heatingRate;
    }
}
