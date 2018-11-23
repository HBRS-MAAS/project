package org.maas.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.maas.data.messages.BakedProductMessage;
import org.maas.data.messages.ProductMessage;
import org.maas.utils.JsonConverter;

@SuppressWarnings("serial")
public class CoolingRackAgent extends BaseAgent{
    private AID LOADING_BAY_AGENT = new AID("dummy", AID.ISLOCALNAME);
    private List<BakedProductMessage> bakedProducts;
    
    protected void setup() {
        System.out.println("\tHello! cooling-rack "+getAID().getLocalName()+" is ready.");
        
        this.register("cooling-rack-agent", "JADE-bakery");
        this.bakedProducts = new ArrayList<BakedProductMessage> ();
        addBehaviour(new BakedProdutsServer());
        addBehaviour(new CoolProducts());
    }
    protected void takeDown() {
        this.deRegister();
        System.out.println("\t" + getAID().getLocalName() + ": Terminating.");
    }
    
    private class CoolProducts extends CyclicBehaviour{
        public void action(){
            ArrayList<BakedProductMessage> temp = new ArrayList<BakedProductMessage> ();
            for (BakedProductMessage pm : bakedProducts) {
                //if (this.getCurrentHour() == pm.getProcessStartTime() + pm.getBakingDuration()){
                //if (pm.getCooled()){
                System.out.println("\tBaked " + pm.getGuid() + " at time " + baseAgent.getCurrentHour());
                temp.add(pm);
                //}
                //else {
                    //TODO: start cooling
                    //pm.setIsBaking(true);
                    //pm.setProcessStartTime(this.getCurrentHour());
                //}
            }
            if (temp.size() > 0) {
                Hashtable<String,Integer> outMsg = new Hashtable<String,Integer> ();
                for (BakedProductMessage pm : temp) {
                    outMsg.put(pm.getGuid(), pm.getQuantity());
                }
                ProductMessage p = new ProductMessage();
                p.setProducts(outMsg);
                String messageContent = JsonConverter.getJsonString(p);
                ACLMessage loadingBayMessage = new ACLMessage(ACLMessage.INFORM);
                loadingBayMessage.addReceiver(LOADING_BAY_AGENT);
                loadingBayMessage.setConversationId("baked-products-152");
                loadingBayMessage.setContent(messageContent);
                baseAgent.sendMessage(loadingBayMessage);
            }
            for (BakedProductMessage pm : temp)
                bakedProducts.remove(pm);
        }
    }

    private class BakedProdutsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println(String.format("\tcooling-rack::Received message from oven-manager %s", 
                        msg.getSender().getName()));
                String messageContent = msg.getContent();
                System.out.println(String.format("\tmessage:: %s", messageContent));
                ArrayList<BakedProductMessage> receivedBakedProducts = this.parseBakedProducts(messageContent);
                bakedProducts.addAll(receivedBakedProducts);
            }
            else {
                block();
            }
        }
        private ArrayList<BakedProductMessage> parseBakedProducts(String orderString){
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<?> type = new TypeReference<ArrayList<BakedProductMessage>>(){};
            try {
                ArrayList<BakedProductMessage> data = mapper.readValue(orderString, type);
                return data;
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
