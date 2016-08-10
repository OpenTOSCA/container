package org.opentosca.core.model.deployment.plan;

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
 * Deployment information of a Plan inside a CSAR file.<br />
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
@NamedQueries({@NamedQuery(name = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDAndRelPath, query = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDAndRelPathQuery), @NamedQuery(name = PlanDeploymentInfo.getPlanDeploymentInfoByCSARID, query = PlanDeploymentInfo.getPlanDeploymentInfoByCSARIDQuery)})
// @formatter:on
@Table(name = PlanDeploymentInfo.tableName)
@PrimaryKey(columns = {@Column(name = "csarID"), @Column(name = "RelPath")})
public class PlanDeploymentInfo extends AbstractFileDeploymentInfo {
	
	protected final static String tableName = "PlanDeploymentInfo";
	
	/**
	 * JPQL Queries
	 */
	public static final String getPlanDeploymentInfoByCSARIDAndRelPath = "PlanDeploymentInfo.ByCSARIDAndRelPath";
	protected static final String getPlanDeploymentInfoByCSARIDAndRelPathQuery = "select t from " + PlanDeploymentInfo.tableName + " t where t.relPath = :planRelPath and t.csarID = :csarID";
	
	public static final String getPlanDeploymentInfoByCSARID = "PlanDeploymentInfo.ByCSARID";
	protected static final String getPlanDeploymentInfoByCSARIDQuery = "select t from " + PlanDeploymentInfo.tableName + " t where t.csarID = :csarID";
	
	/**
	 * Deployment state of this Plan.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "DeploymentState")
	private PlanDeploymentState deploymentState;
	
	
	/**
	 * Needed by JPA.
	 */
	protected PlanDeploymentInfo() {
	}
	
	/**
	 * Creates a <code>PlanDeploymentInfo</code>.
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param relPath - relative file path where this Plan is located inside the
	 *            CSAR file
	 * @param deploymentState of this Plan
	 */
	public PlanDeploymentInfo(CSARID csarID, String relPath, PlanDeploymentState deploymentState) {
		super(csarID, relPath);
		this.deploymentState = deploymentState;
	}
	
	/**
	 * @return the deployment state of this Plan
	 */
	public PlanDeploymentState getDeploymentState() {
		return this.deploymentState;
	}
	
	/**
	 * Sets the deployment state of this Plan.
	 * 
	 * @param deploymentState to set
	 */
	public void setDeploymentState(PlanDeploymentState deploymentState) {
		this.deploymentState = deploymentState;
	}
	
}
