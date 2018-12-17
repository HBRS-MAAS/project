# Loading Bay messages
## Incoming

### Order Details Message:
The LoadingBayAgent receives order details from the order processor in a message of the following format:

**Performative**: INFORM

**Sender**: AID of the order processor agent

**Receiver**: AID of the LoadingBayAgent

**MessageContent**: 

The message content is a JSON string of the following format (proposed by the team handling the order processing phase): 

```
{
  "customerId" : String,
  "guid" : String,
  "orderDate": {
    "day": Integer,
    "hour": Integer
  }
  "deliveryDate": {
    "day": Integer,
    "hour": Integer
  }
  "products": {
    (type of product 1) : Integer,
    (type of product 2) : Integer,
    (type of product n) : Integer,
  }
}
```

### Example message:

```
{
  "customerId" : customer-001,
  "guid" : order-003,
  "orderDate": {
    "day": 15,
    "hour": 12
  }
  "deliveryDate": {
    "day": 17,
    "hour": 10
  }
  "products": {
    "Bread" : 20,
    "Donut" : 5,
    "Pretzel" : 10,
  }
}
```

### Packaged Boxes Message:
The LoadingBayAgent receives boxes of packaged products from a packaging agent in a message of the following format:

**Performative**: INFORM

**Sender**: AID of the packaging agent

**Receiver**: AID of the LoadingBayAgent

**Conversation ID**: "boxes-ready"

**MessageContent**: 

The message content is a JSON string of the following format: 

```
{
  "OrderID": String,
  "Boxes": [
    {
      "BoxID": String,
      "ProductType": String,
      "Quantity": Integer
    },
    {
      "BoxID": String,
      "ProductType": String,
      "Quantity": Integer
    }
  ]
}
```

### Example message:

```
{
  "OrderID": "order-001",
  "Boxes": [
    {
      "BoxID": "1",
      "ProductType": "Bread",
      "Quantity": 12
    },
    {
      "BoxID": "2",
      "ProductType": "Bread",
      "Quantity": 6
    },
    {
      "BoxID": "3",
      "ProductType": "Donut",
      "Quantity": 5
    }
  ]
}
```


<br>



## Outgoing

### Fulfilled Order Boxes Message:
The LoadingBayAgent agent sends an order aggregator agent a message containing the details of boxes of products belonging to a particular order. It does so once it receives boxes that contain all of the products that fulfill that order. The message is of the following format:

**Performative**: INFORM

**Sender**: AID of the LoadingBayAgent

**Receiver**: AID of an OrderAggregatorAgent

**MessageContent**: 

The message content is a JSON string as described below: 

```
{
  "OrderID": String,
  "Boxes": [
    {
      "BoxID": String,
      "ProductType": String,
      "Quantity": Integer
    },
    {
      "BoxID": String,
      "ProductType": String,
      "Quantity": Integer
    }
  ]
}
```

### Example message:

```
{
  "OrderID": "order-001",
  "Boxes": [
    {
      "BoxID": "1",
      "ProductType": "Bread",
      "Quantity": 12
    },
    {
      "BoxID": "2",
      "ProductType": "Bread",
      "Quantity": 6
    },
    {
      "BoxID": "3",
      "ProductType": "Donut",
      "Quantity": 5
    }
  ]
}
```
<br>
