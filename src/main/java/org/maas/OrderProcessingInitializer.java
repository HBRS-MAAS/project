package org.maas;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class OrderProcessingInitializer extends Initializer {
    @Override
    public String initialize(String scenarioDirectory) {
        ClassLoader classLoader = getClass().getClassLoader();
        String sOPPrefix = ":org.maas.agents.OrderProcessing";
        String sSchPrefix2 = ":org.maas.agents.SchedulerAgent";
        List<String> agents = new Vector<>();
        String bakeries_path = "config/" + scenarioDirectory + "/bakeries.json";
        String meta_path = "config/" + scenarioDirectory + "/meta.json";
        String bakeries = readScenarioFile(classLoader.getResource(bakeries_path).getPath());
        String meta = readScenarioFile(classLoader.getResource(meta_path).getPath());
        if(bakeries == null || meta == null) {
            System.exit(-1);
        }
        JSONArray jaBakeries = new JSONArray(bakeries);
        JSONObject joMeta = new JSONObject(meta);
        Iterator<Object> bakery_iterator = jaBakeries.iterator();
        while (bakery_iterator.hasNext()) {
            JSONObject bakery = (JSONObject) bakery_iterator.next();
            String id = bakery.getString("guid");
            String bakery_idNum = id.split("-")[1];
            agents.add(id + "-orderProcessing" + sOPPrefix);
            agents.add("scheduler-" + bakery_idNum + sSchPrefix2);
        }
    	List<String> cmd = buildCMD(agents, jaBakeries, joMeta);
//         System.out.println(cmd.get(0));
//         System.out.println(cmd.get(1));
        System.out.println(cmd.size());
        System.out.println(agents.size());
        return cmd.get(1);
    }

    public static List<String> buildCMD(List<String> agents, JSONArray jaBakeries, JSONObject joMeta) {
    	StringBuilder sb = new StringBuilder();
        List<String> cmd = new Vector<>();
        Iterator<Object> bakery_iterator = jaBakeries.iterator();

        cmd.add("-agents");
    	JSONObject bakery = new JSONObject();
		for (String a : agents) {
                if(a.contains("Scheduler")){
                    appendAgentAndArguments(sb, bakery.toString().replaceAll(",", "###") + "," + joMeta.toString().replaceAll(",", "###"), a);
                    sb.append(";");
                    continue;
                }
                if(a.contains("OrderProcessing")) {
                    bakery = (JSONObject)bakery_iterator.next();
                    appendAgentAndArguments(sb, bakery.toString().replaceAll(",", "###") + "," + joMeta.toString().replaceAll(",", "###"), a);
                    sb.append(";");
                    continue;
                }
			sb.append(a);
            sb.append(";");
		}
        cmd.add(sb.toString());
    	return cmd;
	}

	public static String readScenarioFile(String path) {
    	String jsonString = null;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = null;
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            jsonString = sb.toString();
		}
		catch(IOException e) {
            e.printStackTrace();
            System.out.println("Error reading scenario file!");
		}
		return jsonString;
	}

	private static void appendAgentAndArguments(StringBuilder sb, String argument, String agent) {
        sb.append(agent);
        sb.append("(");
        sb.append(argument);
        sb.append(")");
    }

    private static void parsingBakeryId(StringBuilder sb, JSONObject joObject) {
        sb.append(joObject.get("guid"));
        sb.append("#");
    }

}
