package org.maas.agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class CustomerAgent extends BaseAgent {	
	private JSONObject incomingProposal = new JSONObject();
	private JSONObject confirmation = new JSONObject();
	private JSONObject combined = new JSONObject();
	
	private JSONArray dataArray = new JSONArray();
	private JSONArray orders = new JSONArray();
	//private JSONObject location = new JSONObject();
	private Object location = null;
	
	private String agentName = "";
	
	private int n = 0;
	private int total = 0;
	
	private AID [] sellerAgents;
	
	protected void setup() {		
		super.setup();
		agentName = getAID().getLocalName();
		System.out.println(agentName + " is ready.");
		
		register("Bakery-Customer", "Bakery");
		getSellers();
		
		retrieve("src/main/resources/config/small/clients.json");
		total = getOrder(agentName);
		
		addBehaviour(new RequestPerformer());
	    try {
	    	Thread.sleep(3000);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }			
	}
	
	protected void takeDown() {
		deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}
	
	protected void getSellers() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Bakery-Seller");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(CustomerAgent.this, template);
            sellerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                sellerAgents[i] = result[i].getName();
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
		
	private class RequestPerformer extends Behaviour {
		private MessageTemplate mt;
		private int step = 0;	
				
		public void action() {
			//if (!baseAgent.getAllowAction()) {
			//    return;
			//}
			
			//System.out.println("Action Start");
			switch (step) {
			case 0:				
				// Send the order (message) to all sellers
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
								
				for (int i = 0; i < sellerAgents.length; ++i) {
					msg.addReceiver(sellerAgents[i]);
				}
								
				//Check Time -> Later use BaseAgent.java after ClockAgent has been built
				LocalDate localDate = LocalDate.now();
				
				//Get Order at Specified Time
		    	JSONObject order = getCurrentOrder(localDate);
		    	order = includeLocation(order);
		    			    	
		    	//System.out.println("Send order: " + order);
		    	
				msg.setConversationId("customer-order");
				msg.setLanguage("JSON");
				msg.setContent(order.toString());
				msg.addReplyTo(getAID());
				msg.setReplyWith("order-"+System.currentTimeMillis()); // Unique value
				sendMessage(msg);
				
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("customer-order"),
						MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
				
				step = 1;
				n++;
				break;
				
			case 1:
				//System.out.println("Get Proposal");
				
				// Receive the purchase order reply: Bakery name that sells the order and the price
				ACLMessage proposal = myAgent.receive(mt);				
				
				if (proposal != null) {
					// Purchase order reply received
					if (proposal.getPerformative() == ACLMessage.PROPOSE) {
						if (proposal.getLanguage().equals("JSON")) {
							//System.out.println("Received Proposal: " + proposal.getContent());
							try {
								
								JSONObject Obj1 = new JSONObject(proposal.getContent());
								JSONObject Obj2 = new JSONObject();
								
								String name = proposal.getSender().getLocalName();
								
								Obj2 = Obj1.getJSONObject(name);
								
								CustomerAgent.this.incomingProposal.put(name, Obj2);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} else {
						//System.out.println("No reply received");
						block(); //Wait until receive any reply
					}
					
					if (incomingProposal.length() == sellerAgents.length) {
						//System.out.println("incomingProposal " + incomingProposal);
						step = 2;
					}  
					
				}
				break;
			case 2:
				try {
					confirmation = findTheCheapest(incomingProposal);
					
					//System.out.println("Send Confirmation: " + confirmation);
					
					//Send the confirmation
					for (int i = 0; i < sellerAgents.length; ++i) {
						String name = sellerAgents[i].getLocalName();
						if (CustomerAgent.this.confirmation.has(name)) {
							ACLMessage confirm = new ACLMessage(ACLMessage.CONFIRM);
							confirm.setConversationId("customer-order");
							confirm.addReceiver(sellerAgents[i]);
							confirm.setLanguage("JSON");
							confirm.setContent(CustomerAgent.this.confirmation.getString(name));
							send(confirm);
							
							//System.out.println("confirm " + name + ": " + confirm.getContent());
						}
		            }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				step = 0;
				break;
			default:
				break;
			}
		}
		
		//Stop the action loop
		public boolean done() {
			if (n >= total) {
				addBehaviour(new shutdown());
				//baseAgent.finished(); // calling finished method
                //myAgent.doDelete();
                return true;
			}
			return false;
		}
		
		// Taken from http://www.rickyvanrijn.nl/2017/08/29/how-to-shutdown-jade-agent-platform-programmatically/
		private class shutdown extends OneShotBehaviour{
			public void action() {
				ACLMessage shutdownMessage = new ACLMessage(ACLMessage.REQUEST);
				Codec codec = new SLCodec();
				myAgent.getContentManager().registerLanguage(codec);
				myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());
						shutdownMessage.addReceiver(myAgent.getAMS());
						shutdownMessage.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
						shutdownMessage.setOntology(JADEManagementOntology.getInstance().getName());
				try {
					myAgent.getContentManager().fillContent(shutdownMessage,new Action(myAgent.getAID(), new ShutdownPlatform()));
					myAgent.send(shutdownMessage);
				} catch (Exception e) {
					//LOGGER.error(e);
				}
			}
		}
	}
	
	//FUNCTIONS TO MANAGE JSON OBJECTS
	//Retrieve client data from config file
	private void retrieve(String fileName) {
		File file = new File(fileName);
		String filePath = file.getAbsolutePath();
		String fileContent = "";	
		
		try {
			fileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
			dataArray = new JSONArray(fileContent);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Get list of order 
	private int getOrder(String name) {
		String customerName = "";
				
		//Take Orders from Customer (based on the name)
		try {
			for (int i = 0; i < dataArray.length(); i++) {
				customerName = dataArray.getJSONObject(i).getString("name");
				
				if (customerName.equals(name)) {
					orders = dataArray.getJSONObject(i).getJSONArray("orders");
					//location = dataArray.getJSONObject(i).getJSONObject("location");
					location = dataArray.getJSONObject(i).get("location");
					
					return orders.length();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private JSONObject findTheCheapest(JSONObject proposal) {
		JSONObject confirmation = new JSONObject();
		JSONObject product = new JSONObject();
		
		List<String> bakeryName = new ArrayList();
		List<String> productTypes = new ArrayList();
		
		String chosenBakery = "";
		
		//Get All Bakery Name
		try {
			Iterator iter = proposal.keys();
			while(iter.hasNext()) {
				String key = (String)iter.next();
				bakeryName.add(key);
				
				product = proposal.getJSONObject(key);
				Iterator iter2 = product.keys();
				while(iter2.hasNext()) {
					String key2 = (String)iter2.next();
					if (!productTypes.contains(key2)) {
						productTypes.add(key2);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//Get The Cheapest Price
		try {
			for (String type : productTypes) {
				Double min_price = Double.MAX_VALUE;
				for (String name : bakeryName) {
					product = proposal.getJSONObject(name);	
					
					if (min_price > product.getDouble(type) && product.getDouble(type) != 0) {
						chosenBakery = name;
						min_price = product.getDouble(type);
					}
				}
				
				if (confirmation.has(chosenBakery)) {
					type = type + ", " + confirmation.getString(chosenBakery);
				}
				
				confirmation.put(chosenBakery, type);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return confirmation;
	}
	
	//Function use LocalDate to check the current world date.
	//It is currently commented for simulation.
	//Later the date used will be provided by BaseAgent
	private JSONObject getCurrentOrder(LocalDate date) {
		JSONObject order_date = new JSONObject();
		
		//Check Date
		try {
			for (int i = 0; i < orders.length(); i++) {
				order_date = orders.getJSONObject(i).getJSONObject("order_date");
				
				int day = order_date.getInt("hour");
				int month = order_date.getInt("day");
				
				/*if ((day == date.getDayOfMonth()) && (month == date.getMonthValue()) ) {
					return orders.getJSONObject(i);
				}*/
				
				return orders.getJSONObject(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private JSONObject includeLocation(JSONObject order) {
		try {
			order.put("location", location);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return order;
	}
}