# Proofer messages

## Input Message: Proofing Request

**Peformative**: CFP

**Sender**: AID of a Dough Manager Agent, which provides the service "DoughManager"

Example: "DoughManager_" + bakeryId

**Receiver**: AID of a Proofer Agent, which provides the service "Proofer"

Example: "Proofer_" + bakeryId

**PostTimeStamp**: Current system time

**ConversationId**: 'proofing-Request'

**MessageContent**:


#### Content

```
{
    "proofingTime": float,
    "productQuantities": Vector <Integer>
    "guids": Vector<String>,
    "productType": String,
}

```

#### Example Content


```
{
   "proofingTime":6.0,
   "productQuantities":[
      1,
      2
   ],
   "guids":[
      "order-001",
      "order-002"
   ],
   "productType":"Bagel"
}

```

#### Example of a behaviour to send CFP to the Proofer:

```
private class RequestProofing extends Behaviour{
        private String proofingRequest;
        private boolean proposalReceived = false;
        private MessageTemplate mt;
        private int option = 0;

        public RequestProofing(String proofingRequest){
            this.proofingRequest = proofingRequest;
        }

        public void action(){

            switch(option){
                case 0:

                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                    cfp.addReceiver(prooferAgent);
                    cfp.setContent(proofingRequest);
                    cfp.setConversationId("proofing-Request");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis());

                    baseAgent.sendMessage(cfp);

                    // Template to get proposals/refusals
                    mt = MessageTemplate.and(
                         MessageTemplate.MatchConversationId("proofing-Request"),
                         MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));

                    option = 1;
                    break;

                case 1:

                    ACLMessage reply = baseAgent.receive(mt);
                    if (reply != null) {

                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            proposalReceived = true;
                        }

                        option = 2;
                        messageProcessing.decrementAndGet();
                    }

                    else {
                        block();
                    }
                    break;

                case 2:

                	ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                    if (proposalReceived) {

                        msg.addReceiver(prooferAgent);
                        msg.setContent(proofingRequest);
                        msg.setConversationId("proofing-Request");
                        msg.setReplyWith(proofingRequest + System.currentTimeMillis());
                        baseAgent.sendMessage(msg);

                    }

                    // Prepare the template to get the msg reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("proofing-Request"),
                    MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                    option = 3;

                    break;

                case 3:

                    reply = baseAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // The proofer confirmed the ACCEPT_PROPOSAL

                        }else {
                            // The proofer rejected the ACCEPT_PROPOSAL because it is currently busy. Try again.

                                addBehaviour(new RequestProofing(proofingRequest));
                            }
                            option = 4;
                        }
                        else {
                            block();
                        }
                        break;

                default:
                    break;
            }
        }
        public boolean done(){
            if (option == 2 && !proposalReceived) {
                // The proofer did not send a PROPOSE because it is currently busy. Try again.
                addBehaviour(new RequestProofing(proofingRequest, batch));
            }
            return ((option == 2 && !proposalReceived) || option == 4);

        }
    }
```

## Output Message

**Peformative**: INFORM

**Sender**: AID of a Proofer Agent, which provides the service "Proofer"

Example: "Proofer_" + bakeryId

**Receiver**: AID of an agent in the Baking Stage which provides the service "Baking-interface"

Example: "BakingInterface_" + bakeryId

**PostTimeStamp**: Current system time

**ConversationId**: 'dough-Notification'

**MessageContent**:


#### Content

```
{
    "productQuantities": Vector <int>
    "guids": Vector<String>,
    "productType": String,
}
```

#### Example Content

```
{
   "productQuantities":[
      1,
      2
   ],
   "guids":[
      "order-001",
      "order-002"
   ],
   "productType":"Bagel"
}

```
