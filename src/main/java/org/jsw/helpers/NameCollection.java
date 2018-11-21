package org.jsw.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameCollection {
	private List<String> productTypes = new ArrayList<>(Arrays.asList("baguette", 
			"eclair", "muffin", "doughnut", "cheesecake", "croissant", "apple pie", 
			"swiss roll", "brownies", "strudel", "cup bake", "biscuit"));
	
	private List<String> bakeryName = new ArrayList<>(Arrays.asList("J-Co", 
			"BreadTalk", "Tous Le Jours", "Paris Baguette", "Chiz"));
	
	public List<String> getProductType() {
		return productTypes;
	}
	
	public List<String> getBakeryName() {
		return bakeryName;
	}
}
