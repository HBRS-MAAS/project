package org.right_brothers.visualizer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.maas.data.messages.ProductMessage;
import org.maas.utils.JsonConverter;
import org.right_brothers.data.messages.LoadingBayBox;
import org.right_brothers.data.messages.LoadingBayMessage;
import org.right_brothers.visualizer.model.CardItem;
import org.right_brothers.visualizer.model.PackagingStageCard;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PackagingStageController extends StageController implements Initializable {
	@FXML
	private VBox container;
	
	@FXML
	private Label cardCount;
	
	private List<PackagingStageCard> cardDataList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardDataList = new ArrayList<PackagingStageCard>();
	}

	@Override
	public void updateStage(String messageType, String message) {
		Matcher cooledProductConversationMatcher = Pattern.compile("^([\\w\\-]+)\\-cooled\\-product\\-(\\d+)$")
				.matcher(messageType);
		
		Matcher packagedOrderMatcher = Pattern.compile("^([\\w\\-]+)\\-packaged-orders$")
				.matcher(messageType);
		
		if(cooledProductConversationMatcher.matches()) {
			String bakeryId = cooledProductConversationMatcher.group(1);
			
			ProductMessage productMessage = JsonConverter.getInstance(message, new TypeReference<ProductMessage>() {});
			addCard(bakeryId, productMessage);
		} else if(packagedOrderMatcher.matches()) {
			String bakeryId = packagedOrderMatcher.group(1);
			
			LoadingBayMessage loadingBayMessage = JsonConverter.getInstance(message, new TypeReference<LoadingBayMessage>() {});
			
			Thread thread = new Thread(){
			    public void run(){
			    	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    	
			    	removeCards(bakeryId, loadingBayMessage);
			    }
			  };

			thread.start();
			
		}
	}

	private void addCard(String bakeryId, ProductMessage message) {
		List<CardItem> cardItems = new ArrayList<>();
		
		for(String key: message.getProducts().keySet()) {
			cardItems.add(new CardItem(key, message.getProducts().get(key)));
		}
		
		Platform.runLater(
				  () -> {
					  for(CardItem item: cardItems) {
						  try {
							  	
								FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/PackagingCard.fxml"));
								Parent packagingCardNode = fxmlLoader.load();
								
								PackagingCardController controller =  fxmlLoader.getController();
								
								packagingCardNode.setUserData(controller);
								container.getChildren().add(0, packagingCardNode);
								
								PackagingStageCard packagingStageCard = new PackagingStageCard(bakeryId, item);
								cardDataList.add(0, packagingStageCard);
								controller.setText(packagingStageCard);
								
								highlightCard(packagingCardNode);
								
								
								cardCount.setText(Integer.toString(container.getChildren().size()));
							} catch(IOException e) {
								e.printStackTrace();
						}
					  }
				  }
				);
	}
	
	private void removeCards(String bakeryId, LoadingBayMessage loadingBayMessage) {
		// Find the packaging cards that should be updated based on delivery message
		for(int index = cardDataList.size() -1; index >=0; index--) { 
			if(cardDataList.get(index).getBakeryId().equalsIgnoreCase(bakeryId)) {
				boolean altered = false;
				for(LoadingBayBox box:loadingBayMessage.getBoxes()) {
					int quantityInBox = box.getQuantity();
					
					CardItem item = cardDataList.get(index).getProduct();
					if(quantityInBox >0 && item.getQuantity() > 0  && 
							item.getItemText().equalsIgnoreCase(box.getProductType())) {
						if(quantityInBox >= item.getQuantity()) {
							quantityInBox = quantityInBox - item.getQuantity();
							item.setQuantity(0);
						} else {
							item.setQuantity(item.getQuantity() - quantityInBox);
							quantityInBox = 0;								
						}
						altered = true;
					}
				}
				// Re render the card if altered
				if(altered) {
					PackagingStageCard alteredCard = cardDataList.get(index);
					PackagingCardController controller = (PackagingCardController) container.getChildren().get(index).getUserData();
					
					Platform.runLater(
							  () -> {
								  controller.setText(alteredCard);
							  }
							);
				}
			}
		}
		cleanUp(cardDataList, container, cardCount);
	}

	@Override
	public void setScenario(String scenarioDirectory) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clear() {
		Platform.runLater(
				  () -> {
					  cardDataList.clear();
					  container.getChildren().clear();
					  cardCount.setText("0");
				  }
				);
	}
}
