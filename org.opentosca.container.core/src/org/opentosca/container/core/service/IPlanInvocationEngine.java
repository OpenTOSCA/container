package org.opentosca.container.core.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.tosca.extension.TPlanDTO;

/**
 * Interface of the PlanInvocationEngine. This service provides a the functionality of invoking
 * PublicPlans, getting a list of CorrelationIDs of active PublicPlans and one specific PublicPlan.
 */
public interface IPlanInvocationEngine {

    /**
     * Invoke a PublicPlan for a CSAR. If this PublicPlan is of Type OTHERMANAGEMENT or TERMINATION, the
     * information about the CSARInstance is stored inside the PublicPlan.
     *
     * @param csarID
     * @param instance ID of a CSAR instance
     * @param publicPlan
     * @return boolean about success
     * @throws UnsupportedEncodingException
     */
    public String invokePlan(CSARID csarID, QName serviceTemplateId, int csarInstanceID, TPlanDTO plan)
        throws UnsupportedEncodingException;

    public void correctCorrelationToServiceTemplateInstanceIdMapping(CSARID csarID, QName serviceTemplateId,
                    String corrId, int correctSTInstanceId);

    /**
     * Returns a list of CorrelationIDs of activce PublicPlans of a CSARInstance.
     *
     * @param csarInstanceID
     * @return list of CorrelationIDs of active PublicPlans
     */
    public List<String> getActiveCorrelationsOfInstance(ServiceTemplateInstanceID csarInstanceID);

    /**
     * Returns a specific active PublicPlan.
     *
     * @param csarInstanceID
     * @param correlationID
     * @return PublicPlan
     */
    public TPlanDTO getActivePublicPlanOfInstance(ServiceTemplateInstanceID csarInstanceID, String correlationID);

    public IPlanLogHandler getPlanLogHandler();

}
