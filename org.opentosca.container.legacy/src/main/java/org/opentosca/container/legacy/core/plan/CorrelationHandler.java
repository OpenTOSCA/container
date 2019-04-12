package org.opentosca.container.legacy.core.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This class manages active PublicPlans which are still running or response is not processed yet.
 */
@Deprecated
@Service
@Singleton
public class CorrelationHandler {

  long lastMilli = 0;
  long lastCounter = 0;

  // TODO make persistent, fix JPA
  // CSARID to CorrelationID to Invocation Event
  private final Map<CSARID, Map<QName, Map<Integer, Map<String, PlanInvocationEvent>>>> mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan =
    new HashMap<>();
  private final Map<String, Integer> mapCorrIdToFakedServiceTemplateInstanceId = new HashMap<>();

  private final Logger LOG = LoggerFactory.getLogger(CorrelationHandler.class);


  /**
   * Synchronized method for creating a new CorrelationID for a PublicPlan.
   *
   * @return CorrelationID
   */
  public synchronized String getNewCorrelationID(final CSARID csarID, final QName serviceTemplateId,
                                                 final int serviceTemplateInstanceId, final PlanInvocationEvent event,
                                                 final boolean isBuildPlan) {

    final long time = System.currentTimeMillis();

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
    final String corrID = this.lastMilli + "-" + this.lastCounter;

    if (!this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(csarID)) {
      this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.put(csarID,
        new HashMap<QName, Map<Integer, Map<String, PlanInvocationEvent>>>());
    }
    if (!this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
      .containsKey(serviceTemplateId)) {
      this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
        .put(serviceTemplateId,
          new HashMap<Integer, Map<String, PlanInvocationEvent>>());
    }
    if (!this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
      .containsKey(serviceTemplateInstanceId)) {
      this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
        .put(serviceTemplateInstanceId,
          new HashMap<String, PlanInvocationEvent>());
    }
    this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
      .get(serviceTemplateInstanceId)
      .put(corrID, event);

    if (isBuildPlan) {
      this.mapCorrIdToFakedServiceTemplateInstanceId.put(corrID, serviceTemplateInstanceId);
    }

    return corrID;
  }

  public synchronized void correlateBuildPlanCorrToServiceTemplateInstanceId(final CSARID csarID,
                                                                             final QName serviceTemplateId,
                                                                             final String corrId,
                                                                             final int correctSTInstanceId) {

    final Integer falseSTInstanceId = this.mapCorrIdToFakedServiceTemplateInstanceId.get(corrId);

    if (falseSTInstanceId != null) {
      final Map<String, PlanInvocationEvent> map =
        this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
          .get(serviceTemplateId)
          .remove(falseSTInstanceId);
      this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(serviceTemplateId)
        .put(correctSTInstanceId, map);
    }

  }

  /**
   * Returns an active PublicPlan for CorrelationID.
   *
   * @param correlationID
   * @return PublicPlan
   */
  public TPlanDTO getPublicPlanForCorrelation(final String correlationID) {
    for (final CSARID csarID : this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.keySet()) {
      for (final QName serviceTemplateId : this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
        .keySet()) {
        for (final Integer serviceTemplateInstanceId : this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
          .get(serviceTemplateId)
          .keySet()) {

          if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
            .get(serviceTemplateId)
            .get(serviceTemplateInstanceId)
            .containsKey(correlationID)) {

            final PlanInvocationEvent event =
              this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
                .get(serviceTemplateId)
                .get(serviceTemplateInstanceId)
                .get(correlationID);
            final TPlanDTO plan = new TPlanDTO();
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
  public TPlanDTO getPlanDTOForCorrelation(final ServiceTemplateInstanceID instanceID, final String correlationID) {

    if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(instanceID.getCsarId())) {
      if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
        .containsKey(instanceID.getServiceTemplateId())) {
        if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
          .get(instanceID.getServiceTemplateId())
          .containsKey(instanceID.getInstanceID())) {
          if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
            .get(instanceID.getServiceTemplateId())
            .get(instanceID.getInstanceID())
            .containsKey(correlationID)) {

            final PlanInvocationEvent event =
              this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(instanceID.getCsarId())
                .get(instanceID.getServiceTemplateId())
                .get(instanceID.getInstanceID())
                .get(correlationID);
            final TPlanDTO plan = new TPlanDTO();
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

    this.LOG.error("There is no entry for the CorrelationID \"" + correlationID + "\".");
    return null;

  }

  /**
   * Remove CorrelationID after response of active PublicPlan is processed
   *
   * @param csarid
   * @param correlationID
   */
  public void removeCorrelation(final CSARID csarid, final String correlationID) {
    this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarid).remove(correlationID);
  }

  /**
   * Remove CorrelationID after response of active PublicPlan is processed
   *
   * @param csarid
   * @param correlationID
   */
  public void removeCorrelation(final String correlationID) {
    for (final CSARID csarID : this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.keySet()) {
      if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
        .containsKey(correlationID)) {
        this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
          .remove(correlationID);
      }
    }
  }

  /**
   * Returns the list of all CorrelationIDs of a CSARInstance.
   *
   * @param instanceID
   * @return list of CorrelationIDs
   */
  public List<String> getActiveCorrelationsOfInstance(final ServiceTemplateInstanceID instanceID) {
    final List<String> list = new ArrayList<>();

    final CSARID csarID = instanceID.getCsarId();
    final QName stQName = instanceID.getServiceTemplateId();
    final int stInstanceId = instanceID.getInstanceID();
    // int internalID = instanceID.getInternalID();

    if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.containsKey(csarID)) {
      if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
        .containsKey(stQName)) {
        if (this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID).get(stQName)
          .containsKey(stInstanceId)) {
          for (final String corr : this.mapCsarIdToServiceTemplateIdToSTInstanceIdToCorrelationToPublicPlan.get(csarID)
            .get(stQName)
            .get(stInstanceId)
            .keySet()) {
            list.add(corr);
          }
        }
      }
    }

    return list;
  }

}
