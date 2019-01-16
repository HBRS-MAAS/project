package org.right_brothers.visualizer.ui;
	
import java.util.concurrent.CountDownLatch;

import org.maas.agents.BaseAgent;
import org.maas.utils.Time;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Visualizer extends Application {
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	public static Visualizer currentInstance = null;
	
	private static BaseAgent agent;
	private static String scenarioDirectory;
	
	private LayoutController layoutController;
	
	public static void setInstance(Visualizer visulizer) {
        currentInstance = visulizer;
        countDownLatch.countDown();
    }
	
	// https://stackoverflow.com/questions/25873769/launch-javafx-application-from-another-class
    public static Visualizer waitForInstance() {
        try {
        	countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentInstance;
    }
	
	public static void run(BaseAgent agent, String scenarioDirectory) {
		Visualizer.agent = agent;
		Visualizer.scenarioDirectory = scenarioDirectory;
		
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/right_brothers/Layout.fxml"));
			Parent root = fxmlLoader.load();
			layoutController = fxmlLoader.getController();
			layoutController.setScenario(scenarioDirectory);
			layoutController.setStage(primaryStage);
			
			
			Scene scene = new Scene(root);
			scene.getStylesheets()
				.add(getClass().getResource("/fxml/right_brothers/application.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		setInstance(this);
	}
	
	@Override
	public void stop() {
		countDownLatch = new CountDownLatch(1);
		currentInstance = null;
		
		agent.finished();
	}
	
	public void updateBoard(String messageType, String message) {
		layoutController.updateBoard(messageType, message);
	}

	public void setTime(Time currentTime) {
		layoutController.setTime(currentTime);
	}
}
