package org.maas.agents;

import org.json.*;

import jade.core.AID;
import jade.core.behaviours.*;
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

	private class TimeUpdater extends CyclicBehaviour {
		public void action() {
			if (getAllowAction()) {
				finished();
			}
		}
	}

	private class truckDeliveryCompletionProcessor extends CyclicBehaviour {
		private MessageTemplate mt;

		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("DeliveryConfirmation"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String truckMessageContent = msg.getContent();

				System.out.println("[" + getAID().getLocalName() + "]: Received order completion message from "
						+ msg.getSender().getLocalName());
				
				ACLMessage orderConfirmation = new ACLMessage(ACLMessage.INFORM);
				
				JSONObject truckMessageData = new JSONObject(truckMessageContent);
				
				String orderID = truckMessageData.getJSONObject("DeliveryStatus").getString("OrderID");
				String customerID = truckMessageData.getJSONObject("DeliveryStatus").getString("OrderDeliveredTo");
				
				orderConfirmation.setContent(truckMessageContent);
				orderConfirmation.addReceiver(new AID(customerID, AID.ISLOCALNAME));
				orderConfirmation.setConversationId(orderID);
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