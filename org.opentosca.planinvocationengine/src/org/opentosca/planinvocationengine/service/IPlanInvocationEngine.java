package org.opentosca.planinvocationengine.service;

import java.util.List;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;

/**
 * Interface of the PlanInvocationEngine. This service provides a the
 * functionality of invoking PublicPlans, getting a list of CorrelationIDs of
 * active PublicPlans and one specific PublicPlan.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public interface IPlanInvocationEngine {
	
	/**
	 * Invoke a PublicPlan for a CSAR. If this PublicPlan is of Type
	 * OTHERMANAGEMENT or TERMINATION, the information about the CSARInstance is
	 * stored inside the PublicPlan.
	 * 
	 * @param csarID
	 * @param publicPlan
	 * @return boolean about success
	 */
	public boolean invokePlan(CSARID csarID, PublicPlan publicPlan);
	
	/**
	 * Returns a list of CorrelationIDs of activce PublicPlans of a
	 * CSARInstance.
	 * 
	 * @param csarInstanceID
	 * @return list of CorrelationIDs of active PublicPlans
	 */
	public List<String> getActiveCorrelationsOfInstance(CSARInstanceID csarInstanceID);
	
	/**
	 * Returns a specific active PublicPlan.
	 * 
	 * @param csarInstanceID
	 * @param correlationID
	 * @return PublicPlan
	 */
	public PublicPlan getActivePublicPlanOfInstance(CSARInstanceID csarInstanceID, String correlationID);
	
}