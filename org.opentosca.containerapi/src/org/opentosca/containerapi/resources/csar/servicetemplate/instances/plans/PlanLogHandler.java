package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PlanLogHandler {
	
	
	public static PlanLogHandler instance = new PlanLogHandler();
	
	private Map<String, Map<String, String>> corrToLog = new HashMap<String, Map<String, String>>();
	
	
	private PlanLogHandler() {
	}
	
	public void log(String corrId, String logMsg) {
		if (!corrToLog.containsKey(corrId)) {
			corrToLog.put(corrId, new HashMap<String, String>());
		}
		corrToLog.get(corrId).put(Long.toString(System.currentTimeMillis()), logMsg);
	}
	
	public Map<String, String> getLogsOfPlanInstance(String corrId) {
		if (corrToLog.containsKey(corrId)) {
			TreeMap<String, String> sorted = new TreeMap<>(corrToLog.get(corrId));
			return sorted;
		} else {
			return new HashMap<String, String>();
		}
	}
}
