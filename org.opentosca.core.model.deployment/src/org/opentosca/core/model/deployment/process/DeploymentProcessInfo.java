package org.opentosca.core.model.deployment.process;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrimaryKey;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.AbstractDeploymentInfo;

/**
 * 
 * Deployment information of a CSAR file.<br />
 * It is used for tracking its deploy progress.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// @formatter:off
@NamedQueries({@NamedQuery(name = DeploymentProcessInfo.getDeploymentProcessInfoByCSARID, query = DeploymentProcessInfo.getDeploymentProcessInfoByCSARIDQuery)})
// @formatter:on
@Table(name = DeploymentProcessInfo.tableName)
@PrimaryKey(columns = {@Column(name = "csarID")})
public class DeploymentProcessInfo extends AbstractDeploymentInfo {
	
	protected static final String tableName = "DeploymentProcessInfo";
	
	public static final String getDeploymentProcessInfoByCSARID = "DeploymentProcessInfo.ByCSARID";
	protected static final String getDeploymentProcessInfoByCSARIDQuery = "select t from " + DeploymentProcessInfo.tableName + " t where t.csarID = :csarID";
	
	@Enumerated(EnumType.STRING)
	@Column(name = "DeploymentProcessState")
	private DeploymentProcessState deploymentProcessState;
	
	
	/**
	 * Needed by JPA.
	 */
	protected DeploymentProcessInfo() {
	}
	
	/**
	 * Creates a <code>DeploymentProcessInfo</code>.
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param deploymentProcessState of this CSAR
	 */
	public DeploymentProcessInfo(CSARID csarID, DeploymentProcessState deploymentProcessState) {
		super(csarID);
		this.deploymentProcessState = deploymentProcessState;
	}
	
	/**
	 * @return the deployment process state of this CSAR
	 */
	public DeploymentProcessState getDeploymentProcessState() {
		return this.deploymentProcessState;
	}
	
	/**
	 * Sets the deployment process state of this CSAR.
	 * 
	 * @param deploymentProcessState to set
	 */
	public void setDeploymentProcessState(DeploymentProcessState deploymentProcessState) {
		this.deploymentProcessState = deploymentProcessState;
	}
	
}
