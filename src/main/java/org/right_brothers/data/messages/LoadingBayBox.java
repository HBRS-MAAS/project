package org.right_brothers.data.messages;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadingBayBox {
	private String boxId;
	private String productType;
	private int quantity;
	
	public LoadingBayBox() {}
	
	public LoadingBayBox(String boxId, String productType, int quantity) {
		this.boxId = boxId;
		this.productType = productType;
		this.quantity = quantity;
	}
	@JsonProperty("BoxID")
	public String getBoxId() {
		return boxId;
	}
	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}
	@JsonProperty("ProductType")
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	@JsonProperty("Quantity")
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}