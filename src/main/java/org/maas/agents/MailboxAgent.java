package org.maas.agents;

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
public class MailboxAgent extends BaseAgent {

	protected void setup() {
		super.setup();
		System.out.println("Hello! MailboxAgent " + getAID().getName() + " is ready.");

		register("mailbox", "mailbox");

		addBehaviour(new truckDeliveryCompletionProcessor());
		addBehaviour(new TimeUpdater());
	}

	protected void takeDown() {
		deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	protected class DeliveryStatus {
		String orderDeliveredTo;
		String orderDeliveredBy;
		int dayOfDelivery;
		int timeOfDelivery;
		int numOfBoxes;
		String producedBy;
	}

	protected DeliveryStatus parseTruckConfirmationMessage(String truckMessage) {
		DeliveryStatus status = new DeliveryStatus();

		JSONObject truckMessageData = new JSONObject(truckMessage);
		JSONObject deliveryStatus = truckMessageData.getJSONObject("DeliveryStatus");

		status.orderDeliveredTo = deliveryStatus.getString("OrderDeliveredTo");
		status.orderDeliveredBy = deliveryStatus.getString("OrderDeliveredBy");
		status.dayOfDelivery = deliveryStatus.getInt("DayOfDelivery");
		status.timeOfDelivery = deliveryStatus.getInt("TimeOfDelivery");
		status.numOfBoxes = deliveryStatus.getInt("NumOfBoxes");
		status.producedBy = deliveryStatus.getString("ProducedBy");

		return status;
	}

	private class TimeUpdater extends CyclicBehaviour {
		public void action() {
			if (getAllowAction()) {
				finished();
			}
		}
	}

	private class truckDeliveryCompletionProcessor extends CyclicBehaviour {
		private MessageTemplate mt;
		private AID[] receiverAgents;

		protected void findReceivers() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			// SERVICE TYPE FOR RECEIVING ORDER CONFIRMATIONS:
			sd.setType("order-confirmation");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				receiverAgents = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					receiverAgents[i] = result[i].getName();
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("DeliveryConfirmation"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String truckMessageContent = msg.getContent();

				System.out.println("[" + getAID().getLocalName() + "]: Received order completion message from "
						+ msg.getSender().getLocalName());
				// At the moment, this list includes all customers as well.
				// TODO: Send the message only to the specific customer
				findReceivers();
				ACLMessage orderConfirmation = new ACLMessage(ACLMessage.INFORM);

				for (int i = 0; i < receiverAgents.length; ++i) {
					orderConfirmation.addReceiver(receiverAgents[i]);
				}

				orderConfirmation.setContent(truckMessageContent);
				orderConfirmation.setConversationId("order-confirmation");
				orderConfirmation.setPostTimeStamp(System.currentTimeMillis());
				myAgent.send(orderConfirmation);

				System.out.println(
						"[" + getAID().getLocalName() + "]: Relayed order completion message to all concerned agents");
			} else {
				block();
			}
		}
	}
}