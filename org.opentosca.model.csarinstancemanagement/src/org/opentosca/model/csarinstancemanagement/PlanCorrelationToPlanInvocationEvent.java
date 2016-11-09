package org.opentosca.model.csarinstancemanagement;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maps a CorrelationID to a PlanInvocationEvent for the CSARInstance History.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class PlanCorrelationToPlanInvocationEvent {
	
	private Logger LOG = LoggerFactory.getLogger(PlanCorrelationToPlanInvocationEvent.class);
	
	// map of CorrelationID to PlanInvocationEvent
	// TODO make persistent
	private Map<String, PlanInvocationEvent> storageMap = new HashMap<String, PlanInvocationEvent>();
	
	
	public PlanInvocationEvent getPlan(String planCorrelation) {
		return storageMap.get(planCorrelation);
	}
	
	public void storePlan(String planCorrelation, PlanInvocationEvent planInvocationEvent) {
		//	if (!storageMap.containsKey(planCorrelation)) {
		storageMap.put(planCorrelation, planInvocationEvent);
		//	    return;
		//	}
		//	LOG.warn("The correlation was stored before. Therefore the passed PlanInvocationEvent was not stored again.");
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String ls = System.getProperty("line.separator");
		
		builder.append("Currently stored informations for instances and correlations:" + ls);
		for (String correlation : storageMap.keySet()) {
			builder.append("Correlation \"" + correlation + "\":" + ls + "   ");
			builder.append(storageMap.get(correlation).toString());
			builder.append(ls + ls);
		}
		
		return builder.toString();
	}
	
}
