package org.maas.agents;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.maas.utils.Time;

import org.maas.JSONConverter;
import org.maas.messages.DoughNotification;
import org.maas.messages.ProofingRequest;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// This agent receives a ProofingRequest, executes it ands sends a DoughNotification to the interface agent of the Baking Stage.

public class Proofer extends BaseAgent {
    private AID bakingInterfaceAgent;

    private AtomicBoolean proofingInProcess = new AtomicBoolean(false);
    private AtomicInteger messageProcessing = new AtomicInteger(0);
    private AtomicInteger proofingCounter = new AtomicInteger(0);

    private Vector<String> guids;
    private String productType;
    private Vector<Integer> productQuantities;

    private AID doughManager;
    private String bakeryId;
    private String doughManagerAgentName;
    private String bakingInterfaceAgentName;

    private boolean isAvailable = true;
    private Float proofingTime;
    private AtomicBoolean isInProductionTime = new AtomicBoolean (false);

    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args != null && args.length > 0){
            this.bakeryId = (String) args[0];
        }

        this.register("Proofer_" + bakeryId, "JADE-bakery");

        System.out.println("Hello! " + getAID().getLocalName() + " is ready.");

        getBakingInterfaceAID();
        getDoughManagerAID();

        addBehaviour(new timeTracker());
        addBehaviour(new ReceiveProposalRequests());
        addBehaviour(new ReceiveProofingRequests());
    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        baseAgent.deRegister();
    }

    public void getBakingInterfaceAID() {
        bakingInterfaceAgentName = "BakingInterface_" + bakeryId;
        bakingInterfaceAgent = new AID(bakingInterfaceAgentName, AID.ISLOCALNAME);
    }

    public void getDoughManagerAID() {
        // Name of the doughManager the Proofer communicates with
        doughManagerAgentName = "DoughManager_" + bakeryId;
        doughManager = new AID(doughManagerAgentName, AID.ISLOCALNAME);
    }


   private class timeTracker extends CyclicBehaviour {
       public void action() {
           if (!baseAgent.getAllowAction()) {
               return;
           }else{
               if (proofingInProcess.get() && isInProductionTime.get()){
                   int curCount = proofingCounter.incrementAndGet();
                   System.out.println(">>>>> Proofing Counter -> " + getAID().getLocalName() + " " + proofingCounter + " <<<<<");
                   addBehaviour(new Proofing());
               }
           }
           if (messageProcessing.get() <= 0)
           {
               // Production time is from midnight to lunch (from 00.00 hrs to 12 hrs)
               if ((baseAgent.getCurrentTime().greaterThan(new Time(baseAgent.getCurrentDay(), 0, 0)) ||

                       baseAgent.getCurrentTime().equals(new Time(baseAgent.getCurrentDay(), 0, 0))) &&

                       baseAgent.getCurrentTime().lessThan(new Time(baseAgent.getCurrentDay(), 12, 0)))
                {

                   isInProductionTime.set(true);
                }
                else{

                    isInProductionTime.set(false);
                }

               baseAgent.finished();
           }
       }
   }

   private class ReceiveProposalRequests extends CyclicBehaviour{
       public void action(){
           messageProcessing.incrementAndGet();

           MessageTemplate mt = MessageTemplate.and(
               MessageTemplate.MatchPerformative(ACLMessage.CFP),
               MessageTemplate.MatchConversationId("proofing-request"));

           ACLMessage msg = baseAgent.receive(mt);

           if (msg != null){
               String content = msg.getContent();
               // System.out.println(getAID().getLocalName() + "has received a proposal request from " + msg.getSender().getName());

               ACLMessage reply = msg.createReply();
               if (isAvailable){
                   //System.out.println(getAID().getLocalName() + " is available");
                   reply.setPerformative(ACLMessage.PROPOSE);
                   reply.setContent("Hey I am free, do you wanna use me ;)?");
               }else{
                   // System.out.println(getAID().getLocalName() + " is unavailable");
                   reply.setPerformative(ACLMessage.REFUSE);
                   reply.setContent("Sorry, I am married potato :c");
               }
               baseAgent.sendMessage(reply);
               messageProcessing.decrementAndGet();
           }

           else{
               messageProcessing.decrementAndGet();
               block();
           }
       }
   }

    /* This is the behaviour used for receiving proofing requests */
    private class ReceiveProofingRequests extends CyclicBehaviour {
        public void action() {

            messageProcessing.getAndIncrement();
            MessageTemplate mt =
                MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);

            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                ACLMessage reply = msg.createReply();

                if (!isAvailable){
                    // System.out.println(getAID().getLocalName()  + " is already taken");

                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("Proofer is taken");
                    //System.out.println(getAID().getLocalName() + " failed proofing of " + msg.getContent());
                }
                else{

                    isAvailable = false;

                    String content = msg.getContent();
                    System.out.println(getAID().getLocalName() + " WILL perform Proofing for " + msg.getSender().getLocalName() + ": " + content);

                    ProofingRequest proofingRequest = JSONConverter.parseProofingRequest(content);

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Proofing request was received");

                    proofingTime = proofingRequest.getProofingTime();
                    guids = proofingRequest.getGuids();
                    productType = proofingRequest.getProductType();
                    productQuantities = proofingRequest.getProductQuantities();

                    addBehaviour(new Proofing());

                }
                baseAgent.sendMessage(reply);
                messageProcessing.decrementAndGet();
            }

            else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }


    // This is the behaviour that performs the proofing process.
    private class Proofing extends OneShotBehaviour {
        public void action(){
            if (proofingCounter.get() < proofingTime){
                if (!proofingInProcess.get()){
                    // System.out.println("======================================");
                    // System.out.println("----> " + getAID().getLocalName() + " Proofing for " + proofingTime + " " + productType);
                    // System.out.println("======================================");
                    proofingInProcess.set(true);
                    isAvailable = false;
                }

            }else{
                proofingInProcess.set(false);
                isAvailable = true;
                proofingCounter.set(0);
                System.out.println("======================================");
                System.out.println(getAID().getLocalName() + " Finishing proofing " + productType + guids);
                System.out.println("======================================");

                addBehaviour(new SendDoughNotification());
            }
        }
    }


    // This is the behaviour used for sending a doughNotification msg to the BakingInterface agent
    private class SendDoughNotification extends Behaviour {
        private MessageTemplate mt;
        private int option = 0;
        private Gson gson = new Gson();
        private DoughNotification doughNotification = new DoughNotification(guids, productType, productQuantities);
        private String doughNotificationString = gson.toJson(doughNotification);

        public void action() {

            messageProcessing.getAndIncrement();

            switch (option) {
                case 0:
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                    msg.setContent(doughNotificationString);
                    msg.setConversationId("dough-Notification");
                    msg.addReceiver(bakingInterfaceAgent);

                    baseAgent.sendMessage(msg);

                    System.out.println("----> " + getAID().getLocalName() + " Sent dough Notification to " + bakingInterfaceAgent);
                    messageProcessing.getAndDecrement();
                    option = 1;
                    break;

                case 1:
                    // MatchConversationId dough-Notification
                    mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                           MessageTemplate.MatchConversationId("dough-Notification-reply"));

                    ACLMessage reply = baseAgent.receive(mt);

                    if (reply != null) {
                        // System.out.println(getAID().getLocalName() + " Received confirmation from " + reply.getSender());
                        option = 2;
                    }
                    else {
                        messageProcessing.getAndDecrement();
                        block();
                    }
                    messageProcessing.decrementAndGet();
                    break;

                default:
                    messageProcessing.decrementAndGet();
                    break;
                }
            }

        public boolean done() {
            return option == 2;
        }
    }
}
