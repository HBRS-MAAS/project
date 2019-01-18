package org.maas.Objects;

public class Packaging {
    private int boxingTemp;
    private int breadsPerBox;

    public Packaging(int boxingTemp, int breadsPerBox) {
        super();
        this.boxingTemp = boxingTemp;
        this.breadsPerBox = breadsPerBox;
    }

    public int getBoxingTemp() {
        return boxingTemp;
    }

    public void setBoxingTemp(int boxingTemp) {
        this.boxingTemp = boxingTemp;
    }

    public int getBreadsPerBox() {
        return breadsPerBox;
    }

    public void setBreadsPerBox(int breadsPerBox) {
        this.breadsPerBox = breadsPerBox;
    }

    @Override
    public String toString() {
        return "Packaging [boxingTemp=" + boxingTemp + ", breadsPerBox=" + breadsPerBox + "]";
    }
}
