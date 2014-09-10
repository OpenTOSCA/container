package org.opentosca.model.csarinstancemanagement;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maps a CorrelationID to a PublicPlan for the CSARInstance History.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class PlanCorrelationToPublicPlan {
	
	private Logger LOG = LoggerFactory.getLogger(PlanCorrelationToPublicPlan.class);
	
	// map of CorrelationID to PublicPlan
	// TODO make persistent
	private Map<String, PublicPlan> storageMap = new HashMap<String, PublicPlan>();
	
	
	public PublicPlan getPublicPlan(String planCorrelation) {
		return this.storageMap.get(planCorrelation);
	}
	
	public void storePublicPlan(String planCorrelation, PublicPlan publicPlan) {
		if (!this.storageMap.containsKey(planCorrelation)) {
			this.storageMap.put(planCorrelation, publicPlan);
			return;
		}
		this.LOG.warn("The correlation was stored before. Therefore the passed PublicPlan was not stored again.");
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String ls = System.getProperty("line.separator");
		
		builder.append("Currently stored informations for instances and correlations:" + ls);
		for (String correlation : this.storageMap.keySet()) {
			builder.append("Correlation \"" + correlation + "\":" + ls + "   ");
			builder.append(this.storageMap.get(correlation).toString());
			builder.append(ls + ls);
		}
		
		return builder.toString();
	}
	
}
