package org.opentosca.container.core.service;

import java.util.Collection;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.ia.IADeploymentInfo;
import org.opentosca.container.core.model.deployment.plan.PlanDeploymentInfo;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;

public interface DeploymentTracker {

    /**
     * Store the deployment state of a given Csar to the attached database.
     *
     * @param csar  The csar that the deployment process state applies to.
     * @param state The deployment state applying.
     */
    public void storeDeploymentState(CsarId csar, DeploymentProcessState state);

    /**
     * Retrieve the current deployment process state of a given Csar.
     *
     * @param csar The csar to retrieve deployment state for.
     * @return The deployment state of the given Csar, <tt>null</tt> if no state is available for the given Csar
     */
    public DeploymentProcessState getDeploymentState(CsarId csar);

    /**
     * Stores the given information about the encapsulated implementation artifact into the database. Previous
     * deploymentInfo will be overwritten!
     *
     * @param info The deployment information set to store about the implementation artifact.
     */
    public void storeIADeploymentInfo(IADeploymentInfo info);

    /**
     * Retrieve the stored deployment information for a specific implementation artifact identified by it's relative
     * path within the Csar and it's parent Csar.
     *
     * @param csar      The parent Csar that the implementation artifact belongs to
     * @param iaRelPath The relative path of the implementation artifact within the csar
     * @return The stored deployment information pertaining to the implementation artifact
     */
    public IADeploymentInfo getIADeploymentInfo(CsarId csar, String iaRelPath);

    /**
     * Retrieve all implementation artifact related deployment information for a given Csar.
     *
     * @param csar The csar to check for deployment information on artifacts.
     * @return All Implementation Artifact information for the given Csar
     */
    public Collection<IADeploymentInfo> getIADeployments(CsarId csar);

    /**
     * Store the given Deployment Information for a plan, overwriting any already stored information for the Plan
     * encapsulated in the Deployment Information object.
     *
     * @param info The information to store in the database
     */
    public void storePlanDeploymentInfo(PlanDeploymentInfo info);

    /**
     * Retrieves the plan deployment information stored for a given plan belonging to a given Csar.
     *
     * @param csar        The Csar that the plan deployment belongs to.
     * @param planRelPath The relative path of the Plan within the Csar.
     * @return The plan deployment information associated with the given Csar and plan, <tt>null</tt> if no such plan is
     * known.
     */
    public PlanDeploymentInfo getPlanDeploymentInfo(CsarId csar, String planRelPath);

    /**
     * Retrieves all plan deployment information stored in relation to a given csar.
     *
     * @param csar The csar who's plan deployments are to be found.
     * @return All plan deployment information associated with the given Csar
     */
    public Collection<PlanDeploymentInfo> getPlanDeployments(CsarId csar);

    /**
     * Deletes all deployment information associated to the given Csar.
     *
     * @param csar The csar to remove from deployment tracking.
     */
    public void deleteDeploymentState(CsarId csar);
}
