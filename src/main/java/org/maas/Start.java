package org.maas;

import java.util.List;
import java.util.Vector;

public class Start {
    public static void main(String[] args) {
    	List<String> agents = new Vector<>();
    	agents.add("test:org.maas.agents.DummyAgent");

    	List<String> cmd = new Vector<>();
    	cmd.add("-agents");
    	StringBuilder sb = new StringBuilder();
    	for (String a : agents) {
    		sb.append(a);
    		sb.append(";");
    	}
    	cmd.add(sb.toString());
        jade.Boot.main(cmd.toArray(new String[cmd.size()]));
    }
}
