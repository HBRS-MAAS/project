package org.maas.Objects;

public class Batch {
    private int breadsPerOven;

    public Batch(int breadsPerOven) {
        super();
        this.breadsPerOven = breadsPerOven;
    }

    public int getBreadsPerOven() {
        return breadsPerOven;
    }

    public void setBreadsPerOven(int breadsPerOven) {
        this.breadsPerOven = breadsPerOven;
    }

    @Override
    public String toString() {
        return "Batch [breadsPerOven=" + breadsPerOven + "]";
    }
}
