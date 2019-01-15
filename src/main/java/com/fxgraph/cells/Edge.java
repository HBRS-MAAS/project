package com.fxgraph.cells;

import javafx.scene.Group;
import javafx.scene.shape.Line;

public class Edge extends Group {

    protected Cell source;
    protected Cell target;

    

    public Edge(Cell source, Cell target) {

        this.source = source;
        this.target = target;

        source.addCellChild(target);
        target.addCellParent(source);

        Line line = new Line();
        
        double sourceOffset = (source.getCellType() == CellType.BALL) ? 15.0 : 0.0;
        double targetOffset = (target.getCellType() == CellType.BALL) ? 15.0 : 0.0;

        line.startXProperty().bind( source.layoutXProperty().add(sourceOffset/2.0 + (source.getBoundsInParent().getWidth() / 2.0)));
        line.startYProperty().bind( source.layoutYProperty().add(sourceOffset/2.0 + (source.getBoundsInParent().getHeight() / 2.0)));

        line.endXProperty().bind( target.layoutXProperty().add( targetOffset/2.0 + (target.getBoundsInParent().getWidth() / 2.0)));
        line.endYProperty().bind( target.layoutYProperty().add( targetOffset/2.0 + (target.getBoundsInParent().getHeight() / 2.0)));

        getChildren().add( line);

    }

    public Cell getSource() {
        return source;
    }

    public Cell getTarget() {
        return target;
    }

}
