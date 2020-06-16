package org.opentosca.container.engine.plan.plugin;

import org.eclipse.winery.model.tosca.TPlan.PlanModel;
import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * This is a subinterface of {@link org.opentosca.container.engine.plan.plugin.IPlanEnginePluginService} and specifies
 * handling of PlanModel elements inside a Plan element specified in Topology and Orchestration Specification for Cloud
 * Applications Version 1.0 Chapter 11: Plans.
 * <p>
 * A PlanModel element declares a Plan which is directly written inside ServiceTemplate, for example a bash script. This
 * means the plugin must deploy the script on a appropiate system capable of executing it.
 */
public interface IPlanEnginePlanModelPluginService extends IPlanEnginePluginService {

    /**
     * <p>
     * Method allows deployment of PlanModels
     * </p>
     * <p>
     * In addition a service implementing {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must provide
     * a suitable endpoint.
     * </p>
     *
     * @param planModel the PlanModel element inside a Plan element of a ServiceTemplate Definition
     * @param csarId    the identifier of the CSAR this PlanModel element belongs to
     * @return true if deployment was successful, else false
     */
    public boolean deployPlan(PlanModel planModel, CSARID csarId);

    /**
     * <p>
     * Method allows undeployment of PlanModels
     * </p>
     * <p>
     * In addition a service implementing {@link org.opentosca.core.endpoint.service.ICoreEndpointService} must provide
     * a suitable endpoint.
     * </p>
     *
     * @param planModel the PlanModel element inside a Plan element of a ServiceTemplate Definition
     * @param csarId    the identifier of the CSAR this PlanModel element belongs to
     * @return true if undeployment was successful, else false
     */
    public boolean undeployPlan(PlanModel planModel, CSARID csarId);
}
