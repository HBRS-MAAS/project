package com.fxgraph.cells;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;


public class TriangleCell extends Cell {

    public TriangleCell( String id) {
        super( id);

        double width = 50;
        double height = 50;

        Polygon view = new Polygon( width / 2, 0, width, height, 0, height);

        view.setStroke(Color.RED);
        view.setFill(Color.RED);

        setView(view);
        
        cellType = CellType.TRIANGLE;
    }
    
    public void setColor(Color color) {
    	((Polygon)getView()).setStroke(color);
    	((Polygon)getView()).setFill(color);
    }

}