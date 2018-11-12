# The project visualization could be divided into two main windows

## Orders 
* A table containing the orders with their status in one window.

## Bakery and production visualization
* This window should contain a list of the current orders with their status.
* All the agents of the baking till transportation showing their current activities and their products available for the next stages.
* All could be easily visualized using tables.
## Transportation
* For the transportation their could be a graph for all the bakeries, customers and trucks updating their positions periodically.

# Potential toolkits and frameworks

## __Abstract Window Toolkit(AWT)__
* The most primitive toolkit for simple windows creation and visualization with some basic fields that could be added.
* The main advantage of it is that there are so many examples and implementations using it. 

## Swing
* Swing is an extension of AWT toolkit.
* It is easier to use and contains more options than the AWT. 
* It also contains a class called JTable which is suitable to visualize this jade environment for different orders and agents.
* And for graphs there are also some implemented examples available online to help as a guidance for the transportation.

## The terminal
* The simplest way is to visualize using the terminal.
* Then tables would be the main option to use in all cases.
* Even for the transportation a table updating all the trucks positions then could be used.
* This all is based on printing directly to the terminal.

# Implementation idea
* To create a table of all the order a communication with the order processor should be established to get all the orders and their AID to establish communication with them.
* Communicate with all the agents and update the windows and graphs based on the reply messages.
* Such a way could be a little bit messy so there should be one behavior checking all the agents and then update the visualization once based on all agents not only one by one.
* For each window, a behavior is responsible of updating it.
* For the agents to be visualized a visualization server should be implemented so that the visualization agent can use it to update itself.
