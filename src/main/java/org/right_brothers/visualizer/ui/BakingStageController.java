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
import org.right_brothers.data.messages.UnbakedProductMessage;
import org.right_brothers.visualizer.model.BakingStageCard;
import org.right_brothers.visualizer.model.CardItem;
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

public class BakingStageController extends StageController implements Initializable {
	@FXML
	private VBox container;
	
	@FXML
	private Label cardCount;
	
	private List<BakingStageCard> cardDataList;
	
	private BlockingQueue<StageOperation<BakingStageCard>> stageOperationsQueue = new LinkedBlockingQueue<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cardDataList = new ArrayList<>();
		
		Thread operatorThread = new Thread(new StageOperator());
		operatorThread.start();
	}

	@Override
	public void updateStage(String messageType, String message) {
		Matcher bakingRequestMatcher = Pattern.compile("^([\\w\\-]+)\\-baking-request$")
				.matcher(messageType);
		Matcher cooledProductConversationMatcher = Pattern.compile("^([\\w\\-]+)\\-cooled\\-product\\-(\\d+)$")
				.matcher(messageType);
		
		if(bakingRequestMatcher.matches()) {
			UnbakedProductMessage unbakedProductMessage = JsonConverter
					.getInstance(message, new TypeReference<UnbakedProductMessage>() {});
			String bakeryId = bakingRequestMatcher.group(1);
			
			addStageCard(bakeryId, unbakedProductMessage);
		} else if(cooledProductConversationMatcher.matches()) {		
			String bakeryId = cooledProductConversationMatcher.group(1);
			
			ProductMessage productMessage = JsonConverter.getInstance(message, new TypeReference<ProductMessage>() {});
			updateStageCards(bakeryId, productMessage);
		}
	}

	private void addStageCard(String bakeryId, UnbakedProductMessage message) {
		List<String> orders = new ArrayList<String>();
		List<CardItem> cardItems = new ArrayList<>();
		
		for(int i=0; i< message.getGuids().size(); i++) {
			orders.add(String.format("%s(%s)", 
					message.getGuids().get(i), message.getProductQuantities().get(i)));
			
			cardItems.add(new CardItem(
						message.getGuids().get(i), 
						message.getProductQuantities().get(i)
					));
		}
		
		BakingStageCard bakingStageCard = new BakingStageCard(
				bakeryId, message.getProductType(), cardItems
			);
		
		cardDataList.add(0, bakingStageCard);
		stageOperationsQueue.add(new StageOperation<BakingStageCard>(StageOperationType.ADD, 0, bakingStageCard));
	}
	
	private void updateStageCards(String bakeryId, ProductMessage productMessage) {
		for(int index=cardDataList.size()-1; index>=0; index--) {
			boolean altered = false;
			if(cardDataList.get(index).getBakeryId().equalsIgnoreCase(bakeryId)) {
				for(String bakedProductName: productMessage.getProducts().keySet()) {
					if(bakedProductName.equalsIgnoreCase(cardDataList.get(index).getProductId())) {
						int bakedProductQuantity = productMessage.getProducts().get(bakedProductName);
						
						for(CardItem item: cardDataList.get(index).getOrders()) {
							if(bakedProductQuantity > 0 && item.getQuantity() > 0) {
								if(bakedProductQuantity >= item.getQuantity()) {
									bakedProductQuantity = bakedProductQuantity - item.getQuantity();
									item.setQuantity(0);
								} else {
									item.setQuantity(item.getQuantity() - bakedProductQuantity);
									bakedProductQuantity = 0;
								}
								altered = true;
							}
						}
						
					}
				}
			}
			
			if(altered) {
				BakingStageCard bakingStageCard = cardDataList.get(index);
				stageOperationsQueue.add(new StageOperation<BakingStageCard>(StageOperationType.UPDATE, index, bakingStageCard));
			}
		}
		cleanUp();
	}

	protected void cleanUp() {
		List<BakingStageCard> cardsToRemove = new ArrayList<>();
		
		for(int index = cardDataList.size() -1; index >=0; index--) {
			if(cardDataList.get(index).isComplete()) {
				cardsToRemove.add(cardDataList.get(index));
			}
		}
		
		for(int index = cardsToRemove.size()-1; index>=0; index--) {
			BakingStageCard card = cardsToRemove.get(index);
			
			int indexOfCard = cardDataList.indexOf(card);
			cardDataList.remove(card);
			
			stageOperationsQueue.add(new StageOperation<BakingStageCard>(
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
		            StageOperation<BakingStageCard> operation = stageOperationsQueue.take();
		            
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

		private void updateCard(int index, BakingStageCard card) {
			try {
				if(index >=0 && index < container.getChildren().size()) {
					BakingCardController controller = (BakingCardController)container.getChildren().get(index).getUserData();
					
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

		private void addCard(BakingStageCard card) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/BakingCard.fxml"));
				Parent bakingCardNode = fxmlLoader.load();
				
				BakingCardController controller =  fxmlLoader.getController();
				bakingCardNode.setUserData(controller);
				
				final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
				
				Platform.runLater(
						  () -> {
								container.getChildren().add(0, bakingCardNode);
								controller.setText(card);
								highlightCard(bakingCardNode);
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
