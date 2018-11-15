# Cooling rack messages

## Out messages

Following type of message is sent to an agent in packaging stage every time a product is finished with cooling process of recipe.

-   **performative**: INFORM
-   **receiver**: TBD
-   **content**:
```
{
    "products": {
      "product_name": int,
      "another_product_name": int,
      ... 
    }
}
```

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

# In messages

-   **performative**: INFORM
-   **sender**: TBD
-   **content**:
```
{
    productName: String,
    coolingRate: int,
}
```


