package org.opentosca.planinvocationengine.service.impl.correlation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
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
    // CSARID to CorrelationID to Invocation Event
    private Map<CSARID, Map<String, PlanInvocationEvent>> mapCorrelationToPublicPlan = new HashMap<CSARID, Map<String, PlanInvocationEvent>>();

    private final Logger LOG = LoggerFactory.getLogger(CorrelationHandler.class);

    /**
     * Synchronized method for creating a new CorrelationID for a PublicPlan.
     * 
     * @param publicPlan
     * @return CorrelationID
     */
    public synchronized String getNewCorrelationID(CSARID csarID, PlanInvocationEvent event) {

	long time = System.currentTimeMillis();

	// if there are multiple CorrelationIDs requested in the same
	// millisecond, the counter is added by 1
	if (time == lastMilli) {
	    lastCounter++;
	} else if (time > lastMilli) {
	    lastCounter = 0;
	    lastMilli = time;
	} else {
	    LOG.error("The current nano time is earlier than the last time measured.");
	}

	// put together the CorrelationID
	String cID = lastMilli + "-" + lastCounter;

	if (!mapCorrelationToPublicPlan.containsKey(csarID)) {
	    mapCorrelationToPublicPlan.put(csarID, new HashMap<String, PlanInvocationEvent>());
	}
	mapCorrelationToPublicPlan.get(csarID).put(cID, event);

	return cID;
    }

    /**
     * Returns an active PublicPlan for CorrelationID.
     * 
     * @param correlationID
     * @return PublicPlan
     */
    public TPlanDTO getPublicPlanForCorrelation(String correlationID) {
	for (CSARID csarID : mapCorrelationToPublicPlan.keySet()) {
	    if (mapCorrelationToPublicPlan.get(csarID).containsKey(correlationID)) {

		PlanInvocationEvent event = mapCorrelationToPublicPlan.get(csarID).get(correlationID);
		TPlanDTO plan = new TPlanDTO();
		plan.setId(event.getPlanID());
		plan.setName(event.getPlanName());
		plan.setPlanLanguage(event.getPlanLanguage());
		plan.setPlanType(event.getPlanType());
		plan.setInputParameters(new TPlanDTO.InputParameters());
		plan.getInputParameters().getInputParameter().addAll(event.getInputParameter());
		plan.setOutputParameters(new TPlanDTO.OutputParameters());
		plan.getOutputParameters().getOutputParameter().addAll(event.getOutputParameter());

		return plan;
	    }
	}

	LOG.error("There is no entry for the CorrelationID \"" + correlationID + "\".");
	return null;

    }

    /**
     * Returns an active PublicPlan for CorrelationID and InstanceID.
     * 
     * @param instanceID
     * @param correlationID
     * @return PublicPlan
     */
    public TPlanDTO getPlanDTOForCorrelation(CSARInstanceID instanceID, String correlationID) {
	if (mapCorrelationToPublicPlan.containsKey(instanceID.getOwner())) {
	    if (mapCorrelationToPublicPlan.get(instanceID.getOwner()).containsKey(correlationID)) {

		PlanInvocationEvent event = mapCorrelationToPublicPlan.get(instanceID.getOwner()).get(correlationID);
		TPlanDTO plan = new TPlanDTO();
		plan.setId(event.getPlanID());
		plan.setName(event.getPlanName());
		plan.setPlanLanguage(event.getPlanLanguage());
		plan.setPlanType(event.getPlanType());
		plan.setInputParameters(new TPlanDTO.InputParameters());
		plan.getInputParameters().getInputParameter().addAll(event.getInputParameter());
		plan.setOutputParameters(new TPlanDTO.OutputParameters());
		plan.getOutputParameters().getOutputParameter().addAll(event.getOutputParameter());

		return plan;
	    }
	}

	LOG.error("There is no entry for the CorrelationID \"" + correlationID + "\".");
	return null;

    }

    /**
     * Remove CorrelationID after response of active PublicPlan is processed
     * 
     * @param csarid
     * @param correlationID
     */
    public void removeCorrelation(CSARID csarid, String correlationID) {
	mapCorrelationToPublicPlan.get(csarid).remove(correlationID);
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
	// int internalID = instanceID.getInternalID();

	if (mapCorrelationToPublicPlan.containsKey(csarID)) {
	    for (String correlation : mapCorrelationToPublicPlan.get(csarID).keySet()) {
		// TPlan plan =
		// mapCorrelationToPublicPlan.get(csarID).get(correlation);
		// if (plan.getInternalInstanceInternalID() == internalID) {
		list.add(correlation);
		// }
	    }
	}

	return list;
    }

}
