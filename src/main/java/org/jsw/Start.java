package org.jsw;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.jsw.helpers.NameCollection;
import org.jsw.helpers.PriceList;

public class Start {
	public static void main(String[] args) {
    	NameCollection nameCollection = new NameCollection();
    	List<String> productType = nameCollection.getProductType();
    	List<String> bakeryName = nameCollection.getBakeryName();
    	
    	//Create File of Price List for Each Bakery
    	PriceList priceList = new PriceList();
    	String fileName;
    	for (String bakery : bakeryName) {
    		try {
    			fileName = "/home/widya/Gradle/ws18-project-jsw/src/main/resources/config/list/" + bakery + ".json";
				priceList.create(bakery, productType, fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	List<String> agents = new Vector<>();
    	agents.add("customer1:org.jsw.agents.CustomerAgent");
    	
    	for (String bakery : bakeryName) {
    		agents.add(bakery + ":org.jsw.agents.OrderProcessingAgent");	
    	}

    	List<String> cmd = new Vector<>();
    	cmd.add("-agents");
    	StringBuilder sb = new StringBuilder();
    	for (String a : agents) {
    		sb.append(a);
    		sb.append(";");
    	}
    	cmd.add(sb.toString());
        jade.Boot.main(cmd.toArray(new String[cmd.size()]));
    }
}
