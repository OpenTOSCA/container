package org.opentosca.csarinstancemanagement.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.model.csarinstancemanagement.CSARIDToInstanceToCorrelation;
import org.opentosca.model.csarinstancemanagement.PlanCorrelationToPlanInvocationEvent;
import org.opentosca.model.csarinstancemanagement.ServiceTemplateInstanceID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
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
@Deprecated
public class CSARInstanceManagementService implements ICSARInstanceManagementService {
	
	
	private final Logger LOG = LoggerFactory.getLogger(CSARInstanceManagementService.class);
	
	private final CSARIDToInstanceToCorrelation instanceStorage = new CSARIDToInstanceToCorrelation();
	private final PlanCorrelationToPlanInvocationEvent planHistory = new PlanCorrelationToPlanInvocationEvent();
	
	private Map<String, ServiceTemplateInstanceID> mapCorrelationIDToCSARInstance = new HashMap<String, ServiceTemplateInstanceID>();
	
	private Map<CSARID, List<String>> mapCSARIDToActivePlanCorrelation = new HashMap<CSARID, List<String>>();
	private Map<CSARID, List<String>> mapCSARIDToFinishedPlanCorrelation = new HashMap<CSARID, List<String>>();
	private Map<String, Map<String, String>> mapCorrelationIDToOutputList = new HashMap<String, Map<String, String>>();
	private Map<String, PlanInvocationEvent> mapCorrelationIdToPlanEvent = new HashMap<String, PlanInvocationEvent>();
	
	
	@Override
	public synchronized void setCorrelationAsActive(CSARID csarID, String correlation) {
		LOG.trace("Correlate csar {} with active plan instance {}", csarID, correlation);
		if (!mapCSARIDToActivePlanCorrelation.containsKey(csarID)) {
			mapCSARIDToActivePlanCorrelation.put(csarID, new ArrayList<String>());
		}
		if (!mapCSARIDToActivePlanCorrelation.get(csarID).contains(correlation)) {
			mapCSARIDToActivePlanCorrelation.get(csarID).add(correlation);
		}
	}
	
	@Override
	public synchronized void setCorrelationAsFinished(CSARID csarID, String correlation) {
		LOG.trace("Correlate csar {} with finished plan instance {}", csarID, correlation);
		if (!mapCSARIDToFinishedPlanCorrelation.containsKey(csarID)) {
			mapCSARIDToFinishedPlanCorrelation.put(csarID, new ArrayList<String>());
		}
		if (!mapCSARIDToFinishedPlanCorrelation.get(csarID).contains(correlation)) {
			mapCSARIDToFinishedPlanCorrelation.get(csarID).add(correlation);
		}
	}
	
	@Override
	public List<String> getActiveCorrelations(CSARID csarID) {
		// LOG.trace("Return {} correlations of active plans for csar {}",
		// mapCSARIDToFinishedPlanCorrelation.get(csarID).size(), csarID);
		return mapCSARIDToActivePlanCorrelation.get(csarID);
	}
	
	@Override
	public List<String> getFinishedCorrelations(CSARID csarID) {
		// LOG.trace("Return {} correlations of finished plans for csar {}",
		// mapCSARIDToFinishedPlanCorrelation.get(csarID).size(), csarID);
		return mapCSARIDToFinishedPlanCorrelation.get(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ServiceTemplateInstanceID> getInstancesOfCSAR(CSARID csarID) {
		
		LOG.debug("Return the current list of instances for CSAR \"" + csarID + "\".");
		return instanceStorage.getInstancesOfCSAR(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceTemplateInstanceID createNewInstance(CSARID csarID, QName serviceTemplateId) {
		
		LOG.info("Create a new instance for CSAR \"" + csarID + "\".");
		ServiceTemplateInstanceID id = instanceStorage.storeNewCSARInstance(csarID, serviceTemplateId);
		
		return id;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCorrelationForAnInstance(CSARID csarID, ServiceTemplateInstanceID instanceID, String correlationID) {
		LOG.info("Store correlation {} for CSAR \"" + csarID + "\" instance {}.", correlationID, instanceID);
		instanceStorage.storeNewCorrelationForInstance(csarID, instanceID, correlationID);
		LOG.debug(toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteInstance(CSARID csarID, ServiceTemplateInstanceID instanceID) {
		LOG.debug("Delete instance {} of CSAR {}.", instanceID.toString(), csarID);
		LOG.debug(toString());
		return instanceStorage.deleteInstanceOfCSAR(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePublicPlanToHistory(String correlationID, PlanInvocationEvent invocation) {
		LOG.debug("Store PublicPlan for correlation {}.", correlationID);
		planHistory.storePlan(correlationID, invocation);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanInvocationEvent getPlanFromHistory(String correlationID) {
		LOG.debug("Load PublicPlan for correlation {}.", correlationID);
		return planHistory.getPlan(correlationID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCorrelationsOfInstance(CSARID csarID, ServiceTemplateInstanceID instanceID) {
		return instanceStorage.getCorrelationList(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		builder.append("Print stored data in the CSARInstanceManager: " + ls);
		for (CSARID csarID : instanceStorage.getCSARList()) {
			builder.append("Instances of CSAR " + csarID + ls);
			for (ServiceTemplateInstanceID instanceID : instanceStorage.getInstancesOfCSAR(csarID)) {
				builder.append("   " + instanceID + ls);
				for (String correlationID : instanceStorage.getCorrelationList(csarID, instanceID)) {
					builder.append("      " + correlationID + " " + getPlanFromHistory(correlationID).getPlanCorrelationID() + ls);
				}
			}
			builder.append(ls);
		}
		return builder.toString();
	}
	
	@Override
	public ServiceTemplateInstanceID getInstanceForCorrelation(String correlationID) {
		return mapCorrelationIDToCSARInstance.get(correlationID);
	}
	
	@Override
	public void correlateCSARInstanceWithPlanInstance(ServiceTemplateInstanceID instanceID, String correlationID) {
		mapCorrelationIDToCSARInstance.put(correlationID, instanceID);
	}
	
	@Override
	public synchronized Map<String, String> getOutputForCorrelation(String correlationID) {
		if (!mapCorrelationIDToOutputList.containsKey(correlationID)) {
			mapCorrelationIDToOutputList.put(correlationID, new HashMap<String, String>());
		}
		return mapCorrelationIDToOutputList.get(correlationID);
	}
	
	@Override
	public void correlateCorrelationIdToPlan(String correlationID, PlanInvocationEvent planEvent) {
		mapCorrelationIdToPlanEvent.put(correlationID, planEvent);
	}
	
	@Override
	public PlanInvocationEvent getPlanForCorrelationId(String correlationId) {
		return mapCorrelationIdToPlanEvent.get(correlationId);
	}
}
