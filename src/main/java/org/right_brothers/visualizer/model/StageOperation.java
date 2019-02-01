package org.right_brothers.visualizer.model;

public class StageOperation<C extends StageCard> {
	private StageOperationType operationType;
	private int index;
	private C card;
	
	
	public StageOperation(StageOperationType operationType, int index, C card) {
		this.operationType = operationType;
		this.index = index;
		this.card = card;
	}
	
	public C getCard() {
		return card;
	}
	public void setCard(C card) {
		this.card = card;
	}
	public StageOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(StageOperationType operationType) {
		this.operationType = operationType;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
