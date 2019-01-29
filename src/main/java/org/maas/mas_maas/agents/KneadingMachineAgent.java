package org.mas_maas.agents;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.maas.JSONConverter;
import org.maas.Objects.KneadingMachine;
import org.maas.agents.BaseAgent;
import org.maas.messages.KneadingNotification;
import org.maas.messages.KneadingRequest;
import org.maas.utils.Time;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
public class KneadingMachineAgent extends BaseAgent {
    private AID doughManagerAgent;
    private AID loggerAgent;

    private AtomicBoolean kneadingInProcess = new AtomicBoolean(false);
    private AtomicInteger messageProcessing = new AtomicInteger(0);
    private AtomicInteger kneadingCounter = new AtomicInteger(0);

    private KneadingMachine kneadingMachine;

    private Vector<String> guids;
    private String productType;

    private String kneadingMachineName;
    private String doughManagerName;

    private Float kneadingTime;

    private AtomicBoolean isInProductionTime = new AtomicBoolean(false);

    protected void setup() {
        super.setup();

        Object[] args = getArguments();

        if(args != null && args.length > 0){
            this.kneadingMachine = (KneadingMachine) args[0];
            this.kneadingMachineName = (String) args[1];
            this.doughManagerName = (String) args[2];
        }

        this.getDoughManagerAID();

        this.getLoggerAID();

        System.out.println("Hello! " + getAID().getLocalName() + " is ready." + "its DougManager is: " + doughManagerName);

        this.register(this.kneadingMachineName, "JADE-bakery");

        kneadingMachine.setAvailable(true);

        addBehaviour(new timeTracker());
        addBehaviour(new ReceiveProposalRequests());
        addBehaviour(new ReceiveKneadingRequests());


    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        this.deRegister();
    }

    public void getDoughManagerAID() {
        doughManagerAgent = new AID (doughManagerName, AID.ISLOCALNAME);

    }

    public void getLoggerAID() {
        loggerAgent = new AID ("LoggingAgent", AID.ISLOCALNAME);
    }

    private class timeTracker extends CyclicBehaviour {
        public void action() {
            if (!baseAgent.getAllowAction()) {
                return;
            }else{
                if (kneadingInProcess.get() && isInProductionTime.get()){
                    int curCount = kneadingCounter.incrementAndGet();
                    System.out.println("\t >>>>> Kneading Counter -> " + getAID().getLocalName() + " " + kneadingCounter + " <<<<<");
                    addBehaviour(new Kneading());
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
                MessageTemplate.MatchConversationId("kneading-request"));

            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null){
                String content = msg.getContent();
                // System.out.println(" << << " + getAID().getLocalName() + " has received a CFP from "
                    // + msg.getSender().getName() + " for " + content);

                ACLMessage reply = msg.createReply();
                if (kneadingMachine.isAvailable()){
                    //System.out.println(getAID().getLocalName() + " is available");
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent("Hey I am free, do you wanna use me ;)? " + content);
                }else{
                    // System.out.println(getAID().getLocalName() + " is unavailable");
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("Sorry, I am married potato :c " + content);
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

    // Receiving Kneading requests behavior
    private class ReceiveKneadingRequests extends CyclicBehaviour {
        public void action() {
            messageProcessing.incrementAndGet();

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                ACLMessage reply = msg.createReply();

                if (!kneadingMachine.isAvailable()){
                    // System.out.println(getAID().getLocalName()  + " is already taken");

                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("KneadingMachine is taken");
                    // System.out.println(getAID().getLocalName() + " failed kneading of " + msg.getContent());
                }
                else{
                    kneadingMachine.setAvailable(false);

                    String content = msg.getContent();
                    // System.out.println(getAID().getLocalName() + " WILL perform Kneading for \n \t"
                    //     + msg.getSender().getLocalName() + ": " + content);

                    KneadingRequest kneadingRequest = JSONConverter.parseKneadingRequest(content);

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Kneading request was received " + content);

                    kneadingTime = kneadingRequest.getKneadingTime();
                    guids = kneadingRequest.getGuids();
                    productType = kneadingRequest.getProductType();

                    addBehaviour(new Kneading());
                }

                baseAgent.sendMessage(reply);
                messageProcessing.decrementAndGet();

            }else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    // performs Kneading process
    private class Kneading extends OneShotBehaviour {
        public void action(){
            if (kneadingCounter.get() < kneadingTime){
                if (!kneadingInProcess.get()){
                    System.out.println(getAID().getLocalName() + " Kneading for -> " + kneadingTime + " " + productType + " for guids " + guids);
                    kneadingInProcess.set(true);
                    kneadingMachine.setAvailable(false);
                }

            }else{
                kneadingInProcess.set(false);
                kneadingMachine.setAvailable(true);
                kneadingCounter.set(0);
                System.out.println(getAID().getLocalName() + " finished kneading " + productType + " for guids " + guids);
                // System.out.println("----> " + guidAvailable + " finished Kneading");
                addBehaviour(new SendKneadingNotification());
            }
        }
    }

    // Send a kneadingNotification msg to the doughManager agents
    private class SendKneadingNotification extends Behaviour {
        private MessageTemplate mt;
        private int option = 0;
        private Gson gson = new Gson();
        private KneadingNotification kneadingNotification = new KneadingNotification(guids, productType);
        private String kneadingNotificationString = gson.toJson(kneadingNotification);

        public void action() {
            messageProcessing.incrementAndGet();

            switch (option) {
                case 0:

                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                    msg.setContent(kneadingNotificationString);
                    msg.setConversationId("kneading-notification");

                    msg.addReceiver(doughManagerAgent);
                    msg.addReceiver(loggerAgent);

                    baseAgent.sendMessage(msg);

                    // System.out.println(getAID().getLocalName() + " Sent kneadingNotification");
                    messageProcessing.decrementAndGet();
                    option = 1;
                    break;

                case 1:
                    mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                        MessageTemplate.MatchConversationId("kneading-notification-reply"));

                    ACLMessage reply = baseAgent.receive(mt);

                    if (reply != null) {
                        // System.out.println(getAID().getLocalName() + " Received kneading notification confirmation from " + reply.getSender());
                        option = 2;
                    }
                    else {
                        messageProcessing.decrementAndGet();
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
