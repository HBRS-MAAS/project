package org.right_brothers.visualizer.ui;


import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public abstract class StageController implements ScenarioAware{
	public abstract void updateStage(String messageType, String message);
	
	protected void highlightCard(Node node) {
		FadeTransition ft = new FadeTransition(Duration.millis(200), node);
		ft.setFromValue(0.5);
		ft.setToValue(1.0);
		ft.setCycleCount(1);
		ft.setAutoReverse(true);
 
		ft.play();
	}
	
	public abstract void clear();
}
