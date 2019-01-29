package org.right_brothers.visualizer.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.maas.utils.Time;
import org.right_brothers.visualizer.model.TimelineItem;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LayoutController implements Initializable, ScenarioAware {
	private Stage container;
	private Time currentTime;
	
	private boolean isReplaying = false;
	
	private List<TimelineItem> timelineItems;
	
	@FXML
	private AnchorPane backingStageContainer;
	
	@FXML
	private AnchorPane packagingStageContainer;
	
	@FXML
	private AnchorPane deliveryStageContainer;
	
	@FXML
	private Label timeDisplay;
	
	private List<StageController> controllers = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		timelineItems = new ArrayList<>();
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/BakingStage.fxml"));
			Parent bakingStage = fxmlLoader.load();
			backingStageContainer.getChildren().add(bakingStage);
			controllers.add(fxmlLoader.getController());
			
			fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/PackagingStage.fxml"));
			Parent packaging = fxmlLoader.load();
			packagingStageContainer.getChildren().add(packaging);
			controllers.add(fxmlLoader.getController());
			
			fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/DeliveryStage.fxml"));
			Parent delivery = fxmlLoader.load();
			deliveryStageContainer.getChildren().add(delivery);
			controllers.add(fxmlLoader.getController());
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void updateBoard(String messageType, String message) {
		timelineItems.add(new TimelineItem(currentTime, messageType, message));
		
		if(!isReplaying) {
			for(StageController controller: controllers) {
				controller.updateStage(messageType, message);
			}
		}
	}

	@Override
	public void setScenario(String scenarioDirectory) {
		for(StageController controller: controllers) {
			controller.setScenario(scenarioDirectory);
		}
	}

	public void setTime(Time currentTime) {
		this.currentTime = currentTime;
		
		if(!isReplaying) {
			Platform.runLater(
					  () -> {
						  timeDisplay.setText(currentTime.toString());
					  }
					);
		}
	}
	
	@FXML
    public void handleExitAction(ActionEvent event) {
        container.close();
    }
	
	@FXML
    public void handleReplayAction(ActionEvent event) throws InterruptedException {
		if(!isReplaying) {
	        isReplaying = true;
	    	
	    	Thread thread = new Thread(){
	    	    public void run(){
	    	    	for(StageController controller: controllers) {
	    				controller.clear();
	    			}
	    	    	
	    	        if(timelineItems.size() > 0) {
	    	    		SimulationTask task = new SimulationTask(0, 1);
	    	    		Thread taskThread = new Thread(task);
	    	    		taskThread.start();
	    	        }
	    	    }
	    	  };

	    	thread.start();
		}
    }

	public void setStage(Stage primaryStage) {
		container = primaryStage;
	}
	
	private class SimulationTask extends Task<Void> {
	    private int timelineItemIndex;
	    private final int intervalInSeconds;

	    public SimulationTask(int timelineItemIndex, int intervalInSeconds) {
	        this.timelineItemIndex = timelineItemIndex;
	        this.intervalInSeconds = intervalInSeconds;
	    }

	    @Override
	    protected Void call() throws Exception {
	    	try {
  		      Thread.sleep(intervalInSeconds * 1000L);
  		    } catch (Exception e) {
  		    }
  		    
          	Platform.runLater(
  					  () -> {
  						  	if(timelineItemIndex < timelineItems.size()) {
  						  		timeDisplay.setText(timelineItems.get(timelineItemIndex).getTime().toString());
  						  	
  						  		int index = timelineItemIndex;
  						  		while(index < timelineItems.size() && 
  						  				timelineItems.get(index).getTime().equals(timelineItems.get(timelineItemIndex).getTime())) {
					  				TimelineItem item = timelineItems.get(index);
						  		
		  		                	for(StageController controller: controllers) {
		  		        				controller.updateStage(item.getMessageType(), item.getMessage());
		  		        			}
		  		                	index++;
					  			}
	  		                	
	  		                	if(index >= timelineItems.size()) {
	  		                		isReplaying = false;
	  		                	} else {
	  		                		SimulationTask task = new SimulationTask(index, intervalInSeconds);
	  			    	    		Thread taskThread = new Thread(task);
	  			    	    		taskThread.start();
	  		                	}
  						  	}
  					  }
  					);

	        return null;
	    }
	}
}
