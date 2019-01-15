package com.fxgraph.cells;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Cell{
  public Ball( String id) {
    super( id);

    Circle view = new Circle(25, 25, 10);

    view.setStroke(Color.DODGERBLUE);
    view.setFill(Color.YELLOW);

    setView( view);

    cellType = CellType.BALL;
}
  
  public void setColor(Color color) {
  	((Circle)getView()).setStroke(color);
  	((Circle)getView()).setFill(color);
  }
}
