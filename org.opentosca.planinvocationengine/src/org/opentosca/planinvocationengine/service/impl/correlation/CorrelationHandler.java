package org.opentosca.planinvocationengine.service.impl.correlation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages active PublicPlans which are still running or response is
 * not processed yet.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CorrelationHandler {
	
	long lastMilli = 0;
	long lastCounter = 0;
	
	// TODO make persistent, fix JPA
	private final Map<CSARID, Map<String, PublicPlan>> mapCorrelationToPublicPlan = new HashMap<CSARID, Map<String, PublicPlan>>();
	
	private final Logger LOG = LoggerFactory.getLogger(CorrelationHandler.class);
	
	
	/**
	 * Synchronized method for creating a new CorrelationID for a PublicPlan.
	 * 
	 * @param publicPlan
	 * @return CorrelationID
	 */
	public synchronized String getNewCorrelationID(PublicPlan publicPlan) {
		
		long time = System.currentTimeMillis();
		
		// if there are multiple CorrelationIDs requested in the same
		// millisecond, the counter is added by 1
		if (time == this.lastMilli) {
			this.lastCounter++;
		} else if (time > this.lastMilli) {
			this.lastCounter = 0;
			this.lastMilli = time;
		} else {
			this.LOG.error("The current nano time is earlier than the last time measured.");
		}
		
		// put together the CorrelationID
		String cID = this.lastMilli + "-" + this.lastCounter;
		
		if (!this.mapCorrelationToPublicPlan.containsKey(new CSARID(publicPlan.getCSARID()))) {
			this.mapCorrelationToPublicPlan.put(new CSARID(publicPlan.getCSARID()), new HashMap<String, PublicPlan>());
		}
		this.mapCorrelationToPublicPlan.get(new CSARID(publicPlan.getCSARID())).put(cID, publicPlan);
		
		return cID;
	}
	
	/**
	 * Returns an active PublicPlan for CorrelationID.
	 * 
	 * @param correlationID
	 * @return PublicPlan
	 */
	public PublicPlan getPublicPlanForCorrelation(String correlationID) {
		for (CSARID csarID : this.mapCorrelationToPublicPlan.keySet()) {
			if (this.mapCorrelationToPublicPlan.get(csarID).containsKey(correlationID)) {
				return this.mapCorrelationToPublicPlan.get(csarID).get(correlationID);
			}
		}
		
		this.LOG.error("There is no entry for the CorrelationID \"" + correlationID + "\".");
		return null;
		
	}
	
	/**
	 * Returns an active PublicPlan for CorrelationID and InstanceID.
	 * 
	 * @param instanceID
	 * @param correlationID
	 * @return PublicPlan
	 */
	public PublicPlan getPublicPlanForCorrelation(CSARInstanceID instanceID, String correlationID) {
		if (this.mapCorrelationToPublicPlan.containsKey(instanceID.getOwner())) {
			if (this.mapCorrelationToPublicPlan.get(instanceID.getOwner()).containsKey(correlationID)) {
				return this.mapCorrelationToPublicPlan.get(instanceID.getOwner()).get(correlationID);
			}
		}
		
		this.LOG.error("There is no entry for the CorrelationID \"" + correlationID + "\".");
		return null;
		
	}
	
	/**
	 * Remove CorrelationID after response of active PublicPlan is processed
	 * 
	 * @param csarid
	 * @param correlationID
	 */
	public void removeCorrelation(CSARID csarid, String correlationID) {
		this.mapCorrelationToPublicPlan.get(csarid).remove(correlationID);
	}
	
	/**
	 * Returns the list of all CorrelationIDs of a CSARInstance.
	 * 
	 * @param instanceID
	 * @return list of CorrelationIDs
	 */
	public List<String> getActiveCorrelationsOfInstance(CSARInstanceID instanceID) {
		List<String> list = new ArrayList<String>();
		
		CSARID csarID = instanceID.getOwner();
		int internalID = instanceID.getInternalID();
		
		if (this.mapCorrelationToPublicPlan.containsKey(csarID)) {
			for (String correlation : this.mapCorrelationToPublicPlan.get(csarID).keySet()) {
				PublicPlan plan = this.mapCorrelationToPublicPlan.get(csarID).get(correlation);
				if (plan.getInternalInstanceInternalID() == internalID) {
					list.add(correlation);
				}
			}
		}
		
		return list;
	}
	
}
