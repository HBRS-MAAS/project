package org.right_brothers.visualizer.model;

public class PackagingStageCard extends StageCard {
	private CardItem product;
	
	public PackagingStageCard() {
		super("");
	}
	
	public PackagingStageCard(String bakeryId, CardItem product) {
		super(bakeryId);
		this.product  = product;
	}

	public CardItem getProduct() {
		return product;
	}

	public void setProduct(CardItem product) {
		this.product = product;
	}
	
	public boolean isComplete() {
		return !(product != null && product.getQuantity() > 0);
	}
}
