package org.maas.agents;

// import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

// for shutdown behaviour
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;

import org.maas.agents.BaseAgent;


@SuppressWarnings("serial")
public class DummyAgent extends BaseAgent {
	protected void setup() {
        super.setup();
        // Printout a welcome message
		System.out.println("Hello! Dummy-agent "+getAID().getName()+" is ready.");

        this.register("Dummy-Agent", "JADE-bakery");
		addBehaviour(new DummyBehaviour());

	}
	protected void takeDown() {
        this.deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	/*
     * Prints a line on stdout every time step 
     */
    private class DummyBehaviour extends Behaviour {
        private boolean printed;
        public DummyBehaviour(){
            this.printed = false;
        }
        public void action() {
            if (!baseAgent.getAllowAction()) {
                return;
            }
            System.out.println("Inside DummyServer action");
            this.printed = true;
        }
        public boolean done(){
            if (!this.printed)
                return false;
            baseAgent.finished();
            myAgent.addBehaviour(new DummyBehaviour());
            return true;
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
			}
			catch (Exception e) {
			    //LOGGER.error(e);
			}

		}
	}
}
