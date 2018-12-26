package org.maas.agents;

import java.util.HashMap;

import org.json.*;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class LoadingBayAgent extends BaseAgent {
	private JSONArray orderDetailsArray = new JSONArray();
	private String readyOrderID = null;

	private HashMap<String, HashMap<String, Integer>> productDatabase = new HashMap<>();
	private HashMap<String, JSONArray> boxDatabase = new HashMap<>();

	protected void setup() {
		super.setup();
		System.out.println("Hello! LoadingBay-agent " + getAID().getName() + " is ready.");

		register("loading-bay", "loading-bay");

		addBehaviour(new OrderDetailsReceiver());
		addBehaviour(new ProductDetailsReceiver());
		addBehaviour(new TimeUpdater());
	}

	protected void takeDown() {
		deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	protected void addCustomerOrder(String orderID, String product, int quantity) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(product, quantity);
		this.productDatabase.put(orderID, temp);
	}

	protected void addCustomerProduct(String orderID, String product, int quantity) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		temp.put(product, quantity);
		this.productDatabase.get(orderID).put(product, quantity);
	}

	protected void UpdateCustomerProductQuantity(String orderID, String product, int addedQuantity) {
		int oldQuantity = this.productDatabase.get(orderID).get(product);
		int newQuantity = oldQuantity + addedQuantity;
		this.productDatabase.get(orderID).replace(product, newQuantity);
	}

	protected String createOrderBoxesJSONMessage(String orderID) {
		JSONObject message = new JSONObject();
		message.put("OrderID", orderID);
		message.put("Boxes", this.boxDatabase.get(orderID));

		return message.toString();
	}

	protected void updateBoxDatabase(String orderBoxesDetails) {
		JSONObject JSONData = new JSONObject(orderBoxesDetails);

		String orderID = JSONData.getString("OrderID");
		JSONArray boxes = JSONData.getJSONArray("Boxes");

		if (boxDatabase.get(orderID) != null) {
			for (int i = 0; i < boxDatabase.get(orderID).length(); i++) {
				boxes.put(boxDatabase.get(orderID).getJSONObject(i));
			}
		}

		this.boxDatabase.put(orderID, boxes);
	}

	protected void updateProductDatabase(String orderBoxesDetails) {
		JSONObject JSONData = new JSONObject(orderBoxesDetails);

		String orderID = JSONData.getString("OrderID");
		JSONArray boxes = JSONData.getJSONArray("Boxes");

		// Check if the database does not contain this order's details
		if (!this.productDatabase.containsKey(orderID)) {
			for (int i = 0; i < boxes.length(); i++) {
				JSONObject boxDetails = boxes.getJSONObject(i);
				if (!productDatabase.containsKey(orderID)) {
					addCustomerOrder(orderID, boxDetails.getString("ProductType"), boxDetails.getInt("Quantity"));
				} else {
					String productType = boxDetails.getString("ProductType");
					if (productDatabase.get(orderID).containsKey(productType)) {
						UpdateCustomerProductQuantity(orderID, productType, boxDetails.getInt("Quantity"));
					} else {
						addCustomerProduct(orderID, productType, boxDetails.getInt("Quantity"));
					}
				}
			}
		}
		// In the event that it does:
		else {
			// Get the product details currently associated with and stored for this orderID
			HashMap<String, Integer> orderProductDetails = this.productDatabase.get(orderID);

			for (int i = 0; i < boxes.length(); i++) {
				JSONObject boxDetails = boxes.getJSONObject(i);
				String productType = boxDetails.getString("ProductType");

				// If the order entry in the database already has this product in a certain
				// quantity:
				if (orderProductDetails.containsKey(productType)) {
					// Update that entry with the additional quantity of that product
					UpdateCustomerProductQuantity(orderID, productType, boxDetails.getInt("Quantity"));
				}
				// if it doesn't, simply add it to that order entry's product list:
				else {
					addCustomerProduct(orderID, productType, boxDetails.getInt("Quantity"));
				}
			}
		}
	}

	protected boolean orderProductsReady(String orderID) {
		/*
		 * Returns true if the order details (products and their quantities) are
		 * fulfilled in the database for that particular customer order.
		 */
		int productQuantity = 0;
		HashMap<String, Integer> orderProductDetails = this.productDatabase.get(orderID);

		JSONObject productsObject = new JSONObject();
		String IDCheckString = null;

		for (int i = 0; i < this.orderDetailsArray.length(); i++) {
			JSONObject orderData = this.orderDetailsArray.getJSONObject(i);

			if (orderID.equals(orderData.getString("guid"))) {
				IDCheckString = orderData.getString("guid");
				productsObject = orderData.getJSONObject("products");
				break;
			}
		}

		if (IDCheckString.equals(null)) {
			System.out
					.println("[" + getAID().getLocalName() + "]: ERROR: OrderID not found in this.orderDetailsArray ");
		}

		for (String productName : productsObject.keySet()) {
			int orderQuantity = productsObject.getInt(productName);

			try {
				productQuantity = orderProductDetails.get(productName);
			} catch (NullPointerException e) {
				return false;
			}

			if (productQuantity != orderQuantity) {
				return false;
			}
		}
		return true;
	}

	private class TimeUpdater extends CyclicBehaviour {
		public void action() {
			if (getAllowAction()) {
				finished();
			}
		}
	}

	private class PackagingPhaseMessageSender extends OneShotBehaviour {
		private AID receivingAgent = null;

		protected void findReceiver() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("order-aggregator");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				receivingAgent = result[0].getName();

			} catch (FIPAException fe) {
				System.out.println("[" + getAID().getLocalName() + "]: No OrderAggregator agent found.");
				fe.printStackTrace();
			}
		}

		public void action() {
			findReceiver();

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

			msg.addReceiver(receivingAgent);
			msg.setContent(createOrderBoxesJSONMessage(((LoadingBayAgent) baseAgent).readyOrderID));
			msg.setConversationId("packaged-orders");
			msg.setPostTimeStamp(System.currentTimeMillis());

			myAgent.send(msg);

			System.out.println("[" + getAID().getLocalName() + "]: Order details sent to OrderAggregator");
		}
	}

	private class OrderDetailsReceiver extends CyclicBehaviour {
		private String orderProcessorServiceType;
		private AID orderProcessor = null;
		private MessageTemplate mt;

		protected void findOrderProcessor() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			orderProcessorServiceType = "OrderProcessing";

			sd.setType(orderProcessorServiceType);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				if (result.length > 0) {
					orderProcessor = result[0].getName();
				}
			} catch (FIPAException fe) {
				System.out.println("[" + getAID().getLocalName() + "]: No OrderProcessor agent found.");
				fe.printStackTrace();
			}
		}

		public void action() {
			findOrderProcessor();

			mt = MessageTemplate.and(MessageTemplate.MatchSender(orderProcessor),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				// If a single order is provided, in a message:
				((LoadingBayAgent) baseAgent).orderDetailsArray.put(new JSONObject(msg.getContent()));

				// Enable this instead, if a list of orders is provided:
				/*
				 * JSONArray messagethis.orderDetailsArray = new JSONArray(msg.getContent());
				 * for (int i = 0 ; i < messagethis.orderDetailsArray.length() ; i++) {
				 * this.orderDetailsArray.put(messagethis.orderDetailsArray.get(i)); }
				 */
			} else {
				block();
			}
		}
	}

	private class ProductDetailsReceiver extends CyclicBehaviour {
		private MessageTemplate mt;

		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("boxes-ready"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				System.out.println("[" + getAID().getLocalName() + "]: Received product boxes from "
						+ msg.getSender().getLocalName());

				// This assumes a JSON object is sent by the preceding agent
				String boxesMessageContent = msg.getContent();
				JSONObject JSONData = new JSONObject(boxesMessageContent);
				String orderIDKey = "OrderID";
				String orderID = JSONData.getString(orderIDKey);

				updateBoxDatabase(boxesMessageContent);
				updateProductDatabase(boxesMessageContent);

				if (orderProductsReady(orderID)) {
					((LoadingBayAgent) baseAgent).readyOrderID = orderID;
					addBehaviour(new PackagingPhaseMessageSender());
				}
			} else {
				block();
			}
		}
	}
}
