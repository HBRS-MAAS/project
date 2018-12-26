package org.maas;

import java.util.Vector;
import org.maas.Initializer;

public class BakingStageInitializer extends Initializer {
    @Override
    public String initialize() {
        Vector<String> agents = new Vector<>();

        agents.add("dummy:org.right_brothers.agents.BakingStageTester");
        agents.add("ovenManager:org.right_brothers.agents.OvenManager");
        agents.add("postBakingProcessor:org.right_brothers.agents.PostBakingProcessor");
        agents.add("cooling-rack:org.maas.agents.CoolingRackAgent");

        String agentInitString = String.join(";", agents);
        agentInitString += ";";
        return agentInitString;
    }
}
