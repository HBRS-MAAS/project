package org.right_brothers.visualizer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import org.right_brothers.visualizer.model.BakingStageCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class BakingCardController implements Initializable {
	@FXML
	private Label title;
	
	@FXML
	private Label description;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setText(BakingStageCard card) {
		String title = card.getProductId();
		String description = Integer.toString(
					card.getOrders()
					.stream()
					.mapToInt(item -> item.getQuantity())
					.sum()
				);
				
		
		this.title.setText(title);
		this.description.setText(description);
		
		Tooltip toolTip = new Tooltip(description);
		this.description.setTooltip(toolTip);
	}
}
