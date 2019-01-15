package com.fxgraph.cells;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Cell extends Pane {

    private String cellId;
    protected CellType cellType;

    private List<Cell> children = new ArrayList<>();
    private List<Cell> parents = new ArrayList<>();

    private Node view;
    private Button b;
    public Cell(String cellId) {
        this.cellId = cellId;
    }

    public void addCellChild(Cell cell) {
        children.add(cell);
    }

    public List<Cell> getCellChildren() {
        return children;
    }

    public void addCellParent(Cell cell) {
        parents.add(cell);
    }

    public List<Cell> getCellParents() {
        return parents;
    }

    public void removeCellChild(Cell cell) {
        children.remove(cell);
    }

    public void setView(Node view) {

        this.view = view;
        getChildren().add(view);

    }

    public Node getView() {
        return this.view;
    }

    public String getCellId() {
        return cellId;
    }
    
    public Button getB() {
      return b;
    }

    public void setB(Button b) {
      this.b = b;
    }
    
    public CellType getCellType() {
      return cellType;
    }
}