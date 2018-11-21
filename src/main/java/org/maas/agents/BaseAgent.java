package org.maas.agents;

import java.util.ArrayList;

import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class BaseAgent extends Agent {

    /* This function registers the agent to yellow pages
     * Call this in `setup()` function
     */
    protected void register(ArrayList<String> type, String name){
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        
        for (int i = 0; i < type.size(); i++)
        {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type.get(i));
            sd.setName(name);
            dfd.addServices(sd);
        }
        
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    
    /* This function removes the agent from yellow pages
     * Call this in `doDelete()` function
     */
    protected void deRegister() {
    	try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    /* This function is used as a middle man which uses the message
     * for different visualisation methods
     * Use `baseAgent.sendMessage(message)` instead of `myAgent.send(message)`
     * in every behaviour.
     * */
    protected void sendMessage(ACLMessage msg) {
        this.send(msg);
        this.visualiseHistoricalView(msg);
        this.visualiseIndividualOrderStatus(msg);
        this.visualiseMessageQueuesByAgent(msg);
        this.visualiseOrderBoard(msg);
        this.visualiseStreetNetwork(msg);
    }

    /* implementation skeleton code for different visualisation methods
     */
    protected void visualiseHistoricalView(ACLMessage msg) {
    }
    protected void visualiseIndividualOrderStatus(ACLMessage msg) {
    }
    protected void visualiseMessageQueuesByAgent(ACLMessage msg) {
    }
    protected void visualiseOrderBoard(ACLMessage msg) {
    }
    protected void visualiseStreetNetwork(ACLMessage msg) {
    }
}
