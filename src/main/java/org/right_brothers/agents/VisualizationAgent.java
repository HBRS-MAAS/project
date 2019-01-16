package org.right_brothers.agents;

import org.maas.agents.BaseAgent;
import org.maas.agents.TimeKeeper;
import org.maas.utils.Time;
import org.right_brothers.visualizer.ui.Visualizer;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


@SuppressWarnings("serial")
public class VisualizationAgent extends BaseAgent {
	private String scenarioDirectory;
	private Time endTime;
	
    private Visualizer guiWindow;

	protected void setup() {
		super.setup();
		
		Object[] args = getArguments();
        if (args != null && args.length > 0) {
            scenarioDirectory = (String) args[0];
            String endTimeString = (String) args[1];
            endTime = new Time(endTimeString);
        } else {
            scenarioDirectory = "small";
            endTime = new Time(1,12,0);
        }
        
		// Printout a welcome message
		System.out.println("Hello! Visualization-agent "+getAID().getName()+" is ready for " + scenarioDirectory + " scenario");
		
		this.register("board-visualisation", "board-visualisation-agent");

        // launch the gui window in another thread
        Thread thread = new Thread() {
            @Override
            public void run() {
                Visualizer.run(baseAgent, scenarioDirectory);
            }
        };
    	thread.start();
        
    	guiWindow = Visualizer.waitForInstance();
		addBehaviour(new MessageServer());
	}
	
    @Override
    protected void stepAction(){
    	guiWindow.setTime(baseAgent.getCurrentTime());
    	
        if (baseAgent.getCurrentTime().lessThan(this.endTime) || Visualizer.currentInstance == null) {
        	finished();
        }
    }
    
	protected void takeDown() {
		this.deRegister();
		System.out.println(getAID().getLocalName() + ": Terminating.");
	}

    private class MessageServer extends CyclicBehaviour {
        public void action() {
        	MessageTemplate mt = MessageTemplate.not(MessageTemplate.MatchPerformative(TimeKeeper.BROADCAST_TIMESTEP_PERFORMATIVE));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                guiWindow.updateBoard(msg.getConversationId().toLowerCase(), msg.getContent());
            }
            else {
                block();
            }
        }
    }
}
