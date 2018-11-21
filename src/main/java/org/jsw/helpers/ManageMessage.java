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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ManageMessage {
	
	/*
	16 Nov 2018: 
	Expected Message: 
	{	"bakery_1": {"baguette": 500, "apple pie": 100, ...},
		"bakery_2": {"baguette": 10, ...},
		...
	} 
	Process: pick the lowest offered price from all bakeries for each type of product
	Expected Confirmation: 
	{
		"baguette": "bakery_1", "apple pie": "bakery_3", ...
	}
	*/
	public static JSONObject findTheCheapest(JSONObject message, List<String> bakeryName, List<String> productTypes) throws JSONException {
		JSONObject confirmation = new JSONObject();
		JSONObject bakery = new JSONObject();
		
		String chosenBakery = "";
		
		for (String type : productTypes) {
			int min_price = Integer.MAX_VALUE;
			for (String name : bakeryName) {
				if (message.has(name)) {
					bakery = message.getJSONObject(name);	
				}
				if (min_price > bakery.getInt(type) && bakery.getInt(type) != 0) {
					chosenBakery = name;
					min_price = bakery.getInt(type);
				}
			}
			
			if (confirmation.has(chosenBakery)) {
				type = type + ", " + confirmation.getString(chosenBakery);
			}
			
			confirmation.put(chosenBakery, type);
		}
		
		return confirmation;
	}
	
	public static JSONObject calculatePrice(JSONObject message, String bakeryName, List<String> productType) {
		JSONObject priceList = new JSONObject();
		JSONObject amount = new JSONObject();
		String filepath = "/home/widya/Gradle/ws18-project-jsw/src/main/resources/config/list/" + bakeryName + ".json";
		JSONObject bakeryProduct = new JSONObject();
    	
		//Read Price List and Message
		String fileString = "";
		try {
			fileString = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
			bakeryProduct = new JSONObject(fileString);
			if (bakeryProduct.has("Product Price")) {
				priceList = bakeryProduct.getJSONObject("Product Price");
	        }
			
			if (message.has("Product List")) {
				amount = message.getJSONObject("Product List");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//System.out.println("price list: " + priceList);
		//System.out.println("amount: " + amount);
		
		//Calculate Total Price per Product Type
		JSONObject priceTotal = new JSONObject();
		for (String type : productType) {
			try {
				int n = amount.getInt(type);
				int price = priceList.getInt(type);
				
				int total = n*price;
				priceTotal.put(type, total);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		//Write JSONObject Product Type: Price
		JSONObject bakeryPrice = new JSONObject();
		try {
			bakeryPrice.put(bakeryName, priceTotal);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//System.out.println("Bakery Price: " + bakeryPrice);
		
		return bakeryPrice;		
	}
}
