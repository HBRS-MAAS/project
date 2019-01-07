# CUSTOMER MESSAGES
Message exchanges between CustomerAgent and OrderProcessingAgent


## OUT MESSAGES
- Objective: to inform order processing agent that the customer doesn't have any order currently
- **performative**: INFORM
- **sender**: customer agent
- **receiver**: all order processing agent
- **content**: <String>

- Objective: to send the order at specific time
- **performative**: CFP
- **sender**: customer agent
- **receiver**: all order processing agent
- **conversation-id**: order.guid
- **content**:
{"order_date":{"hour":4,"day":1},"delivery_date":{"hour":21,"day":2},"guid":"order-001","location":null,"customer_id":"customer-001","products":{"Donut":0,"Bagel":4,"Berliner":2,"Muffin":8,"Bread":2}}

- Objective: to send specific order type to the cheapest bakery
- **performative**: ACCEPT_PROPOSAL
- **sender**: customer agent
- **receiver**: (cheapest) order processing agent
- **content**:
{"order_date":{"hour":4,"day":1},"delivery_date":{"hour":21,"day":2},"guid":"order-001","location":null,"customer_id":"customer-001","products":{"Donut":0,"Muffin":8,"Bread":2}}

- Objective: to tell the expensive bakery, the customer isn't satisfied with its proposal
- **performative**: REJECT_PROPOSAL
- **sender**: customer agent
- **receiver**: (expensive) order processing agent
- **content**: <String>

## IN MESSAGES
- **performative**: PROPOSE
- **sender**: order processing agent
- **receiver**: customer agent
- **content**:
{name: "Sunspear Bakery", products: {"Donut":"4.54","Bagel":"3.21","Berliner":"2.94","Muffin":"4.43","Bread":"4.7"}}

- **performative**: REFUSE
- **sender**: order processing agent
- **receiver**: customer agent
- **content**: <String>




