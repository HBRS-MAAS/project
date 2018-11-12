## Visualization of the bakery scenario

The scenario can be visualized by displaying every agent as a circle or a square in a painted graph. Therefor you could use Frameworks like JavaFx or Swing to start a GUI and add the symbols for the agents in it. The messages could then be displayed as lines which are drawn between the agents. 
There are still two ways to display the scenario: 
1. Live (while the scenario is running)
2. History (a history of all events that occured and can be displayed back in time)

### Live
This could be done by adding listeners to the containers and the jade runtime and listening to creation, messages etc. These information would then be send to the gui and displayed.

### History
This can be done by adding a kind of logging mechanism that logs every event that occures. One possibility is to add listeners to jade like in the live variation. The difference here is that even after the scenario has ended, one can read the logs and replay the visualization of it.
