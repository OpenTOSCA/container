package org.opentosca.core.deployment.tracker.service.impl;

import java.util.List;

import org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService;
import org.opentosca.core.internal.deployment.tracker.service.ICoreInternalDeploymentTrackerService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.ia.IADeploymentInfo;
import org.opentosca.core.model.deployment.ia.IADeploymentState;
import org.opentosca.core.model.deployment.plan.PlanDeploymentInfo;
import org.opentosca.core.model.deployment.plan.PlanDeploymentState;
import org.opentosca.core.model.deployment.process.DeploymentProcessState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 * 
 * This implementation currently acts as a Proxy to the Core Internal Deployment
 * Tracker Service. It can in future be used to modify the incoming parameters
 * to fit another back end interface / implementation.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @see ICoreInternalDeploymentTrackerService
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
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
	public boolean storeDeploymentState(CSARID csarID, DeploymentProcessState deploymentState) {
		return this.deploymentTrackerService.storeDeploymentState(csarID, deploymentState);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public DeploymentProcessState getDeploymentState(CSARID csarID) {
		return this.deploymentTrackerService.getDeploymentState(csarID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public boolean storeIADeploymentInfo(IADeploymentInfo iaDeploymentInfo) {
		return this.deploymentTrackerService.storeIADeploymentInfo(iaDeploymentInfo);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public IADeploymentInfo getIADeploymentInfo(CSARID csarID, String iaRelPath) {
		return this.deploymentTrackerService.getIADeploymentInfo(csarID, iaRelPath);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public List<IADeploymentInfo> getIADeploymentInfos(CSARID csarID) {
		return this.deploymentTrackerService.getIADeploymentInfos(csarID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public boolean storePlanDeploymentInfo(PlanDeploymentInfo planDeploymentInfo) {
		return this.deploymentTrackerService.storePlanDeploymentInfo(planDeploymentInfo);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public PlanDeploymentInfo getPlanDeploymentInfo(CSARID csarID, String planRelPath) {
		return this.deploymentTrackerService.getPlanDeploymentInfo(csarID, planRelPath);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public List<PlanDeploymentInfo> getPlanDeploymentInfos(CSARID csarID) {
		return this.deploymentTrackerService.getPlanDeploymentInfos(csarID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public boolean storeIADeploymentInfo(CSARID csarID, String iaRelPath, IADeploymentState iaDeploymentState) {
		return this.deploymentTrackerService.storeIADeploymentInfo(csarID, iaRelPath, iaDeploymentState);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * 
	 * This currently acts as a proxy.
	 */
	public boolean storePlanDeploymentInfo(CSARID csarID, String planRelPath, PlanDeploymentState planDeploymentState) {
		return this.deploymentTrackerService.storePlanDeploymentInfo(csarID, planRelPath, planDeploymentState);
	}
	
	@Override
	public void deleteDeploymentState(CSARID csarId) {
		this.deploymentTrackerService.deleteDeploymentState(csarId);
	}
	
	/**
	 * Binds the Core Internal Deployment Tracker.
	 * 
	 * @param deploymentTrackerService to bind
	 */
	public void bindCoreInternalDeploymentTrackerService(ICoreInternalDeploymentTrackerService deploymentTrackerService) {
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
	public void unbindCoreInternalDeploymentTrackerService(ICoreInternalDeploymentTrackerService deploymentTrackerService) {
		this.deploymentTrackerService = null;
		CoreDeploymentTrackerServiceImpl.LOG.debug("Core Internal Deployment Tracker Service unbound.");
	}
	
}
