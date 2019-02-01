# MAAS Project - <Team Name>

Add a brief description of your project. Make sure to keep this README updated, particularly on how to run your project from the **command line**.

## Team Members
* Name1 LastName1 - [@githubusername](https://github.com/username)
* Name2 LastName2 - [@githubusername](https://github.com/username)

## Dependencies
* JADE v.4.5.0
* ...

## How to run
Just install gradle and run:

    gradle run

### Run with multiple machines
- Connect machines to a common network
- Find the ip of the server/host machine (for example `192.168.1.123`)
- Find a port which can be used for JADE communication (for example `5555`)

For server/host machine

    gradle run --args="-isHost 192.168.1.123 -localPort 5555 -stage1 -stage2 ... -noTK"

For client machines

    gradle run --args="-host 192.168.1.123 -port 5555 -stage3 -noTK"
    
For the client which executes gradle command later than every other machine

    gradle run --args="-host 192.168.1.123 -port 5555 -stageN"


It will automatically get the dependencies and start JADE with the configured agents.
In case you want to clean you workspace run

    gradle clean

### Run Graph (Street Network) Visualization
Note:
Since the JavaFX framework does not support starting multiple application threads simultaneously, it is not possible to initiate two visualizations for the project at the same time.
Therefore, the graph visualization clashes with other visualization applications and would not show up if run along with them. In particular, the graph visualization waits for five seconds before starting the thread in order to ensure that the StreetNetwork and all Truck agents are up.

In order to run the graph visualization, disable the initialization of other visualization agents in the Start.java file. For example, to disable 'Board Visualization' comment out the lines as shown below:
```
if(visualizationStage) {
    Initializer init = new VisualizationInitializer();
    sb.append(init.initialize(scenarioDirectory));
            
    // Initializer boardInit = new BoardVisualisationInitializer(endTime);
    // sb.append(boardInit.initialize(scenarioDirectory));
}
```
After doing this, start the visualization using the follow command:
```
gradle run --args='-visualization'
```
Note that, at the moment, the simulation time is very short, and so the graph visualization will launch and shut down quickly.

For detailed documentation about the design and usage of the graph visualization [click here](docs/visualizer/commitment_issues/VIsualizationDocument.pdf).
The descriptions of the messages required by the visualization agent can be found [here](docs/messages/GraphVisualization.md). 


### Testing Baking
For testing baking stage

    gradle run --args="-baking"

For testing packaging stage
    
    gradle run --args="-packaging"
    
For testing board visualization (**right brothers**). Please note that, the platform will shut down after visualizer window is closed using close (X) button or menu at top right.

	gradle run --args="-visualization"
	
For testing multiple stages with visualization

	gradle run --args="-baking -packaging -visualization"
	

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
