package org.opentosca.csarinstancemanagement.service;

import java.util.List;
import java.util.Map;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;

/**
 * Interface of the CSARInstance management and History.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public interface ICSARInstanceManagementService {
	
	/**
	 * Returns a list of instances of a CSAR.
	 * 
	 * @param csarID the CSAR ID
	 * @return List of ICSARInstanceID or empty list
	 */
	public List<CSARInstanceID> getInstancesOfCSAR(CSARID csarID);
	
	/**
	 * Creates a new instance for a certain CSAR.
	 * 
	 * @param csarID the certain CSAR
	 * @return the ID of the new instance
	 */
	public CSARInstanceID createNewInstance(CSARID csarID);
	
	/**
	 * Deletes a CSARInstance
	 * 
	 * @param csarID of CSARInstance
	 * @param instanceID ID of the CSARInstance
	 * 
	 * @return boolean for success
	 */
	public boolean deleteInstance(CSARID csarID, CSARInstanceID instanceID);
	
	/**
	 * Stores a PublicPlan to History.
	 * 
	 * @param correlationID
	 * @param plan
	 */
	public void storePublicPlanToHistory(String correlationID, PlanInvocationEvent invocation);
	
	/**
	 * Returns a PublicPlan of the History.
	 * 
	 * @param correlationID of the PublicPlan
	 * @return PublicPlan
	 */
	public PlanInvocationEvent getPlanFromHistory(String correlationID);
	
	/**
	 * Maps a CSARInstance with a CorrelationID inside the History.
	 * 
	 * @param csarID
	 * @param instanceID
	 * @param correlationID
	 */
	public void storeCorrelationForAnInstance(CSARID csarID, CSARInstanceID instanceID, String correlationID);
	
	/**
	 * Returns all CorrelationIDs of PublicPlans mapped to a specific
	 * CSARInstance.
	 * 
	 * @param csarID
	 * @param instanceID
	 * @return list of CorrelationIDs
	 */
	public List<String> getCorrelationsOfInstance(CSARID csarID, CSARInstanceID instanceID);
	
	public CSARInstanceID getInstanceForCorrelation(String correlationID);
	
	public void correlateCSARInstanceWithPlanInstance(CSARInstanceID instanceID, String correlationID);
	
	public Map<String, String> getOutputForCorrelation(String correlationID);
	
	void setCorrelationAsActive(CSARID csarID, String correlation);
	
	void setCorrelationAsFinished(CSARID csarID, String correlation);
	
	List<String> getActiveCorrelations(CSARID csarID);
	
	List<String> getFinishedCorrelations(CSARID csarID);
}
