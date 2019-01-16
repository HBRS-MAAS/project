package com.fxgraph.cells;

import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class Graph {

    private Model model;

    

    private Pane scrollPane;

    /**
     * the pane wrapper is necessary or else the scrollpane would always align
     * the top-most and left-most child to the top and left eg when you drag the
     * top child down, the entire scrollpane would move down
     */
    private Pane cellLayer;

    public Graph() {

        this.model = new Model();

        Group canvas = new Group();
        cellLayer = new Pane();

        canvas.getChildren().add(cellLayer);

        scrollPane = new Pane(canvas);

        //scrollPane.setFitToWidth(true);
        //scrollPane.setFitToHeight(true);
    }

    public Pane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getCellLayer() {
        return this.cellLayer;
    }

    public Model getModel() {
        return model;
    }

    public void beginUpdate() {
    // to start over the graph update
    }

    public void endUpdate() {

        // add components to graph pane
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        getCellLayer().getChildren().addAll(model.getAddedCells());

        // remove components from graph pane
        getCellLayer().getChildren().removeAll(model.getRemovedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

       

        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
        getModel().attachOrphansToGraphParent(model.getAddedCells());

        // remove reference to graphParent
        getModel().disconnectFromGraphParent(model.getRemovedCells());

        // merge added & removed cells with all cells
        getModel().merge();

    }

    public double getScale() {
        return this.scrollPane.getScaleX();
    }
}