package org.mas_maas.agents;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.mas_maas.JSONConverter;
import org.mas_maas.messages.DoughNotification;
import org.mas_maas.messages.ProofingRequest;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Proofer extends BaseAgent {
    private AID [] bakingManagerAgents;

    private AtomicBoolean proofingInProcess = new AtomicBoolean(false);
    private AtomicInteger messageProcessing = new AtomicInteger(0);
    private AtomicInteger proofingCounter = new AtomicInteger(0);

    private Vector<String> guids;
    private String productType;
    private Vector<Integer> productQuantities;

    protected void setup() {
        super.setup();

        System.out.println(getAID().getLocalName() + " is ready.");

        this.register("Proofer", "JADE-bakery");

        // Get Agents AIDS
        this.getDoughManagerAIDs();
        this.getBakingManagerAIDs();

        proofingCounter.set(0);
        // Time tracker behavior
        addBehaviour(new timeTracker());
        addBehaviour(new ReceiveProofingRequests());
    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        this.deRegister();
    }

    public void getDoughManagerAIDs() {
        AID [] doughManagerAgents;
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("Dough-manager");
        template.addServices(sd);
        try {
            DFAgentDescription [] result = DFService.search(this, template);
            System.out.println(getAID().getLocalName() + "Found the following Dough-manager agents:");
            doughManagerAgents = new AID [result.length];

            for (int i = 0; i < result.length; ++i) {
                doughManagerAgents[i] = result[i].getName();
                System.out.println(doughManagerAgents[i].getName());
            }

        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }


    public void getBakingManagerAIDs() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("Baking-manager");
        template.addServices(sd);
        try {
            DFAgentDescription [] result = DFService.search(this, template);
            System.out.println(getAID().getLocalName() + "Found the following Baking-interface agents:");
            bakingManagerAgents = new AID [result.length];

            for (int i = 0; i < result.length; ++i) {
                bakingManagerAgents[i] = result[i].getName();
                System.out.println(bakingManagerAgents[i].getName());
            }

        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class timeTracker extends CyclicBehaviour {
        public void action() {
            // first we make sure we are even allowed to do anything
            if (!baseAgent.getAllowAction()) {
                return;
            }

            // once we know our agent is able to do an action check if we need to actually do anything
            if (messageProcessing.get() <= 0)
            {
                if (proofingInProcess.get()){
                    int curCount = proofingCounter.incrementAndGet();
                    //System.out.println("-------> Proofer Clock-> " + baseAgent.getCurrentHour());
                    //System.out.println("-------> Proofer Counter -> " + curCount);
                }
                baseAgent.finished();
            }
        }
    }

      /* This is the behaviour used for receiving proofing requests */
    private class ReceiveProofingRequests extends CyclicBehaviour {
        public void action() {

            messageProcessing.getAndIncrement();
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("proofing-request"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                System.out.println(getAID().getLocalName() + " Received proofing request from " + msg.getSender());
                String content = msg.getContent();
                System.out.println("Proofing request contains -> " + content);
                ProofingRequest proofingRequest = JSONConverter.parseProofingRequest(content);

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.setContent("Proofing request was received");
                reply.setConversationId("proofing-request-reply");
                baseAgent.sendMessage(reply);

                Float proofingTime = proofingRequest.getProofingTime();
                guids = proofingRequest.getGuids();
                productType = proofingRequest.getProductType();
                productQuantities = proofingRequest.getProductQuantities();

                proofingInProcess.set(true);
                addBehaviour(new Proofing(proofingTime));
                messageProcessing.getAndDecrement();
            }
            else {
                messageProcessing.getAndDecrement();
                block();
            }
        }
    }

  // This is the behaviour that performs the proofing process.

    private class Proofing extends Behaviour {
        private float proofingTime;
        private int option = 0;

        public Proofing(float proofingTime){
            this.proofingTime = proofingTime;
            System.out.println(getAID().getLocalName() + " proofing for " + proofingTime);
        }

        public void action(){
            if (proofingCounter.get() >= proofingTime){
                proofingCounter.set(0);
                // TODO do we need another bool to make sure this gets sent?
                addBehaviour(new SendDoughNotification());
                proofingInProcess.set(false);
            }
        }

        public boolean done(){
            if (proofingInProcess.get()){
                return false;
            }else{
                return true;
            }
        }
    }



    // This is the behaviour used for sending a doughNotification msg to the BakingInterface agent
    private class SendDoughNotification extends Behaviour {
        // private AID [] bakingManagerAgents;
        private MessageTemplate mt;
        private int option = 0;
        private Gson gson = new Gson();
        private DoughNotification doughNotification = new DoughNotification(guids, productType, productQuantities);
        private String doughNotificationString = gson.toJson(doughNotification);


        //TODO remove me when debugging is done
        private boolean killMessageSent = false;

        public void action() {

            messageProcessing.getAndIncrement();
            switch (option) {
                case 0:
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setContent(doughNotificationString);
                    msg.setConversationId("dough-notification");

                    // Send doughNotification msg to bakingManagerAgents
                    for (int i=0; i<bakingManagerAgents.length; i++){
                        msg.addReceiver(bakingManagerAgents[i]);
                    }
                    // msg.setReplyWith("msg" + System.currentTimeMillis
                    baseAgent.sendMessage(msg);  // calling sendMessage instead of send

                    option = 1;
                    System.out.println(getAID().getLocalName() + " Sent doughNotification");
                    messageProcessing.getAndDecrement();
                    break;

                case 1:

                    mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                        MessageTemplate.MatchConversationId("dough-notification-reply"));

                    ACLMessage reply = baseAgent.receive(mt);

                    if (reply != null) {
                        System.out.println(getAID().getLocalName() + " Received confirmation from " + reply.getSender());
                        option = 2;
                        messageProcessing.getAndDecrement();
                    }
                    else {
                        if (!killMessageSent)
                        {
                            System.out.println("Waiting for reply. Kill me!");
                            killMessageSent = true;
                        }
                        messageProcessing.getAndDecrement();
                        block();
                    }
                    break;

                default:
                    break;
                }
            }

        public boolean done() {
            if (option == 2) {
                baseAgent.finished();
                //myAgent.doDelete();
                return true;
            }
            return false;
        }
    }
}
