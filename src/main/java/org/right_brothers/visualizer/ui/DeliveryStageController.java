package org.right_brothers.visualizer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.maas.utils.JsonConverter;
import org.right_brothers.data.messages.LoadingBayBox;
import org.right_brothers.data.messages.LoadingBayMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DeliveryStageController extends StageController implements Initializable {
	@FXML
	private VBox container;
	
	@FXML
	private Label cardCount;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateStage(String messageType, String message) {
		Matcher packagedOrderMatcher = Pattern.compile("^[\\w\\-]+\\-packaged-orders$")
				.matcher(messageType);
		if(packagedOrderMatcher.matches()) {
			LoadingBayMessage loadingBayMessage = JsonConverter.getInstance(message, new TypeReference<LoadingBayMessage>() {});
			addCard(loadingBayMessage);
		}
	}
	
	private void addCard(LoadingBayMessage message) {
		List<String> boxes = new ArrayList<String>();
		
		for(LoadingBayBox box: message.getBoxes()) {
			boxes.add(String.format("%s(%s)", box.getProductType(), box.getQuantity()));
		}
		
		Platform.runLater(
				  () -> {
					  try {
							FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/DeliveryCard.fxml"));
							Parent deliveryCard = fxmlLoader.load();
							container.getChildren().add(0, deliveryCard);
							
							DeliveryCardController controller =  fxmlLoader.getController();
							controller.setText(message.getOrderId(), String.join(" ", boxes));
							
							highlightCard(deliveryCard);
							
							cardCount.setText(Integer.toString(container.getChildren().size()));
						} catch(IOException e) {
							e.printStackTrace();
						}
				  }
				);
	}

	@Override
	public void setScenario(String scenarioDirectory) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clear() {
		Platform.runLater(
				  () -> {
					  container.getChildren().clear();
					  cardCount.setText("0");
				  }
				);
	}
}
