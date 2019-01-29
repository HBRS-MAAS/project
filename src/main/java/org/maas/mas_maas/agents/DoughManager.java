package org.mas_maas.agents;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import org.maas.JSONConverter;
import org.maas.messages.KneadingNotification;
import org.maas.messages.KneadingRequest;
import org.maas.messages.PreparationNotification;
import org.maas.messages.PreparationRequest;
import org.maas.messages.ProofingRequest;
import org.maas.utils.Time;
import org.maas.Objects.BakedGood;
import org.maas.Objects.Bakery;
import org.maas.Objects.Client;
import org.maas.Objects.DoughPrepTable;
import org.maas.Objects.Equipment;
import org.maas.Objects.KneadingMachine;
import org.maas.Objects.OrderMas;
import org.maas.Objects.ProductMas;
import org.maas.Objects.ProductStatus;
import org.maas.Objects.Step;
import org.maas.Objects.WorkQueue;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.*;


import org.maas.agents.BaseAgent;

public class DoughManager extends BaseAgent {
    private AID dummyOrderProcesser;
    private AID prooferAgent;

    private ArrayList<AID> preparationTableAgents = new ArrayList<AID>();
    private ArrayList<AID> kneadingMachineAgents = new ArrayList<AID>();

    //private AID dummyOrderProcesser = new AID("dummyOrderProcesser", AID.ISLOCALNAME);
    private String scenarioPath;
    private AgentContainer container = null;

    private Bakery bakery;
    private String bakeryId;

    private String doughManagerAgentName; // Name used to register in the yellowpages
    private WorkQueue needsKneading = new WorkQueue();
    private WorkQueue needsPreparation = new WorkQueue();
    private WorkQueue needsProofing = new WorkQueue();

    private HashMap<String, OrderMas> orders = new HashMap<String, OrderMas>();

    private static final String NEEDS_KNEADING = "needsKneading";
    private static final String NEEDS_PREPARATION = "needsPreparation";
    private static final String NEEDS_PROOFING = "needsProofing";

	private Vector<Equipment> equipment;
    // private Vector<String> kneadingMachineNames = new Vector<String>();
    private Vector<String> doughPrepTableNames = new Vector<String>();

    private AtomicInteger messageProcessing = new AtomicInteger(0);

    private AtomicBoolean isInProductionTime = new AtomicBoolean (false);

    protected void setup() {
        super.setup();

        Object[] args = getArguments();
		if (args != null && args.length > 0) {
            this.scenarioPath = (String) args[0];
            this.bakeryId = (String) args[1];
		}

        //Get the container of this agent
        container = (AgentContainer)getContainerController();
        //System.out.println("-------> Container Dough" + container);
        doughManagerAgentName = "DoughManager_" + bakeryId;

        // Register the Dough-manager in the yellow pages
        this.register(doughManagerAgentName, "JADE-bakery");
        System.out.println("Hello! " + getAID().getLocalName() + " is ready.");

        //Read the scenario file and get the bakery with this.bakeryId
        getBakery(scenarioPath);
        //System.out.println("Bakery " + bakeryId + " is " + bakery.getGuid());

        // Get equipment for this bakery
        equipment = bakery.getEquipment();
        // Create an agent for each equipment
        createEquipmentAgents();

        getDummyOrderProcesserAID();
        getProoferAID();

        addBehaviour(new timeTracker());
        addBehaviour(new ReceiveOrders());
        addBehaviour(new ReceiveKneadingNotification());
        addBehaviour(new ReceivePreparationNotification());

        addBehaviour(new checkingKneadingWorkqueue());
        addBehaviour(new checkingPreparationWorkqueue());
        addBehaviour(new checkingProofingWorkqueue());

    }

    private class timeTracker extends CyclicBehaviour {
        public void action() {
            if (!baseAgent.getAllowAction()) {
                return;
            }

            // only advance if we aren't currently processing any messages
            if (messageProcessing.get() <= 0)
            {
                //System.out.println("Current time: " + baseAgent.getCurrentTime());

                // Production time is from midnight to lunch (from 00.00 hrs to 12 hrs)
                if ((baseAgent.getCurrentTime().greaterThan(new Time(baseAgent.getCurrentDay(), 0, 0)) ||

                        baseAgent.getCurrentTime().equals(new Time(baseAgent.getCurrentDay(), 0, 0))) &&

                        baseAgent.getCurrentTime().lessThan(new Time(baseAgent.getCurrentDay(), 12, 0)))
                {

                    isInProductionTime.set(true);
                    //System.out.println("Setting to true");

                }
                else{

                    isInProductionTime.set(false);
                    System.out.println( "\t" + bakeryId + " Out of production hours");
                    //System.out.println("Setting to false");
                }

                baseAgent.finished();

            }
        }
    }

    protected void takeDown() {
        System.out.println(getAID().getLocalName() + ": Terminating.");
        this.deRegister();
    }

    public void getBakery(String scenarioPath){
        String jsonDir = scenarioPath;
        try {
            // System.out.println("Working Directory = " + System.getProperty("user.dir"));
            String bakeryFile = new Scanner(new File(jsonDir + "bakeries.json")).useDelimiter("\\Z").next();
            Vector<Bakery> bakeries = JSONConverter.parseBakeries(bakeryFile);

            for (Bakery bakery : bakeries){
                if (bakery.getGuid().equals(bakeryId)){
                    this.bakery = bakery;
                }
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void createEquipmentAgents() {

        for (int i = 0; i < equipment.size(); i++){

            // Create KneadingMachineAgents agents for this bakery
            if (equipment.get(i) instanceof KneadingMachine){

                // Object of type KneadingMachine
                KneadingMachine kneadingMachine = (KneadingMachine) equipment.get(i);
                // Name of the kneadingMachineAgent
                String kneadingMachineAgentName = "KneadingMachineAgent_" +  bakeryId + "_" + kneadingMachine.getGuid();

                // kneadingMachineNames.add(kneadingMachineAgentName);
                kneadingMachineAgents.add(new AID (kneadingMachineAgentName, AID.ISLOCALNAME));

                try {
                    Object[] args = new Object[3];
                    args[0] = kneadingMachine;
                    args[1] = kneadingMachineAgentName;
                    args[2] = doughManagerAgentName;

                    AgentController kneadingMachineAgent = container.createNewAgent(kneadingMachineAgentName, "org.mas_maas.agents.KneadingMachineAgent", args);
                    kneadingMachineAgent.start();

                } catch (Exception any) {
                    any.printStackTrace();
                }
            }

            //Create DougPrepTable agents for this bakery
            if (equipment.get(i) instanceof DoughPrepTable){

                //Object of type DoughPrepTable
                DoughPrepTable doughPrepTable = (DoughPrepTable) equipment.get(i);

                //Name of preparationTableAgent
                String doughPrepTableAgentName = "DoughPrepTableAgent_" +  bakeryId + "_" + doughPrepTable.getGuid();

                doughPrepTableNames.add(doughPrepTableAgentName);
                preparationTableAgents.add(new AID(doughPrepTableAgentName, AID.ISLOCALNAME));

                try {
                    Object[] args = new Object[3];
                     args[0] = doughPrepTable;
                     args[1] = doughPrepTableAgentName;
                     args[2] = doughManagerAgentName;

                    AgentController preparationTableAgent = container.createNewAgent(doughPrepTableAgentName, "org.mas_maas.agents.PreparationTableAgent", args);
                    preparationTableAgent.start();

                } catch (Exception any) {
                    any.printStackTrace();
                }
            }

        }

    }

    public void getDummyOrderProcesserAID() {
        String dummyOrderProcesserName = "DummyOrderProcesser";
        dummyOrderProcesser = new AID(dummyOrderProcesserName, AID.ISLOCALNAME);
    }

    public void getProoferAID() {
        String prooferAgentName = "Proofer_" + bakeryId;
        prooferAgent = new AID(prooferAgentName, AID.ISLOCALNAME);
    }

    // Behaviour that checks the needsKneading workqueue and activates CFP for requesting kneading
    private class checkingKneadingWorkqueue extends CyclicBehaviour{
        public void action(){
            messageProcessing.incrementAndGet();

            if (needsKneading.hasProducts() && isInProductionTime.get()){

                // Creates a kneadingRequestMessage for the first product in the workqueue
                KneadingRequest kneadingRequestMessage = createKneadingRequestMessage();

                String productType = kneadingRequestMessage.getProductType();

                //Batch of ProductStatuses used for creating the kneadingRequest
                Vector <ProductStatus> batch = needsKneading.findProductStatus(productType);

                //Remove the product from the needsKneading workQueue
                needsKneading.removeProductStatus(productType);

                // Convert the kneadingRequest object to a String.
                Gson gson = new Gson();
                String kneadingRequestString = gson.toJson(kneadingRequestMessage);

                // System.out.println("KneadingRequest: " + kneadingRequestString);

                // Add behavior to send a CFP for this kneadingRequest
                addBehaviour(new RequestKneading(kneadingRequestString, batch));
            }
            messageProcessing.decrementAndGet();

        }
    }

    // Behaviour that checks the needsKneading workqueue and activates CFP for requesting kneading
    private class checkingPreparationWorkqueue extends CyclicBehaviour{
        public void action(){
            messageProcessing.incrementAndGet();

            if (needsPreparation.hasProducts() && isInProductionTime.get()){

                // Creates a preparationRequestMessage for the first product in the workqueue
                PreparationRequest preparationRequestMessage = createPreparationRequestMessage();

                //System.out.println("preparationRequestMessage" + preparationRequestMessage);

                String productType = preparationRequestMessage.getProductType();

                //Batch of ProductStatuses used for creating the preparationRequest
                Vector <ProductStatus> batch = needsPreparation.findProductStatus(productType);

                //Remove the product from the needsKneading workQueue
                needsPreparation.removeProductStatus(productType);

                // Convert the kneadingRequest object to a String.
                Gson gson = new Gson();
                String preparationRequestString = gson.toJson(preparationRequestMessage);

                // System.out.println("preparationRequest: " + preparationRequestString);

                // Add behavior to send a CFP for this preparationRequest
                addBehaviour(new RequestPreparation(preparationRequestString, batch));
            }
            messageProcessing.decrementAndGet();

        }
    }

    // Behaviour that checks the needsProofing workqueue and activates CFP for requesting kneading
    private class checkingProofingWorkqueue extends CyclicBehaviour{
        public void action(){
            messageProcessing.incrementAndGet();

            if (needsProofing.hasProducts() && isInProductionTime.get()){
                // Creates a proofingRequestMessage for the first product in the workqueue
                ProofingRequest proofingRequestMessage = createProofingRequestMessage();

                String productType = proofingRequestMessage.getProductType();

                //Batch of ProductStatuses used for creating the preparationRequest
                Vector <ProductStatus> batch = needsProofing.findProductStatus(productType);

                //Remove the product from the needsKneading workQueue
                needsProofing.removeProductStatus(productType);

                // Convert the kneadingRequest object to a String.
                Gson gson = new Gson();
                String proofingRequestString = gson.toJson(proofingRequestMessage);

                //System.out.println("preparationRequest: " + preparationRequestString);

                // Add behavior to send a CFP for this preparationRequest
                addBehaviour(new RequestProofing(proofingRequestString, batch));
            }
            messageProcessing.decrementAndGet();

        }
    }

    public void queueOrder(OrderMas order) {
        // Add productStatus to the needsKneading WorkQueue

        for(BakedGood bakedGood : order.getBakedGoods()) {

            int amount = bakedGood.getAmount();
            if (amount > 0){
                String guid = order.getGuid();
                String status = NEEDS_KNEADING;
                ProductMas product = bakery.findProduct(bakedGood.getName());
                ProductStatus productStatus = new ProductStatus(guid, status, amount, product);

                needsKneading.addProduct(productStatus);

                // KneadingRequest kneadingRequestMessage = createKneadingRequestMessage();
                //
                // String productType = kneadingRequestMessage.getProductType();
                //
			    // // Convert the kneadingRequest object to a String.
                // Gson gson = new Gson();
                // String kneadingRequestString = gson.toJson(kneadingRequestMessage);

            }
        }
    }

    public KneadingRequest createKneadingRequestMessage() {
        // Checks the needsKneading workqueue and creates a KneadingRequestMessage

        Vector<ProductStatus> products = needsKneading.getProductBatch();
        KneadingRequest kneadingRequest = null;

        if (products != null) {

            Vector<String> guids = new Vector<String>();

            for (ProductStatus productStatus : products) {
                guids.add(productStatus.getGuid());

            }
            String productType = products.get(0).getProduct().getGuid();
            float kneadingTime = products.get(0).getProduct().getRecipe().getActionTime(Step.KNEADING_STEP);

            kneadingRequest = new KneadingRequest(guids, productType, kneadingTime);
        }

        return kneadingRequest;
    }

    public void queuePreparation(String productType, Vector<String> guids ) {
        // Add productStatus to the needsPreparation WorkQueue

        for (String guid : guids) {

            int amount = -1;
            String status = NEEDS_PREPARATION;
            ProductMas product = bakery.findProduct(productType);
            OrderMas order = orders.get(guid);

            for(BakedGood bakedGood : order.getBakedGoods()) {
                if (bakedGood.getName().equals(productType)) {
                    amount = bakedGood.getAmount();
                }

            }
            ProductStatus productStatus = new ProductStatus(guid, status, amount, product);
            needsPreparation.addProduct(productStatus);
        }
    }

    public PreparationRequest createPreparationRequestMessage() {
        // Checks the needsPreparaion WorkQueue and creates a preparationRequestMessage
        Vector<ProductStatus> products = needsPreparation.getProductBatch();

        PreparationRequest preparationRequest = null;

        if (products != null) {

            Vector<String> guids = new Vector<String>();
            Vector<Integer> productQuantities = new Vector<Integer>();
            Vector<Step> steps = new Vector<Step>();



            for (ProductStatus productStatus : products) {
                guids.add(productStatus.getGuid());
                productQuantities.add(productStatus.getAmount());
            }

            String productType = products.get(0).getProduct().getGuid();
            steps = products.get(0).getProduct().getRecipe().getPreparationSteps();

            preparationRequest = new PreparationRequest(guids, productType, productQuantities, steps);
        }

        return preparationRequest;

    }

    public void queueProofing(String productType, Vector<String> guids ) {
        // Add productStatus to the needsProofing WorkQueue

        for (String guid : guids) {

            int amount = -1;
            String status = NEEDS_PROOFING;
            ProductMas product = bakery.findProduct(productType);
            OrderMas order = orders.get(guid);

            for(BakedGood bakedGood : order.getBakedGoods()) {
                if (bakedGood.getName().equals(productType)) {
                    amount = bakedGood.getAmount();
                }

            }
            ProductStatus productStatus = new ProductStatus(guid, status, amount, product);
            needsProofing.addProduct(productStatus);
        }
    }

    public ProofingRequest createProofingRequestMessage() {
        // Checks the needsProofing WorkQueue and creates a proofingRequestMessage
        Vector<ProductStatus> products = needsProofing.getProductBatch();

        ProofingRequest proofingRequest = null;

        if (products != null) {

            Vector<String> guids = new Vector<String>();
            Vector<Integer> productQuantities = new Vector<Integer>();

            for (ProductStatus productStatus : products) {
                guids.add(productStatus.getGuid());
                productQuantities.add(productStatus.getAmount());
            }

            String productType = products.get(0).getProduct().getGuid();

            float proofingTime = products.get(0).getProduct().getRecipe().getActionTime(Step.PROOFING_STEP);

            proofingRequest = new ProofingRequest(productType, guids, proofingTime, productQuantities);
        }

        return proofingRequest;

    }

    /* This is the behavior used for receiving orders */
    private class ReceiveOrders extends CyclicBehaviour {
        public void action() {

            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(dummyOrderProcesser));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                // System.out.println(getAID().getLocalName() + " received order " + content +
                //                     "\n \t from " + msg.getSender().getName());
                OrderMas order = JSONConverter.parseOrder(content);

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.setContent("Order was received");
                reply.setConversationId("reply-Order");
                baseAgent.sendMessage(reply);

                orders.put(order.getGuid(), order);
                queueOrder(order);

                messageProcessing.decrementAndGet();
            }
            else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    /* This is the behavior used for receiving kneading notification messages */
    private class ReceiveKneadingNotification extends CyclicBehaviour {
        public void action() {

            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("kneading-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                // System.out.println("================================================================================");
                // System.out.println(getAID().getLocalName()+" Received Kneading Notification from " + msg.getSender()
                // + " for: " + msg.getContent());
                // System.out.println("================================================================================");
                String kneadingNotificationString = msg.getContent();

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.setContent("Kneading Notification was received");
                reply.setConversationId("kneading-notification-reply");
                baseAgent.sendMessage(reply);

                // Convert kneadingNotificationString to kneadingNotification object
                KneadingNotification kneadingNotification = JSONConverter.parseKneadingNotification(kneadingNotificationString);
                String productType = kneadingNotification.getProductType();
                Vector<String> guids = kneadingNotification.getGuids();

                // Add guids with this productType to the queuePreparation
                queuePreparation(productType, guids);
                messageProcessing.decrementAndGet();
            }
            else {
                messageProcessing.decrementAndGet();
                block();
            }
        }
    }

    /* This is the behaviour used for receiving preparation notification */
    private class ReceivePreparationNotification extends CyclicBehaviour {
        public void action() {

            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId("preparation-notification"));
            ACLMessage msg = baseAgent.receive(mt);

            if (msg != null) {
                // System.out.println("======================================");
                // System.out.println(getAID().getLocalName()+" Received Preparation Notification from " + msg.getSender() + " " + msg.getContent());
                // System.out.println("======================================");
                String preparationNotificationString = msg.getContent();

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.CONFIRM);
                reply.setContent("Preparation Notification was received");
                reply.setConversationId("preparation-notification-reply");
                baseAgent.sendMessage(reply);

                // Convert preparationNotificationString to preparationNotification object
                PreparationNotification preparationNotification = JSONConverter.parsePreparationNotification(preparationNotificationString);
                String productType = preparationNotification.getProductType();
                Vector<String> guids = preparationNotification.getGuids();

                // Add guids with this productType to the queueProofing
                queueProofing(productType, guids);
                messageProcessing.decrementAndGet();
            }
            else {
                block();
                messageProcessing.decrementAndGet();
            }
        }


    }

    //This is the behaviour used for sensing a KneadingRequest
    private class RequestKneading extends Behaviour{
        private String kneadingRequest;
        private Vector <ProductStatus> batch;
        private MessageTemplate mt;
        private ArrayList<AID> kneadingMachinesAvailable;
        private AID kneadingMachine; // The kneadingMachineAgent that will perform kneading
        private int repliesCnt = 0;
        private int option = 0;

        public RequestKneading(String kneadingRequest, Vector <ProductStatus> batch){
            this.kneadingRequest = kneadingRequest;
            // Batch of products used for creating the kneadingRequest
            this.batch = batch;
        }

        public void action(){
            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();
            switch(option){
                case 0:
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                    kneadingMachinesAvailable = new ArrayList<AID>();
                    // Send kneadingRequest msg to all kneadingMachineAgents
                    for (int i=0; i<kneadingMachineAgents.size(); i++){
                        cfp.addReceiver(kneadingMachineAgents.get(i));
                    }

                    cfp.setContent(kneadingRequest);
                    cfp.setConversationId("kneading-request");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis());

                    baseAgent.sendMessage(cfp);

                    // Template to get proposals/refusals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("kneading-request"),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    messageProcessing.decrementAndGet();
                    option = 1;
                    break;

                case 1:
                    // Receive proposals/refusals
                    ACLMessage reply = baseAgent.receive(mt);
                    if (reply != null) {
                        repliesCnt++;

                        // The kneadingMachine that replies first gets the job
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            kneadingMachinesAvailable.add(reply.getSender());

                            // System.out.println(getAID().getLocalName() +
                            // " received a proposal from " + reply.getSender().getName()
                            // + " for: " + kneadingRequest);
                        }
                        // All kneadingMachines replied
                        if (repliesCnt >= kneadingMachineAgents.size()) {
                            if (!kneadingMachinesAvailable.isEmpty()){
                                kneadingMachine = kneadingMachinesAvailable.get(0);
                                kneadingMachinesAvailable.remove(0);
                            }

                            option = 2;

                        }
                        messageProcessing.decrementAndGet();
                    }

                    else {
                        messageProcessing.decrementAndGet();
                        block();
                    }
                    break;

                case 2:
                    // Accept proposal from the kneading machine that replied first
                    ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                    msg.addReceiver(kneadingMachine);

                    // System.out.println(">>>>> " + getAID().getLocalName() + " Accepting proposal from "
                    //     + kneadingMachine.getName() + " for: " + kneadingRequest);

                    msg.setContent(kneadingRequest);
                    msg.setConversationId("kneading-request");
                    msg.setReplyWith(kneadingRequest + System.currentTimeMillis());
                    baseAgent.sendMessage(msg);

                    // Prepare the template to get the msg reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("kneading-request"),
                    MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                    option = 3;
                    messageProcessing.decrementAndGet();
                    break;

                case 3:
                    // Receive the confirmation from the kneadingMachine
                    reply = baseAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // System.out.println(getAID().getLocalName()+ " confirmation received from -> \n \t"
                            //     + reply.getSender().getLocalName() + " for: " + reply.getContent());
                        }
                        else {
                            // System.out.println(getAID().getLocalName() + " rejection received from -> "
                            //     + reply.getSender().getLocalName() + " for: " + kneadingRequest + "Adding request to the needsKneading queue");

                            //Add the batch to the needsKneading queue
                            for (ProductStatus productStatus : batch){
                                needsKneading.addProduct(productStatus);
                            }
                        }
                        option = 4;
                        messageProcessing.decrementAndGet();
                    }
                    else {
                        messageProcessing.decrementAndGet();
                        block();
                    }
                    break;
                default:
                    messageProcessing.decrementAndGet();
                    break;

            }
        }
        public boolean done(){

            if (option == 2 && kneadingMachine == null) {
                // System.out.println("++++++Attempt failed for " + kneadingRequest + "Adding request to the needsKneading queue");
                //Add the batch to the needsKneading queue
                for (ProductStatus productStatus : batch){
                    needsKneading.addProduct(productStatus);
                }
            }
            return ((option == 2 && kneadingMachine == null) || option == 4);
        }
    }

    //This is the behaviour used for sending a PreparationRequest
    private class RequestPreparation extends Behaviour{
        private String preparationRequest;
        private Vector <ProductStatus> batch;
        private MessageTemplate mt;
        private ArrayList<AID> preparationTablesAvailable;
        private AID preparationTable; // The preparationTable that will perform the preparation steps
        private int repliesCnt = 0;
        private int option = 0;

        public RequestPreparation(String preparationRequest, Vector <ProductStatus> batch){
            this.preparationRequest = preparationRequest;
            // Batch of products used for creating the kneadingRequest
            this.batch = batch;
        }
        public void action(){
            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();

            switch(option){
                case 0:
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                    preparationTablesAvailable = new ArrayList<AID>();
                    // Send preparation requests msg to all preparationTableAgents
                    for (int i=0; i<preparationTableAgents.size(); i++){
                        cfp.addReceiver(preparationTableAgents.get(i));
                    }

                    cfp.setContent(preparationRequest);
                    cfp.setConversationId("preparation-request");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis());

                    // System.out.println("======================================");
                    // System.out.println("CFP for: " + preparationRequest);
                    // System.out.println("======================================");

                    baseAgent.sendMessage(cfp);

                    // Template to get proposals/refusals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("preparation-request"),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    messageProcessing.decrementAndGet();
                    option = 1;
                    break;

                case 1:
                    // Receive proposals/refusals
                    ACLMessage reply = baseAgent.receive(mt);
                    if (reply != null) {
                        repliesCnt++;
                        // The doughPrepTable that replies first gets the job
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            preparationTablesAvailable.add(reply.getSender());
                            // System.out.println(getAID().getLocalName() + " received a proposal from " + reply.getSender().getName() + " for: " + preparationRequest);
                        }
                        // We received all replies
                        if (repliesCnt >= preparationTableAgents.size()) {

                            if (!preparationTablesAvailable.isEmpty()){
                                preparationTable = preparationTablesAvailable.get(0);
                                preparationTablesAvailable.remove(0);
                            }
                            option = 2;
                        }
                        messageProcessing.decrementAndGet();
                    }

                    else {
                        messageProcessing.decrementAndGet();
                        block();
                    }
                    break;

            case 2:
                    ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                    msg.addReceiver(preparationTable);

                    // System.out.println(getAID().getLocalName() + " Accepting proposal from "
                    //     + preparationTable.getName() + " for: " + preparationRequest);

                    msg.setContent(preparationRequest);
                    msg.setConversationId("preparation-request");
                    msg.setReplyWith(preparationRequest + System.currentTimeMillis());
                    baseAgent.sendMessage(msg);

                    // Prepare the template to get the msg reply
                    mt = MessageTemplate.and(
                         MessageTemplate.MatchConversationId("preparation-request"),
                         MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                    option = 3;
                    messageProcessing.decrementAndGet();
                    break;

            case 3:
                // Receive the confirmation from the preparationTable
                reply = baseAgent.receive(mt);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        // System.out.println(getAID().getLocalName()+ " confirmation received from -> \n \t"
                        //     +reply.getSender().getLocalName() + " for: " + reply.getContent());
                        }
                        else{
                            // System.out.println(getAID().getLocalName() + " rejection received from -> "
                            //     +reply.getSender().getLocalName() + " for: " + preparationRequest);

                            //Add the batch to the needsPreparation queue
                            for (ProductStatus productStatus : batch){
                                needsPreparation.addProduct(productStatus);
                            }
                        }
                        option = 4;
                        messageProcessing.decrementAndGet();
                }
                else {
                    messageProcessing.decrementAndGet();
                    block();
                }
                break;

            default:
                messageProcessing.decrementAndGet();
                break;
            }
        }
        public boolean done(){
            if (option == 2 && preparationTable == null) {
                // System.out.println("++++++Attempt failed for " + preparationRequest + "Adding request to the needsPreparation queue");
                //Add the batch to the needsKneading queue
                for (ProductStatus productStatus : batch){
                    needsPreparation.addProduct(productStatus);
                }
            }
            return ((option == 2 && preparationTable == null) || option == 4);
        }
    }

    // This is the behavior used for sensing a ProofingRequest
    private class RequestProofing extends Behaviour{
        private String proofingRequest;
        private Vector <ProductStatus> batch;
        private AID proofer;
        private MessageTemplate mt;
        private int option = 0;

        public RequestProofing(String proofingRequest, Vector <ProductStatus> batch){
            this.proofingRequest = proofingRequest;
            // Batch of products used for creating the kneadingRequest
            this.batch = batch;
        }

        public void action(){
            // insure we don't allow a time step until we are done processing this message
            messageProcessing.incrementAndGet();

            switch(option){
                case 0:
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                    cfp.addReceiver(prooferAgent);

                    cfp.setContent(proofingRequest);
                    cfp.setConversationId("proofing-Request");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis());
                    // System.out.println("***************************************");
                    // System.out.println("CFP for: " + proofingRequest);
                    // System.out.println("***************************************");
                    baseAgent.sendMessage(cfp);

                    // Template to get proposals/refusals
                    mt = MessageTemplate.and(
                         MessageTemplate.MatchConversationId("proofing-Request"),
                         MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    messageProcessing.decrementAndGet();
                    option = 1;
                    break;

                case 1:
                    ACLMessage reply = baseAgent.receive(mt);
                    if (reply != null) {
                        // The Proofer that replies first gets the job
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // System.out.println(getAID().getLocalName() + " received a proposal from " +
                            //     reply.getSender().getName() + " for: " + proofingRequest);


                        // }else if (reply.getPerformative() == ACLMessage.FAILURE){
                        //     option = 0;
                        //
                        // }

                            proofer = reply.getSender();

                        }
                        option = 2;
                        messageProcessing.decrementAndGet();
                    }

                    else {
                        messageProcessing.decrementAndGet();
                        block();
                    }
                    break;

                case 2:
                    // Accept proposal from the kneading machine that replied first
                    ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    msg.addReceiver(proofer);
                    msg.setContent(proofingRequest);
                    msg.setConversationId("proofing-Request");
                    msg.setReplyWith(proofingRequest + System.currentTimeMillis());
                    baseAgent.sendMessage(msg);

                    // System.out.println(getAID().getLocalName() + " Accepting proposal from "
                    //     + prooferAgent.getName() + " for: " + proofingRequest);

                    // Prepare the template to get the msg reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("proofing-Request"),
                    MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                    option = 3;
                    messageProcessing.decrementAndGet();

                    break;

                case 3:
                    // Receive the confirmation for the proofer
                    reply = baseAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // System.out.println(getAID().getLocalName()+ " confirmation received from -> \n \t"
                            //     +reply.getSender().getLocalName() + " for: " + reply.getContent());

                        }else {
                            // System.out.println(getAID().getLocalName() + " rejection received from -> "
                            //     +reply.getSender().getLocalName() + " for: " + proofingRequest + "Adding it back to the queue");

                                //Add the batch to the needsProofing queue
                                for (ProductStatus productStatus : batch){
                                    needsProofing.addProduct(productStatus);
                                }
                            }
                            option = 4;
                            messageProcessing.decrementAndGet();
                        }
                        else {
                            messageProcessing.decrementAndGet();
                            block();
                        }
                        break;

                default:
                    messageProcessing.decrementAndGet();
                    break;
            }
        }
        public boolean done(){
            if (option == 2 && proofer == null) {
                // System.out.println("++++++Attempt failed for " + proofingRequest + "Adding request to the needsProofing queue");

                for (ProductStatus productStatus : batch){
                    needsProofing.addProduct(productStatus);
                }
            }
            return ((option == 2 && proofer == null) || option == 4);

        }
    }
}
