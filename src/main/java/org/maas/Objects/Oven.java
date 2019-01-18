package org.maas.Objects;

import java.util.Vector;

public class Oven extends Equipment {
    public final static int NUM_SLOTS = 4;
    private int coolingRate;
    private int heatingRate;
    private Vector<OvenSlot> ovenSlots;

    public Oven(String guid, int coolingRate, int heatingRate) {
        super(guid);
        this.coolingRate = coolingRate;
        this.heatingRate = heatingRate;
        this.ovenSlots = new Vector<OvenSlot>();
        for (int i = 0; i < NUM_SLOTS; ++i)
        {
            this.ovenSlots.add(new OvenSlot(guid));
        }
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

    public Vector<OvenSlot> getOvenSlots() {
        return ovenSlots;
    }

    public void setOvenSlots(Vector<OvenSlot> ovenSlots) {
        this.ovenSlots = ovenSlots;
    }

    @Override
    public String toString() {
        return "Oven [coolingRate=" + coolingRate + ", heatingRate=" + heatingRate + ", ovenSlots=" + ovenSlots + "]";
    }
}
