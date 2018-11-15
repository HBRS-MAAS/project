## Input Message

### Order Message

#### ACL Message:
- Performative: INFORM
- Receiver: A Dough Manager Agent, which provides the service "Dough-Manager"
- Content: json String

#### Content

```
{
    "customerId": String,
    "guid": String,
    "orderDate": {
      "day": int,
      "hour": int
    },
    "deliveryDate": {
      "day": int,
      "hour": int
    },
    "products": {
      "Bagel": int,
      "Donut": int,
      "Berliner": int,
      "Muffin": int,
      "Bread": int
}
```

#### Example Content


```
{
    "customerId": "customer-001",
    "guid": "order-331",
    "orderDate": {
      "day": 7,
      "hour": 0
    },
    "deliveryDate": {
      "day": 11,
      "hour": 11
    },
    "products": {
      "Bagel": 7,
      "Donut": 1,
      "Berliner": 5,
      "Muffin": 2,
      "Bread": 4
}
```


## Output Message

#### Dough Notification Message
#### ACL Message:
- Performative: INFORM
- Receiver: A Baking Interface Agent, which provides the service "Baking-interface"
- Content: json String

#### Content

```
{
    "guid": "String"
}
```

#### Example Content

```
{
    "guid": "order-331"
}
```

The Dough Preparation stage is initialized with the information of the types of bread it can prepare dough for (as specified in the products field in the scenario)

It receives Order Messages and takes care of dough preparation steps (kneading, resting, item preparation and proofing).

The Proofer agent sends a Dough Notification Message to the Baking Stage. This message only contains the guid of the order, as the Baking Stage already has the Order information. The Dough Notification Message notifies the Baking stage that the order with guid is ready to be baked.
