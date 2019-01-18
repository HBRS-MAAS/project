package org.maas.Objects;

public class CoolingRequestTuple {
    private String guid;
    private int quantity;
    private float coolingDuration;

    public CoolingRequestTuple(String guid, float coolingDuration, int quantity) {
        this.guid = guid;
        this.coolingDuration = coolingDuration;
        this.quantity = quantity;
    }

    public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getCoolingDuration() {
		return coolingDuration;
	}

	public void setCoolingDuration(float coolingDuration) {
		this.coolingDuration = coolingDuration;
	}

	@Override
    public String toString() {
        return "CoolingRequest [guid=" + guid + ", coolingDuration=" + coolingDuration + ", quantity=" + quantity + "]";
    }
}
