package org.maas;

import java.util.List;
import java.util.Vector;

import org.maas.Initializer;
import org.maas.utils.Data;

public class CustomerInitializer extends Initializer {
    @Override
    public String initialize(String scenarioDirectory) {
        ClassLoader classLoader = getClass().getClassLoader();
        String clientResourcePath = "config/" + scenarioDirectory + "/clients.json";
        String clientFilePath = classLoader.getResource(clientResourcePath).getPath();

        Data customer = new Data();
    	customer.retrieve(clientFilePath);
    	List<String> customerID = customer.getID();
    	
        List<String> agents = new Vector<>();
    	for (String id : customerID) {
    		agents.add(id + ":org.maas.agents.CustomerAgent");
            break;
    	}
        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
	}
}
