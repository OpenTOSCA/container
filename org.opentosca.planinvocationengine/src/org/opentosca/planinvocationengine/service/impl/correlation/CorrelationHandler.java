package org.opentosca.planinvocationengine.service.impl.correlation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.ServiceTemplateInstanceID;
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
	private Map<CSARID, Map<QName, Map<Integer, Map<String, PlanInvocationEvent>>>> mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan = new HashMap<CSARID, Map<QName, Map<Integer, Map<String, PlanInvocationEvent>>>>();
	private Map<String, Integer> mapCorrIdToFakedServiceTemplateInstanceId = new HashMap<>();

	private final Logger LOG = LoggerFactory.getLogger(CorrelationHandler.class);

	/**
	 * Synchronized method for creating a new CorrelationID for a PublicPlan.
	 * 
	 * @param csarInstanceID
	 * @param serviceTemplateId
	 * @param b
	 * 
	 * @param publicPlan
	 * @return CorrelationID
	 */
	public synchronized String getNewCorrelationID(CSARID csarID, QName serviceTemplateId,
			int serviceTemplateInstanceId, PlanInvocationEvent event, boolean isBuildPlan) {

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
		String corrID = lastMilli + "-" + lastCounter;

		if (!mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(csarID)) {
			mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.put(csarID,
					new HashMap<QName, Map<Integer, Map<String, PlanInvocationEvent>>>());
		}
		if (!mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
				.containsKey(serviceTemplateId)) {
			mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).put(serviceTemplateId,
					new HashMap<Integer, Map<String, PlanInvocationEvent>>());
		}
		if (!mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
				.containsKey(serviceTemplateInstanceId)) {
			mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
					.put(serviceTemplateInstanceId, new HashMap<String, PlanInvocationEvent>());
		}
		mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
				.get(serviceTemplateInstanceId).put(corrID, event);

		if (isBuildPlan) {
			mapCorrIdToFakedServiceTemplateInstanceId.put(corrID, serviceTemplateInstanceId);
		}

		return corrID;
	}

	public synchronized void correlateBuildPlanCorrToServiceTemplateInstanceId(CSARID csarID, QName serviceTemplateId,
			String corrId, int correctSTInstanceId) {

		Integer falseSTInstanceId = mapCorrIdToFakedServiceTemplateInstanceId.get(corrId);

		if (falseSTInstanceId != null) {
			Map<String, PlanInvocationEvent> map = mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan
					.get(csarID).get(serviceTemplateId).remove(falseSTInstanceId);
			mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
					.put(correctSTInstanceId, map);
		}

	}

	/**
	 * Returns an active PublicPlan for CorrelationID.
	 * 
	 * @param correlationID
	 * @return PublicPlan
	 */
	public TPlanDTO getPublicPlanForCorrelation(String correlationID) {
		for (CSARID csarID : mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.keySet()) {
			for (QName serviceTemplateId : mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan
					.get(csarID).keySet()) {
				for (Integer serviceTemplateInstanceId : mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan
						.get(csarID).get(serviceTemplateId).keySet()) {

					if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
							.get(serviceTemplateId).get(serviceTemplateInstanceId).containsKey(correlationID)) {

						PlanInvocationEvent event = mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan
								.get(csarID).get(serviceTemplateId).get(serviceTemplateInstanceId).get(correlationID);
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
	public TPlanDTO getPlanDTOForCorrelation(ServiceTemplateInstanceID instanceID, String correlationID) {

		if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(instanceID.getCsarId())) {
			if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
					.containsKey(instanceID.getServiceTemplateId())) {
				if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
						.get(instanceID.getServiceTemplateId()).containsKey(instanceID.getInstanceID())) {
					if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
							.get(instanceID.getServiceTemplateId()).get(instanceID.getInstanceID())
							.containsKey(correlationID)) {

						PlanInvocationEvent event = mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan
								.get(instanceID.getCsarId()).get(instanceID.getServiceTemplateId())
								.get(instanceID.getInstanceID()).get(correlationID);
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
		mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarid).remove(correlationID);
	}

	/**
	 * Remove CorrelationID after response of active PublicPlan is processed
	 * 
	 * @param csarid
	 * @param correlationID
	 */
	public void removeCorrelation(String correlationID) {
		for (CSARID csarID : mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.keySet()) {
			if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
					.containsKey(correlationID)) {
				mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).remove(correlationID);
			}
		}
	}

	/**
	 * Returns the list of all CorrelationIDs of a CSARInstance.
	 * 
	 * @param instanceID
	 * @return list of CorrelationIDs
	 */
	public List<String> getActiveCorrelationsOfInstance(ServiceTemplateInstanceID instanceID) {
		List<String> list = new ArrayList<String>();

		CSARID csarID = instanceID.getCsarId();
		QName stQName = instanceID.getServiceTemplateId();
		int stInstanceId = instanceID.getInstanceID();
		// int internalID = instanceID.getInternalID();

		if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(csarID)) {
			if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).containsKey(stQName)) {
				if (mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(stQName)
						.containsKey(stInstanceId)) {
					for (String corr : mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
							.get(stQName).get(stInstanceId).keySet()) {
						list.add(corr);
					}
				}
			}
		}

		return list;
	}

}
