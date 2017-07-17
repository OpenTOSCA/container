package org.opentosca.container.core.impl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.CSARIDToInstanceToCorrelation;
import org.opentosca.container.core.model.instance.PlanCorrelationToPlanInvocationEvent;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the management of CSARInstances and History.
 */
@Deprecated
public class CSARInstanceManagementService implements ICSARInstanceManagementService {

	private final Logger LOG = LoggerFactory.getLogger(CSARInstanceManagementService.class);
	
	private final CSARIDToInstanceToCorrelation instanceStorage = new CSARIDToInstanceToCorrelation();
	private final PlanCorrelationToPlanInvocationEvent planHistory = new PlanCorrelationToPlanInvocationEvent();
	
	private final Map<String, ServiceTemplateInstanceID> mapCorrelationIDToCSARInstance = new HashMap<>();
	
	private final Map<CSARID, List<String>> mapCSARIDToActivePlanCorrelation = new HashMap<>();
	private final Map<CSARID, List<String>> mapCSARIDToFinishedPlanCorrelation = new HashMap<>();
	private final Map<String, Map<String, String>> mapCorrelationIDToOutputList = new HashMap<>();
	private final Map<String, PlanInvocationEvent> mapCorrelationIdToPlanEvent = new HashMap<>();
	
	
	@Override
	public synchronized void setCorrelationAsActive(final CSARID csarID, final String correlation) {
		this.LOG.trace("Correlate csar {} with active plan instance {}", csarID, correlation);
		if (!this.mapCSARIDToActivePlanCorrelation.containsKey(csarID)) {
			this.mapCSARIDToActivePlanCorrelation.put(csarID, new ArrayList<String>());
		}
		if (!this.mapCSARIDToActivePlanCorrelation.get(csarID).contains(correlation)) {
			this.mapCSARIDToActivePlanCorrelation.get(csarID).add(correlation);
		}
	}
	
	@Override
	public synchronized void setCorrelationAsFinished(final CSARID csarID, final String correlation) {
		this.LOG.trace("Correlate csar {} with finished plan instance {}", csarID, correlation);
		if (!this.mapCSARIDToFinishedPlanCorrelation.containsKey(csarID)) {
			this.mapCSARIDToFinishedPlanCorrelation.put(csarID, new ArrayList<String>());
		}
		if (!this.mapCSARIDToFinishedPlanCorrelation.get(csarID).contains(correlation)) {
			this.mapCSARIDToFinishedPlanCorrelation.get(csarID).add(correlation);
		}
	}
	
	@Override
	public List<String> getActiveCorrelations(final CSARID csarID) {
		// LOG.trace("Return {} correlations of active plans for csar {}",
		// mapCSARIDToFinishedPlanCorrelation.get(csarID).size(), csarID);
		return this.mapCSARIDToActivePlanCorrelation.get(csarID);
	}
	
	@Override
	public List<String> getFinishedCorrelations(final CSARID csarID) {
		// LOG.trace("Return {} correlations of finished plans for csar {}",
		// mapCSARIDToFinishedPlanCorrelation.get(csarID).size(), csarID);
		return this.mapCSARIDToFinishedPlanCorrelation.get(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ServiceTemplateInstanceID> getInstancesOfCSAR(final CSARID csarID) {
		
		this.LOG.debug("Return the current list of instances for CSAR \"" + csarID + "\".");
		return this.instanceStorage.getInstancesOfCSAR(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceTemplateInstanceID createNewInstance(final CSARID csarID, final QName serviceTemplateId) {
		
		this.LOG.info("Create a new instance for CSAR \"" + csarID + "\".");
		final ServiceTemplateInstanceID id = this.instanceStorage.storeNewCSARInstance(csarID, serviceTemplateId);
		
		return id;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storeCorrelationForAnInstance(final CSARID csarID, final ServiceTemplateInstanceID instanceID, final String correlationID) {
		this.LOG.info("Store correlation {} for CSAR \"" + csarID + "\" instance {}.", correlationID, instanceID);
		this.instanceStorage.storeNewCorrelationForInstance(csarID, instanceID, correlationID);
		this.LOG.debug(this.toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteInstance(final CSARID csarID, final ServiceTemplateInstanceID instanceID) {
		this.LOG.debug("Delete instance {} of CSAR {}.", instanceID.toString(), csarID);
		this.LOG.debug(this.toString());
		return this.instanceStorage.deleteInstanceOfCSAR(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void storePublicPlanToHistory(final String correlationID, final PlanInvocationEvent invocation) {
		this.LOG.debug("Store PublicPlan for correlation {}.", correlationID);
		this.planHistory.storePlan(correlationID, invocation);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanInvocationEvent getPlanFromHistory(final String correlationID) {
		this.LOG.debug("Load PublicPlan for correlation {}.", correlationID);
		return this.planHistory.getPlan(correlationID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCorrelationsOfInstance(final CSARID csarID, final ServiceTemplateInstanceID instanceID) {
		return this.instanceStorage.getCorrelationList(csarID, instanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		final String ls = System.getProperty("line.separator");
		
		builder.append("Print stored data in the CSARInstanceManager: " + ls);
		for (final CSARID csarID : this.instanceStorage.getCSARList()) {
			builder.append("Instances of CSAR " + csarID + ls);
			for (final ServiceTemplateInstanceID instanceID : this.instanceStorage.getInstancesOfCSAR(csarID)) {
				builder.append("   " + instanceID + ls);
				for (final String correlationID : this.instanceStorage.getCorrelationList(csarID, instanceID)) {
					builder.append("      " + correlationID + " " + this.getPlanFromHistory(correlationID).getPlanCorrelationID() + ls);
				}
			}
			builder.append(ls);
		}
		return builder.toString();
	}
	
	@Override
	public ServiceTemplateInstanceID getInstanceForCorrelation(final String correlationID) {
		return this.mapCorrelationIDToCSARInstance.get(correlationID);
	}
	
	@Override
	public void correlateCSARInstanceWithPlanInstance(final ServiceTemplateInstanceID instanceID, final String correlationID) {
		this.mapCorrelationIDToCSARInstance.put(correlationID, instanceID);
	}
	
	@Override
	public synchronized Map<String, String> getOutputForCorrelation(final String correlationID) {
		if (!this.mapCorrelationIDToOutputList.containsKey(correlationID)) {
			this.mapCorrelationIDToOutputList.put(correlationID, new HashMap<String, String>());
		}
		return this.mapCorrelationIDToOutputList.get(correlationID);
	}
	
	@Override
	public void correlateCorrelationIdToPlan(final String correlationID, final PlanInvocationEvent planEvent) {
		this.mapCorrelationIdToPlanEvent.put(correlationID, planEvent);
	}
	
	@Override
	public PlanInvocationEvent getPlanForCorrelationId(final String correlationId) {
		return this.mapCorrelationIdToPlanEvent.get(correlationId);
	}
}
