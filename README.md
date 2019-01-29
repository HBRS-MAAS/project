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

### Run only the **Dough Preparation Stage** with **visualization**

    gradle run --args='-doughPrepVisual -scenarioDirectory nameScenarioDirectory'

Example:

    gradle run --args='-doughPrepVisual -scenarioDirectory small'

## Eclipse
To use this project with eclipse run

    gradle eclipse

This command will create the necessary eclipse files.
Afterwards you can import the project folder.
