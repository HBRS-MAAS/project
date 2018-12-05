# CUSTOMER MESSAGES

3 message exchange between CustomerAgent and OrderProcessingAgent


## OUT MESSAGES

Message 1
- **performative**: REQUEST
- **sender**: customer agent
- **receiver**: order processing agent
- **conversation-id**: "customer-order"
- **content**:
{"order_date":{"hour":4,"day":1},"delivery_date":{"hour":21,"day":2},"guid":"order-001","location":null,"customer_id":"customer-001","products":{"Donut":0,"Bagel":4,"Berliner":2,"Muffin":8,"Bread":2}}

Message 2
- **performative**: CONFIRM
- **sender**: customer agent
- **receiver**: order processing agent
- **conversation-id**: "customer-order"
- **content**:
"Bread, Muffin, Donut"


## IN MESSAGES
- **performative**: PROPOSE
- **sender**: order processing agent
- **receiver**: customer agent
- **content**:
{"Sunspear Bakery":{"Donut":"4.54","Bagel":"3.21","Berliner":"2.94","Muffin":"4.43","Bread":"4.7"}}




