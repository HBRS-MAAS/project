package org.maas.agents;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class TimedAgent extends BaseAgent{

	private int currentDay;
    private int currentHour;
    private boolean allowAction = false;
    protected AID clockAgent = new AID("TimeKeeper", AID.ISLOCALNAME);
    protected TimedAgent timedAgent = this;
    
    /* Setup to add behaviour to talk with clockAgent
     * Call `super.setup()` from `setup()` function
     */
    protected void setup() {
        this.addBehaviour(new PermitAction());
    }
    
    protected void register(ArrayList<String> type, String name){
        ArrayList<String> services = type;
        services.add("timed-agent");
        
        super.register(services, name);
    }
    
    /* This function sends finished message to clockAgent
     * This function should be called by every agent which implements TimedAgent
     * after the agent is done with the task it has to perform in a time step.
     */
    protected void finished(){
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
    
    /* Behaviour to receive message from clockAgent to proceed further with
     * tasks of next time step
     */
    private class PermitAction extends CyclicBehaviour {
        private MessageTemplate mt;

        public void action(){
            this.mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(timedAgent.clockAgent));
            ACLMessage msg = myAgent.receive(this.mt);
            if (msg != null) {
                String messageContent = msg.getContent();
                int counter = Integer.parseInt(messageContent);
                int day = counter / 24;
                int hour = counter % 24;
                currentDay = day;
                currentHour = hour;
                allowAction = true;
            }
            else {
                block();
            }
        }
   }
}
