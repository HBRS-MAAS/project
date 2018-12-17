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
	}
	protected void takeDown() {
        this.deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

	/*
     * Prints a line on stdout every time step 
     */
    protected void stepAction(){
        System.out.println("Printing inside DummyAgent");
        baseAgent.finished();
    }
}
