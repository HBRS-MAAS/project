package org.right_brothers.data.messages;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class LoadingBayMessage {
	private String orderId;
	private List<LoadingBayBox> boxes;
	
	public LoadingBayMessage() {
		boxes = new ArrayList<LoadingBayBox>();
	}
	
	public LoadingBayMessage(String orderId, List<LoadingBayBox> boxes) {
		this();
		
		this.orderId = orderId;
		this.boxes = boxes;
	}
	@JsonProperty("OrderID")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	@JsonProperty("Boxes")
	public List<LoadingBayBox> getBoxes() {
		return boxes;
	}
	public void setBoxes(List<LoadingBayBox> boxes) {
		this.boxes = boxes;
	}
}
