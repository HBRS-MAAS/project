package org.right_brothers.agents;

import jade.core.Agent;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class BaseAgent extends Agent {

	private int currentDay;
	private int currentHour;
	private boolean allowAction = false;
	protected AID clockAgent = new AID("TimeKeeper", AID.ISLOCALNAME);
	protected AID messageQueueAgent = new AID("MessageQueue", AID.ISLOCALNAME);
	protected BaseAgent baseAgent = this;

	/*
	 * Setup to add behaviour to talk with clockAgent Call `super.setup()` from
	 * `setup()` function
	 */
	protected void setup() {
		this.addBehaviour(new PermitAction());
	}

	/*
	 * This function registers the agent to yellow pages Call this in `setup()`
	 * function
	 */
	protected void register(String type, String name) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	/*
	 * This function removes the agent from yellow pages Call this in `doDelete()`
	 * function
	 */
	protected void deRegister() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	/*
	 * This function sends finished message to clockAgent This function should be
	 * called by every agent which implements BaseAgent after the agent is done with
	 * the task it has to perform in a time step.
	 */
	protected void finished() {
		this.allowAction = false;
		ACLMessage finish = new ACLMessage(ACLMessage.INFORM);
		finish.addReceiver(this.clockAgent);
		finish.setContent("finished");
		this.send(finish);
	}

	protected boolean getAllowAction() {
		return allowAction;
	}

	protected int getCurrentDay() {
		return currentDay;
	}

	protected int getCurrentHour() {
		return currentHour;
	}

	/*
	 * This function is used as a middle man which uses the message for different
	 * visualisation methods Use `baseAgent.sendMessage(message)` instead of
	 * `myAgent.send(message)` in every behaviour.
	 */
	protected void sendMessage(ACLMessage msg) {
		this.send(msg);
		this.visualiseHistoricalView(msg);
		this.visualiseIndividualOrderStatus(msg);
		this.visualiseMessageQueuesByAgent(msg, false);
		this.visualiseOrderBoard(msg);
		this.visualiseStreetNetwork(msg);
	}

	/**
	 * This function is used as a middle man which uses the message for different
	 * visualisation methods Use `baseAgent.receiveMessage(message)` instead of
	 * `myAgent.receive(message)` in every behaviour.
	 */
	public ACLMessage receiveMessage(MessageTemplate mt) {
		ACLMessage msg = this.receive(mt);
		if (msg != null) {
			this.visualiseMessageQueuesByAgent(msg, true);
		}
		return msg;
	}

	/*
	 * implementation skeleton code for different visualisation methods
	 */
	protected void visualiseHistoricalView(ACLMessage msg) {
	}

	protected void visualiseIndividualOrderStatus(ACLMessage msg) {
	}

	protected void visualiseMessageQueuesByAgent(ACLMessage msg, Boolean in) {
		ACLMessage info = new ACLMessage(ACLMessage.INFORM);
		try {
			info.setContentObject(in);
			info.addReceiver(messageQueueAgent);
			this.send(info);
		} catch (IOException e) {
			System.err.println(
					String.format("Informing message queue agent about message %s failed: %s", msg, e.getMessage()));
		}
	}

	protected void visualiseOrderBoard(ACLMessage msg) {
	}

	protected void visualiseStreetNetwork(ACLMessage msg) {
	}

	/*
	 * Behaviour to receive message from clockAgent to proceed further with tasks of
	 * next time step
	 */
	private class PermitAction extends CyclicBehaviour {
		private MessageTemplate mt;
		private BaseAgent ba;

		public void action() {
			this.mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(baseAgent.clockAgent));
			ACLMessage msg = myAgent.receive(this.mt);
			if (msg != null) {
				String messageContent = msg.getContent();
				int counter = Integer.parseInt(messageContent);
				int day = counter / 24;
				int hour = counter % 24;
				currentDay = day;
				currentHour = hour;
				allowAction = true;
			} else {
				block();
			}
		}
	}
}
