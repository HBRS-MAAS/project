package org.right_brothers.visualizer.ui;

import java.net.URL;
import java.util.ResourceBundle;
import org.right_brothers.visualizer.model.PackagingStageCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class PackagingCardController implements Initializable {
	@FXML
	private Label title;
	
	@FXML
	private Label bakery;
	
	@FXML
	private Label description;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setText(PackagingStageCard card) {
		if (card != null && card.getProduct() != null) {
			String title = card.getProduct().getItemText();
			String bakeryId = card.getBakeryId();
			String description = Integer.toString(card.getProduct().getQuantity());
			
			this.title.setText(title);
			this.bakery.setText(bakeryId);
			this.description.setText(description);
		}
	}
}
