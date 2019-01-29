package org.right_brothers.visualizer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.maas.data.messages.ProductMessage;
import org.maas.utils.JsonConverter;
import org.right_brothers.data.messages.LoadingBayBox;
import org.right_brothers.data.messages.LoadingBayMessage;
import org.right_brothers.visualizer.model.CardItem;
import org.right_brothers.visualizer.model.PackagingStageCard;
import org.right_brothers.visualizer.model.StageOperation;
import org.right_brothers.visualizer.model.StageOperationType;

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
	
	private BlockingQueue<StageOperation<PackagingStageCard>> stageOperationsQueue = new LinkedBlockingQueue<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardDataList = new ArrayList<PackagingStageCard>();
		
		Thread operatorThread = new Thread(new StageOperator());
		operatorThread.start();
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
			addStageCard(bakeryId, productMessage);
		} else if(packagedOrderMatcher.matches()) {
			String bakeryId = packagedOrderMatcher.group(1);
			
			LoadingBayMessage loadingBayMessage = JsonConverter.getInstance(message, new TypeReference<LoadingBayMessage>() {});
			updateStageCards(bakeryId, loadingBayMessage);
			
		}
	}

	private void addStageCard(String bakeryId, ProductMessage message) {
		List<CardItem> cardItems = new ArrayList<>();
		
		for(String key: message.getProducts().keySet()) {
			cardItems.add(new CardItem(key, message.getProducts().get(key)));
		}
		
		for(CardItem item: cardItems) {			
			PackagingStageCard packagingStageCard = new PackagingStageCard(bakeryId, item);
			
			cardDataList.add(0, packagingStageCard);
			stageOperationsQueue.add(new StageOperation<PackagingStageCard>(StageOperationType.ADD, 0, packagingStageCard));
		}
	}
	
	private void updateStageCards(String bakeryId, LoadingBayMessage loadingBayMessage) {
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
					stageOperationsQueue.add(new StageOperation<PackagingStageCard>(StageOperationType.UPDATE, index, alteredCard));
				}
			}
		}
		cleanUp();
	}
	
	protected void cleanUp() {
		List<PackagingStageCard> cardsToRemove = new ArrayList<>();
		
		for(int index = cardDataList.size() -1; index >=0; index--) {
			if(cardDataList.get(index).isComplete()) {
				cardsToRemove.add(cardDataList.get(index));
			}
		}
		
		for(int index = cardsToRemove.size()-1; index>=0; index--) {
			PackagingStageCard card = cardsToRemove.get(index);
			
			int indexOfCard = cardDataList.indexOf(card);
			cardDataList.remove(card);
			
			stageOperationsQueue.add(new StageOperation<PackagingStageCard>(
						StageOperationType.REMOVE, indexOfCard , card
					));
		}
	}
	

	@Override
	public void setScenario(String scenarioDirectory) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clear() {
		try {
			final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
			Platform.runLater(
					  () -> {
						  cardDataList.clear();
						  stageOperationsQueue.clear();
						  container.getChildren().clear();
						  cardCount.setText("0");
						  
						  latchToWaitForJavaFx.countDown();
					  }
					);
			latchToWaitForJavaFx.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private class StageOperator implements Runnable{
	    public void run() {
	        try {
	        	while(true) {
		            StageOperation<PackagingStageCard> operation = stageOperationsQueue.take();
		            
		            if(operation.getOperationType() == StageOperationType.ADD) {
		            	addCard(operation.getCard());
		            } else if(operation.getOperationType() == StageOperationType.UPDATE) {
	            		updateCard(operation.getIndex(), operation.getCard());
		            } else if(operation.getOperationType() == StageOperationType.REMOVE) {
		            	removeCard(operation.getIndex());
		            }
	        	}
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }

		private void updateCard(int index, PackagingStageCard card) {
			try {
				if(index >=0 && index < container.getChildren().size()) {
					PackagingCardController controller = (PackagingCardController)container.getChildren().get(index).getUserData();
					
					final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
					Platform.runLater(
							  () -> {
								  controller.setText(card);
								  latchToWaitForJavaFx.countDown();
							  }
							);
					
					latchToWaitForJavaFx.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void removeCard(int index) {
			try {
				if(index >=0 && index < container.getChildren().size()) {
					final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
					Platform.runLater(
							  () -> {
								  container.getChildren().remove(index);
								  cardCount.setText(Integer.toString(container.getChildren().size()));
								  
								  latchToWaitForJavaFx.countDown();
							  }
							);
					
					latchToWaitForJavaFx.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void addCard(PackagingStageCard card) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/PackagingCard.fxml"));
				Parent packagingCardNode = fxmlLoader.load();
				
				PackagingCardController controller =  fxmlLoader.getController();
				packagingCardNode.setUserData(controller);
				
				final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
				
				Platform.runLater(
						  () -> {
								container.getChildren().add(0, packagingCardNode);
								controller.setText(card);
								highlightCard(packagingCardNode);
								cardCount.setText(Integer.toString(container.getChildren().size()));
								
								latchToWaitForJavaFx.countDown();
						  }
					  );
				latchToWaitForJavaFx.await();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
