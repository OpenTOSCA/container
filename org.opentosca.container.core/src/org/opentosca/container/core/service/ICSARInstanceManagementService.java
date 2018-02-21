package org.opentosca.container.core.service;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;

/**
 * Interface of the CSARInstance management and History.
 */
public interface ICSARInstanceManagementService {

    /**
     * Returns a list of instances of a CSAR.
     *
     * @param csarID the CSAR ID
     * @return List of ICSARInstanceID or empty list
     */
    public List<ServiceTemplateInstanceID> getInstancesOfCSAR(CSARID csarID);

    /**
     * Creates a new instance for a certain CSAR.
     *
     * @param csarID the certain CSAR
     * @return the ID of the new instance
     */
    public ServiceTemplateInstanceID createNewInstance(CSARID csarID, QName serviceTemplateId);

    /**
     * Deletes a CSARInstance
     *
     * @param csarID of CSARInstance
     * @param instanceID ID of the CSARInstance
     *
     * @return boolean for success
     */
    public boolean deleteInstance(CSARID csarID, ServiceTemplateInstanceID instanceID);

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
    public void storeCorrelationForAnInstance(CSARID csarID, ServiceTemplateInstanceID instanceID,
                    String correlationID);

    /**
     * Returns all CorrelationIDs of PublicPlans mapped to a specific CSARInstance.
     *
     * @param csarID
     * @param instanceID
     * @return list of CorrelationIDs
     */
    public List<String> getCorrelationsOfInstance(CSARID csarID, ServiceTemplateInstanceID instanceID);

    public ServiceTemplateInstanceID getInstanceForCorrelation(String correlationID);

    public void correlateCSARInstanceWithPlanInstance(ServiceTemplateInstanceID instanceID, String correlationID);

    public Map<String, String> getOutputForCorrelation(String correlationID);

    void setCorrelationAsActive(CSARID csarID, String correlation);

    void setCorrelationAsFinished(CSARID csarID, String correlation);

    List<String> getActiveCorrelations(CSARID csarID);

    List<String> getFinishedCorrelations(CSARID csarID);

    public void correlateCorrelationIdToPlan(String correlationID, PlanInvocationEvent planEvent);

    PlanInvocationEvent getPlanForCorrelationId(String correlationId);
}
