## Visualization
- Ángela Enríquez Gómez, Erick Kramer, Ethan Oswald Massey


### Overview
- Two part approach
- "Live" visualization & "Historical Recreation" visualization
- Focuses on showing information useful for debugging
- Not intended for end users but developers

### Live Visualization
- A "main" window can be used to represent all agents (and objects) that are defined at run time
- Most notably this includes the Scheduler and Order Processor but also includes the Dough Maker, Oven Manager, and Delivery Manager
- Dynamic objects such as the Task Manager or Customers will be itemized by guid and name and when clicked on can be expanded (or popped out) into another window
- These windows will be updated as task proceed
- When dynamic objects no longer exist their windows will not be closed but updated to reflect their terminated status


### "Historical Recreation"
- Do to the fast an chaotic nature of real time running, the ability to recreate a simulation is desired
- Recreating a simulation can be done by storing the initial state of all our agents and recording messages as they are passed
- Assuming this is done, a type of debug mode can be designed that allows for stepping through the simulation message by message
- The live visualization code can be used to display the object states
- Simulation can be advanced by 1 to n number of messages


### Library Choice
- Initially test of visualization can be done using a terminal output but this will quickly become unruly as the size of the simulation scales
- A GUI is therefore desired which should not only be easy to use, but common as to allow for quick design and setup
- It is for these reason Swing is proposed
- Swing is an extremely common toolkit for GUI development and is used in industry
- Seeing as Swing is used in industry there are many resources for development and debugging
