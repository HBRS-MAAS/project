# RequestQuotations (CFP)
## Customer requests for quotations from all the available bakeries for their list of products and a specific delivery date
### "<ddd.hh\> product_name:quantity; product_name:quantity; product_name:quantity"
<br>

# ReplyToCustomerRequest (PROPOSE/ REFUSE)
## The bakeries propose a quotation or refuse to accept an order
###  "Quotation" / "Rejected"
<br>

# PlaceOrder (ACCEPT_PROPOSAL)
## The Customer places the order with the bakery that provided the least quotation
###  "<ddd.hh\> product_name:quantity; product_name:quantity; product_name:quantity"
<br>

# ConfirmOrder (INFORM)
## The bakery informs the customer if the order was successfully placed.
###  "Accepted:OrderID" / "Rejected"
<br>

