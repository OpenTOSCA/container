package org.opentosca.csarinstancemanagement.service.impl;

import java.util.List;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.csarinstancemanagement.CSARIDToInstanceToCorrelation;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.csarinstancemanagement.PlanCorrelationToPublicPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the management of CSARInstances and History.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceManagementService implements ICSARInstanceManagementService {
	
	private final Logger LOG = LoggerFactory.getLogger(CSARInstanceManagementService.class);
	
	private final CSARIDToInstanceToCorrelation instanceStorage = new CSARIDToInstanceToCorrelation();
	private final PlanCorrelationToPublicPlan planHistory = new PlanCorrelationToPublicPlan();
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CSARInstanceID> getInstancesOfCSAR(CSARID csarID) {
		
		this.LOG.debug("Return the current list of instances for CSAR \"" + csarID + "\".");
		return this.instanceStorage.getInstancesOfCSAR(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CSARInstanceID createNewInstance(CSARID csarID) {
		
		this.LOG.info("Create a new instance for CSAR \"" + csarID + "\".");
		CSARInstanceID id = this.instanceStorage.storeNewCSARInstance(csarID);
		
		return id;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCorrelationForAnInstance(CSARID csarID, CSARInstanceID instanceID, String correlationID) {
		this.LOG.info("Store correlation {} for CSAR \"" + csarID + "\" instance {}.", correlationID, instanceID);
		this.instanceStorage.storeNewCorrelationForInstance(csarID, instanceID, correlationID);
		this.LOG.debug(this.toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteInstance(CSARID csarID, CSARInstanceID instanceID) {
		this.LOG.debug("Delete instance {} of CSAR {}.", instanceID.toString(), csarID);
		this.LOG.debug(this.toString());
		return this.instanceStorage.deleteInstanceOfCSAR(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePublicPlanToHistory(String correlationID, PublicPlan publicPlan) {
		this.LOG.debug("Store PublicPlan for correlation {}.", correlationID);
		this.planHistory.storePublicPlan(correlationID, publicPlan);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PublicPlan getPublicPlanFromHistory(String correlationID) {
		this.LOG.debug("Load PublicPlan for correlation {}.", correlationID);
		return this.planHistory.getPublicPlan(correlationID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCorrelationsOfInstance(CSARID csarID, CSARInstanceID instanceID) {
		return this.instanceStorage.getCorrelationList(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		builder.append("Print stored data in the CSARInstanceManager: " + ls);
		for (CSARID csarID : this.instanceStorage.getCSARList()) {
			builder.append("Instances of CSAR " + csarID + ls);
			for (CSARInstanceID instanceID : this.instanceStorage.getInstancesOfCSAR(csarID)) {
				builder.append("   " + instanceID + ls);
				for (String correlationID : this.instanceStorage.getCorrelationList(csarID, instanceID)) {
					builder.append("      " + correlationID + " " + this.getPublicPlanFromHistory(correlationID).getPlanID() + ls);
				}
			}
			builder.append(ls);
		}
		return builder.toString();
	}
}
