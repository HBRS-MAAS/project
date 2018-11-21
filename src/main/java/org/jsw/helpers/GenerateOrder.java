package org.jsw.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateOrder{
	private List<JSONObject> orders;
	
	/*
	Orders are randomly generated. They are saved in JSON format and includes ID and ordered
	product list. 
	16 Nov 2018: only one order in orders. Customer ID is randomly generated to make sure 
	there are no duplication. Number of product ordered is also randomly generated. 
	*/ 
	public List<JSONObject> getOrder(List<String> product_types) {
		//Object[] args = getArguments(); //For now no arguments taken
		orders = new ArrayList<>();
		JSONObject order = new JSONObject();
				
		String customer_id = generateRandomID(10);
		int min = 0;
		int max = 50;
		int total_order = 0;
		
		try {
			order.put("id", customer_id);
			//JSONArray products = new JSONArray();
			JSONObject product = new JSONObject();
			
			for(String product_type : product_types ) {
				total_order = ThreadLocalRandom.current().nextInt(min, max + 1);
				product.put(product_type, total_order);
				//products.add(product);
			}
			
			order.put("Product List", product);
			orders.add(order);	
		} catch (JSONException e) {
	    	  e.printStackTrace();
	    }
		
		return orders;
	}
	
	private String generateRandomID(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		             + "abcdefghijklmnopqrstuvwxyz"
		             + "0123456789";
		String str = new Random().ints(length, 0, chars.length())
		                         .mapToObj(i -> "" + chars.charAt(i))
		                         .collect(Collectors.joining());
		return str;
	}
}