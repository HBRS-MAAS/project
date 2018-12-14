package org.maas.agents;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

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
}
