package org.maas.Objects;

import java.util.Vector;

public class Recipe{
    private int bakingTemp;
    private Vector<Step> steps; //inner class Step defined at the end of the file

    public Recipe(int bakingTemp, Vector<Step> steps) {
        this.bakingTemp = bakingTemp;
        this.steps = steps;
    }

    public int getBakingTemp() {
        return bakingTemp;
    }

    public void setBakingTemp(int bakingTemp) {
        this.bakingTemp = bakingTemp;
    }

    public Vector<Step> getSteps() {
        return steps;
    }

    public void setSteps(Vector<Step> steps) {
        this.steps = steps;
    }

    public float getActionTime(String action) {
        float duration = -1;

        for(Step step: steps) {
            if (step.getAction().equals(action)){
                duration = step.getDuration();
                break;
            }
        }

        return duration;
    }

    public Step getProofingStep() {
        Step proofingStep = new Step(null, null);
        for(Step step: steps) {
            if(step.getAction().equals(Step.PROOFING_STEP)) {
                proofingStep.setDuration(step.getDuration());
                proofingStep.setAction(step.getAction());
                break;
            }
        }
        return proofingStep;
    }

    public Vector<Step> getPreparationSteps(){
        Vector<Step> preparationSteps = new Vector<Step>();
        boolean passedKneading = false;

        // System.out.println("=============================================");
        // System.out.println(" Steps " + steps);

        for(Step step: steps) {
            if (passedKneading) {
                if (!step.getAction().equals(Step.PROOFING_STEP)) {
                    preparationSteps.add(step);
                }
                else {
                    break;
                }

            }
            if (step.getAction().equals(Step.KNEADING_STEP)) {
                // System.out.println("-------> Step =  " + step);
                passedKneading = true;
            }
        }
        return preparationSteps;

    }

    public Vector<Step> getBakingPreparationSteps(){
        Vector<Step> bakingPreparationSteps = new Vector<Step>();
        boolean passedBaking = false;

        for(Step step: steps) {
            if (passedBaking) {
                if (!step.getAction().equals(Step.COOLING_STEP)) {
                    bakingPreparationSteps.add(step);
                }
                else {
                    break;
                }

            }
            if (step.getAction().equals(Step.BAKING_STEP)) {
                // System.out.println("-------> Step =  " + step);
                passedBaking = true;
            }
        }
        return bakingPreparationSteps;

    }

    @Override
    public String toString() {
        return "Recipe [bakingTemp=" + bakingTemp + ", steps=" + steps + "]";
    }
}
