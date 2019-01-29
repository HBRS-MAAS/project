package org.maas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import org.maas.Objects.Bakery;

public class DoughStageVisualization extends Initializer {
    private String scenarioPath;
    private Vector<Bakery> bakeries;
    private Vector<String> agents = new Vector<>();

    @Override
    public String initialize(String scenarioDirectory) {
        // Path of scenario files
        this.scenarioPath = "src/main/resources/config/" + scenarioDirectory + "/" ;
        getBakery(this.scenarioPath);

        // Create a DummyOrderProcesser agent
        agents.add("DummyOrderProcesser:org.mas_maas.agents.DummyOrderProcesser(" + scenarioPath + ")");

        // Create looger
        agents.add("LoggingAgent:org.mas_maas.agents.LoggingAgent(" + scenarioPath + ")");

        // Create agents per bakery
        for (Bakery bakery : bakeries) {
        //Bakery bakery = bakeries.get(0);

            String bakeryId = bakery.getGuid();
            System.out.println(bakeryId);

            agents.add("DoughManager_" + bakeryId + ":org.mas_maas.agents.DoughManager(" + scenarioPath + "," + bakeryId +")");

            agents.add("Proofer_" + bakeryId + ":org.maas.agents.Proofer(" + bakeryId + ")");

            //Adding for testig. Remove it later!
            // agents.add("BakingInterface_" + bakeryId + ":org.mas_maas.agents.BakingManager(" + scenarioPath + "," + bakeryId +")");
        }

        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
    }

    public void getBakery(String scenarioName){
        // guid is the name of the bakery
        String jsonDir = scenarioName;
        try {
            // System.out.println("Working Directory = " + System.getProperty("user.dir"));
            String bakeryFile = new Scanner(new File(jsonDir + "bakeries.json")).useDelimiter("\\Z").next();
            this.bakeries = JSONConverter.parseBakeries(bakeryFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
