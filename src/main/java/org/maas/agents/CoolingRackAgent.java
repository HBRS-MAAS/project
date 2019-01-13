package org.maas.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.maas.agents.BaseAgent;
import org.maas.objects.ProcessedProduct;
import org.maas.data.messages.ProductMessage;
import org.maas.utils.JsonConverter;
import org.maas.utils.Time;

@SuppressWarnings("serial")
public class CoolingRackAgent extends BaseAgent{
    private AID packagingAgent;
    private List<ProcessedProduct> processedProductList;
    private int cooledProductConversationNumber = 0;
    private String bakeryGuid = "bakery-001";
    private boolean verbose = false;
    
    protected void setup() {
        super.setup();
        System.out.println("\tHello! cooling-rack "+getAID().getLocalName()+" is ready.");
        
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.bakeryGuid = (String) args[0];
        }

        this.packagingAgent = new AID(this.bakeryGuid + "-preLoadingProcessor", AID.ISLOCALNAME);
        AID postBakingProcessor = new AID(this.bakeryGuid + "-postBakingProcessor", AID.ISLOCALNAME);
       
        this.register("cooling-rack-agent", this.bakeryGuid+"-CoolingRackAgent");
        this.processedProductList = new ArrayList<ProcessedProduct> ();

        addBehaviour(new ProcessedProductsServer(postBakingProcessor));
    }
    protected void takeDown() {
        this.deRegister();
        System.out.println("\t" + getAID().getLocalName() + ": Terminating.");
    }
    
    /*
     * Cooling products
     */
    @Override
    protected void stepAction(){
        if (baseAgent.getCurrentTime().lessThan(new Time(baseAgent.getCurrentDay(), 12, 0))){
            ArrayList<ProcessedProduct> message = this.getCooledProducts();
            if (message.size() > 0) {
                this.sendProducts(message);
            }
        }
        baseAgent.finished();
    }
    private ArrayList<ProcessedProduct> getCooledProducts() {
        ArrayList<ProcessedProduct> temp = new ArrayList<ProcessedProduct> ();
        for (ProcessedProduct processedProduct : processedProductList) {
            if (processedProduct.getRemainingTimeDuration() < 0){
                processedProduct.setRemainingTimeDuration(processedProduct.getCoolingDuration());
                this.print("\tStarted cooling " + processedProduct.getQuantity() + " " + processedProduct.getGuid() + " at time " + baseAgent.getCurrentHour());
            }
            if (processedProduct.getRemainingTimeDuration() == 0){
                this.print("\tCooled " + processedProduct.getGuid() + " at time " + baseAgent.getCurrentHour());
                temp.add(processedProduct);
            }
            processedProduct.setRemainingTimeDuration(processedProduct.getRemainingTimeDuration() - 1);
        }
        for (ProcessedProduct pm : temp)
            processedProductList.remove(pm);
        return temp;
    }
    private void sendProducts(ArrayList<ProcessedProduct> temp){
        Hashtable<String,Integer> outMsg = new Hashtable<String,Integer> ();
        for (ProcessedProduct pm : temp) {
            if (outMsg.containsKey(pm.getGuid())){
                outMsg.put(pm.getGuid(), pm.getQuantity() + outMsg.get(pm.getGuid()));
            }
            else {
                outMsg.put(pm.getGuid(), pm.getQuantity());
            }
        }
        ProductMessage p = new ProductMessage();
        p.setProducts(outMsg);
        String messageContent = JsonConverter.getJsonString(p);
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(this.packagingAgent);
        cooledProductConversationNumber ++;
        message.setConversationId(this.bakeryGuid + "-cooled-product-" + Integer.toString(this.cooledProductConversationNumber));
        message.setContent(messageContent);
        baseAgent.sendMessage(message);
    }
    private void print(String str){
        if (this.verbose){
            System.out.println(str);
        }
    }

    /*
     * Server to receive processedProduct from the previous agent in baking stage
     */
    private class ProcessedProductsServer extends CyclicBehaviour {
        private MessageTemplate mt;
        private AID sender;

        public ProcessedProductsServer (AID sender){
            this.sender = sender;
        }
        public void action() {
            this.mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchSender(this.sender));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                print(String.format("\tcooling-rack::Received message from post-baking-processor %s", 
                        msg.getSender().getName()));
                String messageContent = msg.getContent();
                print(String.format("\tmessage:: %s", messageContent));
                TypeReference<?> type = new TypeReference<ArrayList<ProcessedProduct>>(){};
                ArrayList<ProcessedProduct> receivedProcessedProducts = JsonConverter.getInstance(messageContent, type);
                processedProductList.addAll(receivedProcessedProducts);
            }
            else {
                block();
            }
        }
    }
}
