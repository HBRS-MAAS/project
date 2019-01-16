package org.right_brothers.visualizer.model;

public class CardItem {
	private String itemText;
	private int quantity;
	
	public CardItem(String itemText, int quantity) {
		this.itemText = itemText;
		this.quantity = quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItemText() {
		return itemText;
	}
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}
}
