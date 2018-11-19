package src.main.java.org.yourteamname.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class MessageQueueAgent extends Agent {
	private static final long serialVersionUID = -5310054528477305012L;
	private boolean gui = false;

	private List<AgentQueueMap> agentQueueMaps;

	// Put agent initializations here
	@SuppressWarnings("serial")
	protected void setup() {
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			gui = (boolean) args[0];
		}

		agentQueueMaps = new EventPrintList();

		// Register service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("message-queue");
		sd.setName("JADE-message-queue");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving requests for offer from buyer agents
		addBehaviour(new TickerBehaviour(this, 60000) {

			@Override
			protected void onTick() {
				try {
					DFAgentDescription[] agents = listAgents();
					for (DFAgentDescription agent : agents) {
						agentQueueMaps.add(new AgentQueueMap(agent.getName().getName()));
					}
				} catch (FIPAException e) {
					System.err.println(String.format("An error occured while starting the %s: %s",
							MessageQueueAgent.class.getSimpleName(), e.getMessage()));
//				e.printStackTrace();
				}
			}
		});
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					for (AgentQueueMap knownAgent : agentQueueMaps) {
						if (msg.getSender().getName().equals(knownAgent.getName())) {
							Boolean in;
							try {
								in = (Boolean) msg.getContentObject();
								if (in) {
									knownAgent.increaseAmount();
								} else {
									knownAgent.decreaseAmount();
									if (knownAgent.getAmount() < 1) {
										agentQueueMaps.remove(knownAgent);
									}
								}
								return;
							} catch (UnreadableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					agentQueueMaps.add(new AgentQueueMap(msg.getSender().getName(), 1));
				} else {
					block();
				}
			}
		});
		if (gui) {
			startGui();
		}

	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Bakery-agent " + getAID().getName() + " terminating.");
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private void startGui() {
//TODO
	}

	private DFAgentDescription[] listAgents() throws FIPAException {
		return DFService.search(this, new DFAgentDescription());
	}

	class EventPrintList extends ArrayList<AgentQueueMap> {

		private static final long serialVersionUID = 328647187803597475L;

		@Override
		public boolean remove(Object a) {
			return processEvent(() -> {
				return super.remove(a);
			});
		}

		@Override
		public boolean add(AgentQueueMap a) {
			return processEvent(() -> {
				return super.add(a);
			});
		}

		private boolean processEvent(Callable<Boolean> r) {
			boolean ret;
			try {
				ret = r.call();
			} catch (Exception e) {
				return false;
			}
			if (ret) {
				System.out.println("MessageQueue states: " + this);
			}
			return ret;
		}

	}

	class AgentQueueMap {
		private String name;
		private int amount;

		public AgentQueueMap() {
			this("");
		}

		public AgentQueueMap(String name) {
			this(name, 0);
		}

		public AgentQueueMap(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		public void increaseAmount() {
			this.amount++;
		}

		public void decreaseAmount() {
			this.amount--;
		}

		@Override
		public String toString() {
			return String.format("{name:%s, amount:%s}", name, amount);
		}

	}

}