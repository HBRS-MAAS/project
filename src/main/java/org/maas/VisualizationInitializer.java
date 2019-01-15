package org.maas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.maas.data.models.Bakery;
import org.maas.utils.JsonConverter;
import com.fasterxml.jackson.core.type.TypeReference;

public class VisualizationInitializer extends Initializer {
    @Override
    public String initialize(String scenarioDirectory) {
        Vector<String> agents = new Vector<>();        
        agents.add("GraphVisualizationAgent:org.maas.agents.GraphVisualizationAgent");
        
        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
    }
}
