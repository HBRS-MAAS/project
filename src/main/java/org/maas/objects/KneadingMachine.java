package org.maas.objects;

public class KneadingMachine extends Equipment {

    public KneadingMachine(String guid) {
        super(guid);
    }

    @Override
    public String toString() {
        return "KneadingMachine [guid=" + getGuid() + "]";
    }

}
