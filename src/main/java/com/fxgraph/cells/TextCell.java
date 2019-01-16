package com.fxgraph.cells;


import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextCell extends Cell {
    private String parentID;
    private String content;
    private Text v;
    
    public TextCell(String cellId, String parentID, String content) {
      super(cellId);
      this.parentID = parentID;
      this.content = content;
      this.v = new Text();
      v.setText(this.content);
      v.setAccessibleText(this.content);
      v.setFont(new Font(20));
      
      setView(v);
      
      cellType = CellType.TEXT;
    }
    public String getParentID() {
      return parentID;
    }
    public void setParentID(String parentID) {
      this.parentID = parentID;
    }
    
    public String getContent() {
    	return this.content;
    }
    public void setContent(String content) {
      this.content = content;
      
    ((Text)getView()).setText(this.content);
  	((Text)getView()).setAccessibleText(this.content);
      
      //v.setText(this.content);
//      setView(v);
    }
    
    
    
  }

