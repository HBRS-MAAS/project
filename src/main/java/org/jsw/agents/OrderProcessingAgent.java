package org.jsw.agents;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsw.helpers.GenerateOrder;
import org.jsw.helpers.ManageMessage;

@SuppressWarnings("serial")
public class OrderProcessingAgent extends Agent {
	private List<String> productTypes;	
	private String bakeryName;
	
    protected void setup() {
    	bakeryName = getAID().getLocalName();
    	
        System.out.println("\tOrder-processing-agent " + bakeryName +" is born.");

        registerSeller();
        getProductTypes();
        
        addBehaviour(new OfferRequestsServer());
    }
    
	protected void getProductTypes() {
    	String filepath = "/home/widya/Gradle/ws18-project-jsw/src/main/resources/config/list/" + bakeryName + ".json";
    	String fileString = "";
    	JSONObject bakeryProduct = new JSONObject();
    	JSONObject product = new JSONObject();
    	productTypes = new ArrayList();
    	
		try {
			fileString = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
			bakeryProduct = new JSONObject(fileString);
			
			if (bakeryProduct.has("Product Price")) {
				product = bakeryProduct.getJSONObject("Product Price");
	        }
			
			Iterator iter = product.keys();
			while(iter.hasNext()){
				String key = (String)iter.next();
				productTypes.add(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("\t"+getAID().getLocalName()+" terminating.");
    }
    
    protected void registerSeller(){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Bakery-Seller");
        sd.setName("Bakery");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
        	JSONObject incomingRequest = new JSONObject();
        	JSONObject proposal = new JSONObject();
            
        	//Receive order request from a customer
        	MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage request = myAgent.receive(mt);
            
            //Process the request if it is not empty and it is in JSON language
            if (request != null) {
            	if (request.getLanguage().equals("JSON")) {
					try {
						//System.out.println("Received Request: " + request.getContent());
						incomingRequest = new JSONObject(request.getContent());
						proposal = ManageMessage.calculatePrice(incomingRequest, bakeryName, productTypes);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            	
            	//Reply the request with a proposal
                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                //reply.setContent("Got your order.");
                //myAgent.send(reply);
                
                //Send the proposal
				reply.setLanguage("JSON");
				reply.setContent(proposal.toString());
				myAgent.send(reply);
            } else {
                //System.out.println("Could not read order");
                block();
            }
        }
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
