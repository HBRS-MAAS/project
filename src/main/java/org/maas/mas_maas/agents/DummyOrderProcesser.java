package org.mas_maas.agents;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.maas.JSONConverter;
import org.maas.Objects.Bakery;
import org.maas.Objects.Client;
import org.maas.Objects.OrderMas;
import org.maas.agents.BaseAgent;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DummyOrderProcesser extends BaseAgent {
    private ArrayList<AID> doughManagerAgents = new ArrayList<AID>();
    private ArrayList<AID> bakingInterfaceAgents = new ArrayList<AID>();
    private Vector<Bakery> bakeries;
    private String scenarioPath;
    private Vector<OrderMas> orders = new Vector<OrderMas>();
    private AtomicInteger messageProcessing = new AtomicInteger(0);


    protected void setup(){
        super.setup();
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.scenarioPath = (String) args[0];
        }

        System.out.println("Hello! " + getAID().getLocalName() + " is ready.");
        this.register("DummyOrderProcesser", "JADE-bakery");

        getBakeries(this.scenarioPath);
        getDoughManagerAIDs();
        getBakingInterfaceAIDs();

        try {
            //Read the orders from the scenarioPath
            getOrderInfo();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addBehaviour(new timeTracker());
        processOrders();
    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        this.deRegister();
    }

    private class timeTracker extends CyclicBehaviour {
        public void action() {
            if (!baseAgent.getAllowAction()) {
                return;
            }
            // only advance if we aren't currently processing any messages
            if (messageProcessing.get() <= 0)
            {
                baseAgent.finished();
            }
        }
    }

    public void getBakeries(String scenarioPath){
        String jsonDir = scenarioPath;
        try {
            // System.out.println("Working Directory = " + System.getProperty("user.dir"));
            String bakeryFile = new Scanner(new File(jsonDir + "bakeries.json")).useDelimiter("\\Z").next();
            this.bakeries = JSONConverter.parseBakeries(bakeryFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // public void getDoughManagerAIDs(){
    //     // For now get just the first one to test
    //     for (Bakery bakery : bakeries) {
    //     //Bakery bakery = bakeries.get(0);
    //         String doughManagerAgentName = "DoughManager_" + bakery.getGuid();
    //         doughManagerAgents.add(new AID (doughManagerAgentName, AID.ISLOCALNAME));
    //     }
    // }

    public void getDoughManagerAIDs(){
        // For now get just the first one to test

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        for (Bakery bakery : bakeries) {
        //Bakery bakery = bakeries.get(0);
            String doughManagerAgentName = "DoughManager_" + bakery.getGuid();
            sd.setType(doughManagerAgentName);
            template.addServices(sd);
            // doughManagerAgents.add(new AID (doughManagerAgentName, AID.ISLOCALNAME));
            try {
                DFAgentDescription[] result = DFService.search(this, template);
                for (int i = 0; i < result.length; ++i) {
                    // doughManagerAgents[j] = result[i].getName();
                    doughManagerAgents.add(result[i].getName());
                    // System.out.println(doughManagerAgents[j].getName());
                }
            }
            catch (FIPAException fe) {
                System.out.println("-----> Failed to find " + doughManagerAgentName);
                fe.printStackTrace();
            }
        }
    }

    public void getBakingInterfaceAIDs(){
        // For now get just the first one to test

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        for (Bakery bakery : bakeries) {
        //Bakery bakery = bakeries.get(0);
            String bakingInterfaceAgentName = "BakingInterface_" + bakery.getGuid();
            sd.setType(bakingInterfaceAgentName);
            template.addServices(sd);
            // doughManagerAgents.add(new AID (doughManagerAgentName, AID.ISLOCALNAME));
            try {
                DFAgentDescription[] result = DFService.search(this, template);
                for (int i = 0; i < result.length; ++i) {
                    // doughManagerAgents[j] = result[i].getName();
                    bakingInterfaceAgents.add(result[i].getName());
                    // System.out.println(doughManagerAgents[j].getName());
                }
            }
            catch (FIPAException fe) {
                System.out.println("-----> Failed to find " + bakingInterfaceAgentName);
                fe.printStackTrace();
            }
        }
    }

    // public void getBakingInterfaceAIDs(){
    //     // For now get just the first one to test
    //     for (Bakery bakery : bakeries) {
    //     //Bakery bakery = bakeries.get(0);
    //         String bakingInterfaceAgentName = "BakingInterface_" + bakery.getGuid();
    //         bakingInterfaceAgents.add(new AID (bakingInterfaceAgentName, AID.ISLOCALNAME));
    //     }
    // }

    private void getOrderInfo() throws FileNotFoundException{
        String clientFile = new Scanner(new File(this.scenarioPath+ "clients.json")).useDelimiter("\\Z").next();
        Vector<Client> clients = JSONConverter.parseClients(clientFile);
        for (Client client : clients){
            for (OrderMas order : client.getOrders()){
                // System.out.println(order);
                orders.add(order);
            }
        }
    }

    public void processOrders(){
        Gson gson = new Gson();
        Random rand = new Random();
        //OrderMas order = orders.get(0);
        for (OrderMas order : orders){
            //Randomly select the index of the DoughManager and BakingInterface to send the order to
            int index = rand.nextInt(doughManagerAgents.size());

            AID doughManagerAgent = doughManagerAgents.get(index);
            
            String orderString = gson.toJson(order);


            if (bakingInterfaceAgents.size() > 0){

                AID bakingInterfaceAgent = bakingInterfaceAgents.get(index);

                addBehaviour(new sendOrder(orderString, doughManagerAgent, bakingInterfaceAgent));
            }
            else{
            	
                addBehaviour(new sendOrder(orderString, doughManagerAgent));
            }


            // System.out.println("Order will be sent to: " + doughManagerAgent + "and" + bakingInterfaceAgent);

            
            //System.out.println("Order: " + orderString);

        }
    }



// Send a kneadingNotification msg to the doughManager agents
    private class sendOrder extends Behaviour {
        private MessageTemplate mt;
        private int option = 0;
        private int repliesCnt = 0;
        private Gson gson = new Gson();
        private String orderString;
        private AID doughManagerAgent = null;
        private AID bakingInterfaceAgent = null;

        private sendOrder(String orderString, AID doughManagerAgent, AID bakingInterfaceAgent){
            this.orderString = orderString;
            this.doughManagerAgent = doughManagerAgent;
            this.bakingInterfaceAgent = bakingInterfaceAgent;
        }

        private sendOrder(String orderString, AID doughManagerAgent){
            this.orderString = orderString;
            this.doughManagerAgent = doughManagerAgent;
        }

        public void action() {
            messageProcessing.incrementAndGet();
            switch (option) {

                case 0:
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

                    msg.setContent(orderString);
                    msg.setConversationId("sending-Order");
                    msg.addReceiver(doughManagerAgent);

                    if (bakingInterfaceAgent != null){

                        msg.addReceiver(bakingInterfaceAgent);
                    }

                    // System.out.println("================================================================================");
                    // System.out.println("Sending order to: " + doughManagerAgent + "and" + bakingInterfaceAgent);
                    // System.out.println("================================================================================");

                    baseAgent.sendMessage(msg);

                    messageProcessing.decrementAndGet();
                    option = 1;
                    break;

                case 1:
                    mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                        MessageTemplate.MatchConversationId("reply-Order"));

                    ACLMessage reply = baseAgent.receive(mt);

                    if (reply != null) {
                        repliesCnt++;

                        // System.out.println("******" + getAID().getLocalName() + "Received confirmation from " + reply.getSender());

                        // We expect a reply from the doughManagerAgent and from the bakingInterfaceAgent
                        if (repliesCnt >= 2){
                            option = 2;
                        }
                        messageProcessing.decrementAndGet();
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
