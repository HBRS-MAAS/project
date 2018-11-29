package org.maas;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        List<String> agents = new Vector<>();
        List<String> cmd = new Vector<>();

        List<String> arguments = Arrays.asList(args);

        // execution according to arguments
        if(arguments.size() > 0) {
            // template for running all agents in main container
            if(arguments.get(0).equalsIgnoreCase("someUniqueWord")){
                for (int i = 1; i < arguments.size(); i++) // add additional arguments as provided
                   cmd.add(arguments.get(i)); 

                cmd.add("-agents");
                // add agents according to need
                agents.add("dummy:org.maas.agents.DummyAgent");
            }
            /* An example from right-brothers team's repo
            if(arguments.get(0).equalsIgnoreCase("bakingStageTest")){
                for (int i = 1; i < arguments.size(); i++)
                   cmd.add(arguments.get(i)); 
                cmd.add("-agents");
                agents.add("TimeKeeper:org.right_brothers.agents.TimeKeeper");
                agents.add("dummy:org.right_brothers.agents.BakingStageTester");
                agents.add("ovenManager:org.right_brothers.agents.OvenManager");
                agents.add("intermediater:org.right_brothers.agents.Intermediater");
                agents.add("cooling-rack:org.right_brothers.agents.CoolingRackAgent");
            }
            */
            if(arguments.get(0).equalsIgnoreCase("dummyTest")){  // run dummy and TimeKeeper in same container
                for (int i = 1; i < arguments.size(); i++)
                   cmd.add(arguments.get(i)); 
                cmd.add("-agents");
                agents.add("TimeKeeper:org.maas.agents.TimeKeeper");
                agents.add("dummy:org.maas.agents.DummyAgent");
            }
            if(arguments.get(0).equalsIgnoreCase("server")) {  // run as main container
                for (int i = 1; i < arguments.size(); i++)
                   cmd.add(arguments.get(i)); 
                cmd.add("-agents");
                agents.add("dummy:org.maas.agents.DummyAgent");
                // add additional agents here
            }
            else if(arguments.get(0).equalsIgnoreCase("client")) {  // run as additional container
                cmd.add("-container");
                for (int i = 1; i < arguments.size(); i++)
                   cmd.add(arguments.get(i)); 
                cmd.add("-agents");
                agents.add("TimeKeeper:org.maas.agents.TimeKeeper");
                // add additional agents here
            }
        } 
        // default execution
        else {
            cmd.add("-agents");
            agents.add("TimeKeeper:org.maas.agents.TimeKeeper");
            agents.add("dummy:org.maas.agents.DummyAgent");
        }

        cmd.add(String.join(";", agents));
        jade.Boot.main(cmd.toArray(new String[cmd.size()]));
    }
}
