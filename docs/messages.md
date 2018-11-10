# Messages between Customer and OrderProcessor Agents

## Messages:

### AskForPrice
- performative: CFP
- Sender: Customer
- Receiver: All OrderProcessor
- Content: Order
- ReplyWith: Current Time

### AskForPriceProposal
- performative: PROPOSE
- Sender: OrderProcessor
- Receiver: Customer
- Content: float
- ReplyWith: Value received in AskForPrice

### AskForPriceRefuse
- performative: REFUSE
- Sender: OrderProcessor
- Receiver: Customer
- Content: null
- ReplyWith: Value received in AskForPrice

### PlaceOrder
- performative: ACCEPT_PROPOSAL
- Sender: Customer
- Receiver: OrderProcessor with best proposal
- Content: Order
- ReplyWith: Order guid

### RefuseOrder
- performative: REFUSE 
- Sender: Order processor
- Receiver: Customer
- Content: null
- ReplyWith: Order guid

## Objects:

### Order
- guid
- customerId
- orderDate
- deliveryDate
- products
