package org.opentosca.container.engine.plan.plugin;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;

/**
 * This is a subinterface of
 * {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePluginService} and specifies
 * handling of PlanReference elements inside a Plan element specified in Topology and Orchestration
 * Specification for Cloud Applications Version 1.0 Chapter 11: Plans.
 *
 * The plugin musn't resolve the {@link org.opentosca.model.tosca.TPlan.PlanModelReference}, a
 * service implementing {@link org.opentosca.core.file.service.ICoreFileService} should be called
 * for the raw data.
 */
public interface IPlanEnginePlanRefPluginService extends IPlanEnginePluginService {

    /**
     * <p>
     * Method allows deployment of PlanModelReferences.
     * </p>
     * <p>
     * The reference must be resolved in a service implementing
     * {@link org.opentosca.core.file.service.ICoreFileService}. In addition a service of
     * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must provide a suitable
     * endpoint.
     * </p>
     *
     *
     * @param planRef the PlanReference element under a Plan element of a ServiceTemplate Definition
     * @param csarId the identifier of the CSAR the PlanReference element belongs to
     * @return true if deployment was successful, else false
     */
    public boolean deployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId);

    /**
     * <p>
     * Method allows undeployment of PlanModelReferences.
     * </p>
     * <p>
     * The reference must be resolved in a service implementing
     * {@link org.opentosca.core.file.service.ICoreFileService}. In addition a service of
     * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must provide a suitable
     * endpoint.
     * </p>
     *
     *
     * @param planRef the PlanReference element under a Plan element of a ServiceTemplate Definition
     * @param csarId the identifier of the CSAR the PlanReference element belongs to
     * @return true if undeployment was successful, else false
     */
    public boolean undeployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId);

}
