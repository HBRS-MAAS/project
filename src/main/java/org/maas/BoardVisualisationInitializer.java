package org.maas;

import java.util.Vector;
import org.maas.Initializer;

public class BoardVisualisationInitializer extends Initializer {
	private String endTime;
	
	public BoardVisualisationInitializer(String endTime) {
		this.endTime = endTime;
	}
	
    @Override
    public String initialize(String scenarioDirectory) {
        Vector<String> agents = new Vector<>();

        agents
        .add("visualisation:org.right_brothers.agents.VisualizationAgent(" + scenarioDirectory + ", " + endTime + ")");

        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
    }
}
