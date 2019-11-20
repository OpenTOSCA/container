package org.opentosca.container.core.service;

import java.io.UnsupportedEncodingException;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.TPlanDTO;

/**
 * Interface of the PlanInvocationEngine. This service provides a the functionality of invoking
 * PublicPlans, getting a list of CorrelationIDs of active PublicPlans and one specific PublicPlan.
 */
public interface IPlanInvocationEngine {


    public String createCorrelationId();

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
    public void invokePlan(CSARID csarID, QName serviceTemplateId, long serviceTemplateInstanceID, TPlanDTO plan,
                           String correlationID) throws UnsupportedEncodingException;
}
