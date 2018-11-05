# Messages between Customer and OrderProcessor Agents

## Messages:

### AskForPrice
- performative: CFP
- Sender: Customer
- Receiver: All OrderProcessor
- Content: Order
- ReplyWith: Current Time

### AskForPriceProposalResponse
- performative: PROPOSE
- Sender: OrderProcessor
- Receiver: Customer
- Content: float
- ReplyWith: Value received in AskForPrice

### AskForPriceRefuseResponse
- performative: REFUSE
- Sender: OrderProcessor
- Receiver: Customer
- Content: null
- ReplyWith: Value received in AskForPrice

### AcceptProposal
- performative: ACCEPT_PROPOSAL
- Sender: Customer
- Receiver: OrderProcessor with best proposal
- Content: Order
- ReplyWith: Order guid

### RefuseOrderResponse
- performative: REFUSE 
- Sender: Order processor
- Receiver: Customer
- Content: null
- ReplyWith: Order guid

### InformDeliveryResponse
- performative: INFORM
- Sender: DeliverManager
- Receiver: Customer
- Content: Order
- ReplyWith: Order guid


## Objects:

### Order
- guid
- customerId
- orderDate
- deliveryDate
- products