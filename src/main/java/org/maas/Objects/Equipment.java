package org.maas.Objects;

abstract public class Equipment {
    private String guid;
    private boolean isAvailable;

    public Equipment(String guid) {
        this.guid = guid;
        this.isAvailable = true;
    }

    public Equipment(String guid, boolean isAvailable) {
        this.guid = guid;
        this.isAvailable = isAvailable;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Equipment [guid=" + guid + ", isAvailable=" + isAvailable + "]";
    }
}
