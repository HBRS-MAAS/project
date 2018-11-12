## Messages

#### Order
- customer_id [String]
- order_date [String]
- delivery_date [String]
- list_products [list]

#### Confirmation
- accept [boolean]
- customer_id [String]

#### Time Left
- time_to_complete_order [float]

#### Time Increment
- time_increment [float]

#### Dough Order
- product_type [int]
- preparation_time [float]
- volume [float]
- resting_time [float]
- customer_id [String]

#### Baking Order
- product_type [int]
- quantity [int]
- backing_temperature [float]
- cooling_rate [float]
- backing_time [float]
- customer_id [String]
- quantity_per_slot [int]

#### Delivery Order
- product_type [int]
- quantity [int]
- boxing temperature [float]
- cooling_rate [float]
- customer_id [String]
- quantity per box [int]

#### Notifications
- order_finished [boolean]
- product_type [int]
- customer_id [String]

#### BakedGoods
- customer_id: '001'
- list_products: [1, 2, 3]
