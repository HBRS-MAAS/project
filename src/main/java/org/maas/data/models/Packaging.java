package org.maas.data.models;

public class Packaging {
    private int breadsPerBox;
    private int boxingTemp;

    public void setBreadsPerBox(int breadsPerBox) {
        this.breadsPerBox = breadsPerBox;
    }

    public int getBreadsPerBox() {
        return breadsPerBox;
    }

    public void setBoxingTemp(int boxingTemp) {
        this.boxingTemp = boxingTemp;
    }

    public int getBoxingTemp() {
        return boxingTemp;
    }
}
