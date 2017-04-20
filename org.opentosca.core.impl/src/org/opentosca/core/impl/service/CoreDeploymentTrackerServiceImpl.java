package org.opentosca.core.impl.service;

import java.util.List;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.ia.IADeploymentInfo;
import org.opentosca.core.model.deployment.ia.IADeploymentState;
import org.opentosca.core.model.deployment.plan.PlanDeploymentInfo;
import org.opentosca.core.model.deployment.plan.PlanDeploymentState;
import org.opentosca.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.core.service.ICoreDeploymentTrackerService;
import org.opentosca.core.service.internal.ICoreInternalDeploymentTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation currently acts as a Proxy to the Core Internal Deployment
 * Tracker Service. It can in future be used to modify the incoming parameters
 * to fit another back end interface / implementation.
 *
 * @see ICoreInternalDeploymentTrackerService
 */
public class CoreDeploymentTrackerServiceImpl implements ICoreDeploymentTrackerService {

	ICoreInternalDeploymentTrackerService deploymentTrackerService;

	final private static Logger LOG = LoggerFactory.getLogger(CoreDeploymentTrackerServiceImpl.class);


	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public boolean storeDeploymentState(final CSARID csarID, final DeploymentProcessState deploymentState) {
		return this.deploymentTrackerService.storeDeploymentState(csarID, deploymentState);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public DeploymentProcessState getDeploymentState(final CSARID csarID) {
		return this.deploymentTrackerService.getDeploymentState(csarID);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public boolean storeIADeploymentInfo(final IADeploymentInfo iaDeploymentInfo) {
		return this.deploymentTrackerService.storeIADeploymentInfo(iaDeploymentInfo);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public IADeploymentInfo getIADeploymentInfo(final CSARID csarID, final String iaRelPath) {
		return this.deploymentTrackerService.getIADeploymentInfo(csarID, iaRelPath);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public List<IADeploymentInfo> getIADeploymentInfos(final CSARID csarID) {
		return this.deploymentTrackerService.getIADeploymentInfos(csarID);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public boolean storePlanDeploymentInfo(final PlanDeploymentInfo planDeploymentInfo) {
		return this.deploymentTrackerService.storePlanDeploymentInfo(planDeploymentInfo);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public PlanDeploymentInfo getPlanDeploymentInfo(final CSARID csarID, final String planRelPath) {
		return this.deploymentTrackerService.getPlanDeploymentInfo(csarID, planRelPath);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public List<PlanDeploymentInfo> getPlanDeploymentInfos(final CSARID csarID) {
		return this.deploymentTrackerService.getPlanDeploymentInfos(csarID);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public boolean storeIADeploymentInfo(final CSARID csarID, final String iaRelPath, final IADeploymentState iaDeploymentState) {
		return this.deploymentTrackerService.storeIADeploymentInfo(csarID, iaRelPath, iaDeploymentState);
	}

	@Override
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy.
	 */
	public boolean storePlanDeploymentInfo(final CSARID csarID, final String planRelPath, final PlanDeploymentState planDeploymentState) {
		return this.deploymentTrackerService.storePlanDeploymentInfo(csarID, planRelPath, planDeploymentState);
	}

	@Override
	public void deleteDeploymentState(final CSARID csarId) {
		this.deploymentTrackerService.deleteDeploymentState(csarId);
	}

	/**
	 * Binds the Core Internal Deployment Tracker.
	 *
	 * @param deploymentTrackerService to bind
	 */
	public void bindCoreInternalDeploymentTrackerService(final ICoreInternalDeploymentTrackerService deploymentTrackerService) {
		if (deploymentTrackerService == null) {
			CoreDeploymentTrackerServiceImpl.LOG.error("Can't bind Core Internal Deployment Tracker Service.");
		} else {
			this.deploymentTrackerService = deploymentTrackerService;
			CoreDeploymentTrackerServiceImpl.LOG.debug("Core Internal Deployment Tracker Service bound.");
		}

	}

	/**
	 * Unbinds the Core Internal Deployment Tracker.
	 *
	 * @param deploymentTrackerService to unbind
	 */
	public void unbindCoreInternalDeploymentTrackerService(final ICoreInternalDeploymentTrackerService deploymentTrackerService) {
		this.deploymentTrackerService = null;
		CoreDeploymentTrackerServiceImpl.LOG.debug("Core Internal Deployment Tracker Service unbound.");
	}

}
