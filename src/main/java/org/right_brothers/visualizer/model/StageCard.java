package org.right_brothers.visualizer.model;

public abstract class StageCard {
	protected String bakeryId;
	
	protected StageCard(String bakeryId) {
		this.bakeryId = bakeryId;
	}

	public String getBakeryId() {
		return bakeryId;
	}

	public void setBakeryId(String bakeryId) {
		this.bakeryId = bakeryId;
	}
	
	public abstract boolean isComplete();
}
