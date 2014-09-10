package org.opentosca.core.model.deployment.ia;

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
import org.opentosca.core.model.deployment.AbstractFileDeploymentInfo;

/**
 * Deployment information of a Implementation Artifact inside a CSAR file.<br />
 * It is used for tracking its deploy progress.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// @formatter:off
@NamedQueries({@NamedQuery(name = IADeploymentInfo.getIADeploymentInfoByCSARIDAndRelPath, query = IADeploymentInfo.getIADeploymentInfoByCSARIDAndRelPathQuery), @NamedQuery(name = IADeploymentInfo.getIADeploymentInfoByCSARID, query = IADeploymentInfo.getIADeploymentInfoByCSARIDQuery)})
// @formatter:on
@Table(name = IADeploymentInfo.tableName)
@PrimaryKey(columns = {@Column(name = "csarID"), @Column(name = "RelPath")})
public class IADeploymentInfo extends AbstractFileDeploymentInfo {
	
	protected static final String tableName = "IADeploymentInfo";
	
	/**
	 * JPQL Queries
	 */
	public static final String getIADeploymentInfoByCSARIDAndRelPath = "IADeploymentInfo.ByCSARIDAndRelPath";
	protected static final String getIADeploymentInfoByCSARIDAndRelPathQuery = "select t from " + IADeploymentInfo.tableName + " t where t.relPath = :iaRelPath and t.csarID = :csarID";
	
	public static final String getIADeploymentInfoByCSARID = "IADeploymentInfo.ByCSARID";
	protected static final String getIADeploymentInfoByCSARIDQuery = "select t from " + IADeploymentInfo.tableName + " t where t.csarID = :csarID";
	
	/**
	 * Deployment state of this IA.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "DeploymentState")
	private IADeploymentState deploymentState;
	
	
	/**
	 * Needed by JPA.
	 */
	protected IADeploymentInfo() {
	}
	
	/**
	 * 
	 * Creates a <code>IADeploymentInfo</code>.
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param relPath - relative file path where this IA is located inside the
	 *            CSAR file
	 * @param deploymentState of this IA
	 */
	public IADeploymentInfo(CSARID csarID, String relPath, IADeploymentState deploymentState) {
		super(csarID, relPath);
		this.deploymentState = deploymentState;
	}
	
	/**
	 * @return the deployment state of this IA
	 */
	public IADeploymentState getDeploymentState() {
		return this.deploymentState;
	}
	
	/**
	 * Sets the deployment state of this IA.
	 * 
	 * @param deploymentState to set
	 */
	public void setDeploymentState(IADeploymentState deploymentState) {
		this.deploymentState = deploymentState;
	}
	
}
