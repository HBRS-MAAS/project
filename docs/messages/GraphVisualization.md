# Graph Visualization Agent messages
## Incoming

### Street Network Details Message:
The Graph Visualization Agent receives the street network information from the Street Network Agent in a message of the following format:

**Performative**: INFORM

**Sender**: AID of the StreetNetworkAgent

**Receiver**: AID of the GraphVisualizationAgent

**Conversation ID**: "graph-visualization"

**MessageContent**: 

The message content is a JSON string of the following format: 

```
{
  "nodes": [
    {
      "guid": String,
      "company": String,
      "location": {
        "x": int,
        "y": int
      },
      "type": String
    },

    ...

  ],
  "edges": [
    {
      "source": String,
      "target": String
    },
    
    ...
  ]
}
```

### Example message:

```
{
  "nodes": [
    {
      "guid": "node-014",
      "company": "customer-009",
      "location": {
        "x": 5,
        "y": 2
      },
      "type": "client"
    },

    ...

    {
      "guid": "node-039",
      "company": "delivery-company-004",
      "location": {
        "x": 4,
        "y": 2
      },
      "type": "delivery"
    }
  ],
  "edges": [
    {
      "source": "node-083",
      "target": "node-050"
    },

    ...

    {
      "source": "node-083",
      "target": "node-052"
    }
  ]
}
```

### Truck Info Message:
The Graph Visualization Agent receives the truck status information from the Truck Agent in a message of the following format:

**Performative**: INFORM

**Sender**: AID of the TruckAgent

**Receiver**: AID of the GraphVisualizationAgent

**Conversation ID**: "TruckPosUpdate"

**MessageContent**: 

The message content is a JSON string of the following format: 

```
{
  "id": String,
  "x": Float,
  "y": Float,
  "state": String,
  "eta": int
}
```

We currently support three truck states: "MOVING_TO_BAKERY", "MOVING_TO_CUSTOMER" and "IDLE"

### Example message:

```
{
  "id": "delivery-company-001_truck-001",
  "x": 5.2,
  "y": 3.6,
  "state": "IDLE",
  "eta": 5
}
```


<br>
