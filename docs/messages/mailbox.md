# Mailbox messages
## Incoming
The TruckAgent informs the mailbox about the delivered order using the following message:

**Performative**: INFORM

**Sender**: AID of the TruckAgent that sends this message

**Receiver**: AID of the MailboxAgent

**PostTimeStamp**: Current system time 

**ConversationId**: 'order-001' 

**MessageContent**: 

The message content is a JSON string as described below: 

```json
{
  "DeliveryStatus": {
    "OrderDeliveredTo": "String-CustomerName",
    "OrderDeliveredBy": "String-TruckID",
    "DayOfDelivery": "Integer-Day(0-365)",
    "TimeOfDelivery": "Integer-HourOfDay(0-23)",
    "NumOfBoxes": "Integer-CountOfBoxes",
    "ProducedBy": "String-NameOfBakery"
  }
}
```

### Example message:

```json
{
  "DeliveryStatus": {
    "OrderDeliveredTo": "Customer-001",
    "OrderDeliveredBy": "Truck-001",
    "DayOfDelivery": 35,
    "TimeOfDelivery": 16,
    "NumOfBoxes": 5,
    "ProducedBy": "Flying Saucers Bakery"
  }
}
```

<br>



## Outgoing
The mail box relays the delivery status message to the concerned customer and other agents who are interested in knowing the delivery status of the order using the following message:

**Performative**: INFORM

**Sender**: AID of the MailboxAgent

**Receiver**: AID of the Concerned CutomerAgent. If more agents are interested in receiving this message, thier AID's will also be added to the reciever list.

**PostTimeStamp**: Current system time 

**ConversationId**: 'order-001' 

**MessageContent**: 

The message content is a JSON string as described below: 

```json
{
  "DeliveryStatus": {
    "OrderDeliveredTo": "String-CustomerName",
    "OrderDeliveredBy": "String-TruckID",
    "DayOfDelivery": "Integer-Day(0-365)",
    "TimeOfDelivery": "Integer-HourOfDay(0-23)",
    "NumOfBoxes": "Integer-CountOfBoxes",
    "ProducedBy": "String-NameOfBakery"
  }
}
```

### Example message:

```json
{
  "DeliveryStatus": {
    "OrderDeliveredTo": "Customer-001",
    "OrderDeliveredBy": "Truck-001",
    "DayOfDelivery": 35,
    "TimeOfDelivery": 16,
    "NumOfBoxes": 5,
    "ProducedBy": "Flying Saucers Bakery"
  }
}
```
<br>