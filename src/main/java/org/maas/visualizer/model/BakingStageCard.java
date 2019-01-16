package org.right_brothers.visualizer.model;

import java.util.ArrayList;
import java.util.List;

public class BakingStageCard extends StageCard {
	private String productId;
	private List<CardItem> orders;
	
	public BakingStageCard() {
		super("");
		
		setProductId("");
		setOrders(new ArrayList<>());
	}
	
	public BakingStageCard(String bakeryId, String productId, List<CardItem> orders) {
		super(bakeryId);
		
		this.setProductId(productId);
		this.setOrders(orders != null? orders: new ArrayList<>());
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public List<CardItem> getOrders() {
		return orders;
	}

	public void setOrders(List<CardItem> orders) {
		this.orders = orders;
	}
	
	public boolean isComplete() {
		for(CardItem item: orders) {
			if(item.getQuantity() > 0) {
				return false;
			}
		}
		return true;
	}
}
