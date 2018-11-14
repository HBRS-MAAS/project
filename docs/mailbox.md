## Message to mailbox
### The TruckAgent informs the mailbox about the delivered order
#### ACL Performative: INFORM
#### Message Content: JSON string with the content described below

```json
{
  "DeliveryStatus": {
    "OrderID": "String-UniqueOrderIDReceivedFromOrderProcessor",
    "DeliveredTo": "String-CustomerName",
    "DeliveredBy": "String-TruckID",
    "DayOfDelivery": "Integer-Day(0-365)",
    "TimeOfDelivery": "Integer-HourOfDay(0-23)",
    "NumOfBoxes": "Integer-CountOfBoxes",
    "ProducedBy": "String-NameOfBakery"
  }
}
```

#### Example message:

```json
{
  "DeliveryStatus": {
    "OrderID": "Order-152",
    "DeliveredTo": "Customer-001",
    "DeliveredBy": "Truck-001",
    "DayOfDelivery": 35,
    "TimeOfDelivery": 16,
    "NumOfBoxes": 5,
    "ProducedBy": "Flying Saucers Bakery"
  }
}
```

<br>


## Message from the mailbox
### The mail box relays the delivery status message to the concerned customer and other agents who are interested in knowing the delivery status of the order
#### ACL Performative: INFORM
#### Message Content: JSON string with the content described below

```json
{
  "DeliveryStatus": {
    "OrderID": "String-UniqueOrderIDReceivedFromOrderProcessor",
    "DeliveredTo": "String-CustomerName",
    "DeliveredBy": "String-TruckID",
    "DayOfDelivery": "Integer-Day(0-365)",
    "TimeOfDelivery": "Integer-HourOfDay(0-23)",
    "NumOfBoxes": "Integer-CountOfBoxes",
    "ProducedBy": "String-NameOfBakery"
  }
}
```

#### Example message:

```json
{
  "DeliveryStatus": {
    "OrderID": "Order-152",
    "DeliveredTo": "Customer-001",
    "DeliveredBy": "Truck-001",
    "DayOfDelivery": 35,
    "TimeOfDelivery": 16,
    "NumOfBoxes": 5,
    "ProducedBy": "Flying Saucers Bakery"
  }
}
```
<br>