package org.opentosca.core.deployment.tracker.service;

import java.util.List;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.ia.IADeploymentInfo;
import org.opentosca.core.model.deployment.ia.IADeploymentState;
import org.opentosca.core.model.deployment.plan.PlanDeploymentInfo;
import org.opentosca.core.model.deployment.plan.PlanDeploymentState;
import org.opentosca.core.model.deployment.process.DeploymentProcessState;

/**
 * Interface that provides methods for storing and getting the deployment states
 * of CSAR files, IAs and Plans.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
public interface ICoreDeploymentTrackerService {
	
	/**
	 * Stores the deployment state of a CSAR file.
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param deploymentState to store
	 * @return <code>true</code> if storing was successful, otherwise
	 *         <code>false</code>
	 */
	public boolean storeDeploymentState(CSARID csarID, DeploymentProcessState deploymentState);
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 * @return the deployment state of the CSAR file; if CSAR file doesn't exist
	 *         <code>null</code>
	 */
	public DeploymentProcessState getDeploymentState(CSARID csarID);
	
	/**
	 * Stores deployment information for a Implementation Artifact. Already
	 * stored deployment information will be overwritten!
	 * 
	 * @param iaDeploymentInfo to store (contains CSARID, relative file path
	 *            where the IA is located inside the CSAR file and deployment
	 *            state of IA)
	 * @return <code>true</code> if storing was successful, otherwise
	 *         <code>false</code>
	 */
	public boolean storeIADeploymentInfo(IADeploymentInfo iaDeploymentInfo);
	
	/**
	 * Stores deployment information for a Implementation Artifact. Already
	 * stored deployment information will be overwritten!
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param iaRelPath- relative file path where the Implementation Artifact is
	 *            located inside the CSAR file
	 * @param iaDeploymentState - deployment state of the Implementation
	 *            Artifact
	 * @return <code>true</code> if storing was successful, otherwise
	 *         <code>false</code>
	 */
	public boolean storeIADeploymentInfo(CSARID csarID, String iaRelPath, IADeploymentState iaDeploymentState);
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 * @param iaRelPath - relative file path where the Implementation Artifact
	 *            is located inside the CSAR file
	 * @return if Implementation Artifact exists, its deployment information;
	 *         otherwise <code>null</code>
	 */
	public IADeploymentInfo getIADeploymentInfo(CSARID csarID, String iaRelPath);
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 * @return the deployment informations for all Implementation Artifacts of
	 *         the CSAR file.
	 */
	public List<IADeploymentInfo> getIADeploymentInfos(CSARID csarID);
	
	/**
	 * Stores the deployment information for a Plan. Already stored deployment
	 * information will be overwritten!
	 * 
	 * @param planDeploymentInfo to store (contains CSARID, relative file path
	 *            where the Plan is located inside the CSAR file and deployment
	 *            state of Plan)
	 * @return <code>true</code> if storing was successful, otherwise
	 *         <code>false</code>
	 */
	public boolean storePlanDeploymentInfo(PlanDeploymentInfo planDeploymentInfo);
	
	/**
	 * Stores deployment information for a Plan. Already stored deployment
	 * information will be overwritten!
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param planRelPath - relative file path where the Plan is located inside
	 *            the CSAR file
	 * @param planDeploymentState - deployment state of the Plan
	 * @return <code>true</code> if storing was successful, otherwise
	 *         <code>false</code>
	 */
	public boolean storePlanDeploymentInfo(CSARID csarID, String planRelPath, PlanDeploymentState planDeploymentState);
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 * @param planRelPath - relative file path where the Plan is located inside
	 *            the CSAR file
	 * @return if Plan exists, its deployment information; otherwise
	 *         <code>null</code>
	 */
	public PlanDeploymentInfo getPlanDeploymentInfo(CSARID csarID, String planRelPath);
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 * @return the deployment informations for all Plans of the CSAR file
	 */
	public List<PlanDeploymentInfo> getPlanDeploymentInfos(CSARID csarID);

	/**
	 * Deletes all deployment information for the given CSAR id
	 * @param csarID the CSAR id whose deployment state should be deleted
	 */
	public void deleteDeploymentState(CSARID csarId);
	
}
