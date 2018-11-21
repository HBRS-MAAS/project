package org.jsw.helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONException;
import org.json.JSONObject;

public class PriceList {
	public void create(String name, List<String> productType, String fileName) throws IOException, JSONException {
		JSONObject order = new JSONObject();	
		int price;
		int min = 5;
		int max = 10;
		
		try {
			order.put("Bakery Name", name);
			
			JSONObject product = new JSONObject();
			
			for(String type : productType ) {
				price = ThreadLocalRandom.current().nextInt(min, max + 1);
				product.put(type, price);
				//products.add(product);
			}
			
			order.put("Product Price", product);
		} catch (JSONException e) {
	    	  e.printStackTrace();
	    }
		
		
		FileWriter file = new FileWriter(fileName);
		
		try {
			file.write(order.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + order);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.flush();
			file.close();
		}
	}
}
