package org.maas;

import java.util.Vector;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import com.fasterxml.jackson.core.type.TypeReference;

import org.maas.Initializer;
import org.maas.utils.JsonConverter;
import org.maas.data.models.Bakery;

public class BakingStageInitializer extends Initializer {
    @Override
    public String initialize(String scenarioDirectory) {
        Vector<String> bakeryNames = this.getBakeryNames(scenarioDirectory);
        Vector<String> agents = new Vector<>();

        for (String bakeryName : bakeryNames) {
        	agents.add(bakeryName + "-dummy:org.right_brothers.agents.BakingStageTester(" + bakeryName + ")");
            agents.add(bakeryName + "-ovenManager:org.right_brothers.agents.OvenManager(" + bakeryName + ")");
            agents.add(bakeryName + "-postBakingProcessor:org.right_brothers.agents.PostBakingProcessor(" + bakeryName + ")");
            agents.add(bakeryName + "-cooling-rack:org.maas.agents.CoolingRackAgent(" + bakeryName + ")");
        }

        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
    }
    private Vector<String> getBakeryNames (String scenarioDirectory) {
        String filePath = "config/" + scenarioDirectory + "/bakeries.json";
        String fileString = this.readConfigFile(filePath);
        TypeReference<?> type = new TypeReference<Vector<Bakery>>(){};
        Vector<Bakery> bakeries = JsonConverter.getInstance(fileString, type);
        Vector<String> bakeryNames = new Vector<String> (bakeries.size());
        for (Bakery bakery : bakeries) {
            bakeryNames.add(bakery.getGuid());
        }
        return bakeryNames;
    }
    private String readConfigFile (String filePath){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        String fileString = "";
        try (Scanner sc = new Scanner(file)) {
            sc.useDelimiter("\\Z"); 
            fileString = sc.next();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileString;
    }
}
