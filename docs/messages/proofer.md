## Input Message

#### Order Message
- customerId [String]
- guid [String]
- orderDate [json object]
- deliveryDate [json object]
- products [json object]

Order Date
- day [int]
- hour [int]

Delivery Date
- day [int]
- hour [int]

Product
-  Bagel: [int]
-  Donut: [int]
-  Berliner: [int]
-  Muffin: [int]
-  Bread: [int]

Product is a json object. The key is the name of the product and the value is the quantity of each product type. We base this definition based on the sample json file. This is subject to change if the json message changes.


## Output Message

#### Dough Notification Message
- guid


The Dough Preparation stage is initialized with the information of the types of bread it can prepare dough for (as specified in the products field in the scenario)

It receives Order Messages and takes care of dough preparation steps (kneading, resting, item preparation and proofing).

The Proofer agent sends a Dough Notification Message to the Baking Stage. This message only contains the guid of the order, as the Baking Stage already has the Order information. The Dough Notification Message notifies the Baking stage that the order with guid is ready to be baked.
