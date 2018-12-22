package org.maas.data.models;

import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Equipment {
    private List<Oven> ovens;
    private List<DoughPrepTable> doughPrepTables;
    private List<KneadingMachine> kneadingMachines;

    public Equipment() {
        this.ovens = new Vector<Oven>();
        this.doughPrepTables = new Vector<DoughPrepTable>();
        this.kneadingMachines = new Vector<KneadingMachine>();
    }

    public void setOvens(List<Oven> ovens) {
        this.ovens = ovens;
    }

    public List<Oven> getOvens() {
        return ovens;
    }

    public void setDoughPrepTables(List<DoughPrepTable> doughPrepTables) {
        this.doughPrepTables = doughPrepTables;
    }

    public List<DoughPrepTable> getDoughPrepTables() {
        return doughPrepTables;
    }

    public void setKneadingMachines(List<KneadingMachine> kneadingMachines) {
        this.kneadingMachines = kneadingMachines;
    }

    public List<KneadingMachine> getKneadingMachines() {
        return kneadingMachines;
    }
}
