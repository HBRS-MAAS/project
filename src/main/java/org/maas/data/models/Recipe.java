package org.maas.data.models;

import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {
    private int coolingRate;
    private int bakingTemp;
    private List<Step> steps;

    public Recipe() {
        this.steps = new Vector<Step>();
    }

    public void setCoolingRate(int coolingRate) {
        this.coolingRate = coolingRate;
    }

    public int getCoolingRate() {
        return coolingRate;
    }

    public void setBakingTemp(int bakingTemp) {
        this.bakingTemp = bakingTemp;
    }

    public int getBakingTemp() {
        return bakingTemp;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
