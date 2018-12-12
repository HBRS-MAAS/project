# Cooling rack messages

## Out messages

Following type of message is sent to an agent in packaging stage every time a product is finished with cooling process of recipe.

-   **performative**: INFORM
-   **sender**: AID of cooling-racks agent
-   **receiver**: AID of an agent in packaging stage
-   **conversation-id**: `"cooled-product-" + counter.toString()`
-   **content**:
```
{
    "products": {
      String("product_name"): int(quantity),
      String("another_product_name"): int(quantity),
      ... 
    }
}
```
**Note**: `counter` is an int which is incremented everytime a message is sent. It starts at 1.

### Example of content

```
{
    "products": {
      "Bagel": 7,
      "Donut": 1,
      "Berliner": 5,
      "Muffin": 2,
      "Bread": 4
    }
}
```

**Note**:
-   We recommend use of `CyclicBehaviour` which acts as a server inside the receiver for maximum compatibility.
-   Cooling rack agent does not expect any reply for above message.

## In messages

-   **performative**: INFORM
-   **sender**: AID of oven manager
-   **receiver**: AID of cooling-racks agent
-   **conversation-id**: `"baked-products-" + counter.toString()`
-   **content**:
```
[
    {
        guid: String("product_name"),
        quantity: int(quantity),
        coolingDuration: int(cooling_duration)
    }
]
```
### Example of content

```
[
    {
        guid: "Donut",
        quantity: 7,
        coolingDuration: 1
    },
    {
        guid: "Bread",
        quantity: 11,
        coolingDuration: 2
    }
]
```

