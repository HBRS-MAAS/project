# Proofer messages

## Input Message: Proofing Request

**Peformative**: INFORM

**Sender**: AID of a Dough Manager Agent, which provides the service "Dough-manager"

**Receiver**: AID of a Proofer Agent, which provides the service "Proofer"

**PostTimeStamp**: Current system time

**ConversationId**: 'proofing-Request'

**MessageContent**:


#### Content

```
{
    "proofingTime": float,
    "productType": String,
    "guids": Vector<String>
}

```

#### Example Content


```
{
  "proofingTime": 1,
  "productType": "Berliner",
  "guids": [
    "order-001",
    "order-002"
  ]
}

```


## Output Message

**Peformative**: INFORM

**Sender**: AID of a Proofer Agent, which provides the service "Proofer"

**Receiver**: AID of an agent in the Baking Stage. (In our proof of concept the receiver agent provides the service "Baking-interface")

**PostTimeStamp**: Current system time

**ConversationId**: 'baking-Request'

**MessageContent**:


#### Content

```
{
    "productType": String,
    "guids": Vector<String>,
    "productQuantities": Vector <int>
}
```

#### Example Content

```
{
  "productType": "Berliner",
  "guids": [
    "order-001",
    "order-002"
  ],
  "productQuantities":[10,20]
}
```
