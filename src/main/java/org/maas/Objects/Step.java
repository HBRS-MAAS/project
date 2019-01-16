package org.maas.Objects;

public class Step
{
    private String action;
    private Float duration;
    public final static String KNEADING_STEP = "kneading";
    public final static String ITEM_PREPARATION_STEP = "item preparation";
    public final static String PROOFING_STEP = "proofing";
    public final static String BAKING_STEP = "baking";
    public final static String COOLING_STEP =  "cooling";


    public Step(String action, Float duration2) {
        this.action = action;
        this.duration = duration2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Step [action=" + action + ", duration=" + duration + "]";
    }
}
