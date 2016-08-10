package org.opentosca.core.model.deployment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.opentosca.core.model.csar.id.CSARID;

/**
 * 
 * Abstract class for the deployment information of a file inside a CSAR file.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
@MappedSuperclass
public abstract class AbstractFileDeploymentInfo extends AbstractDeploymentInfo {
	
	/**
	 * 
	 * @param csarID that uniquely identifies a CSAR file
	 * @param relPath - relative path where the file is located inside the CSAR
	 *            file
	 */
	public AbstractFileDeploymentInfo(CSARID csarID, String relPath) {
		super(csarID);
		this.relPath = relPath;
	}
	
	/**
	 * Needed by JPA.
	 */
	protected AbstractFileDeploymentInfo() {
	}
	
	
	/**
	 * Relative path where the file is located inside a CSAR file.
	 */
	@Column(name = "RelPath")
	private String relPath;
	
	/**
	 * Counts the number of deployment / undeployment attempts.<br />
	 * It will be incremented by one on every deployment / undeployment attempt.
	 * If a file is deployed and will be now undeployed it will be reseted to 0.
	 */
	@Column(name = "Attempt")
	private int attempt = 0;
	
	
	/**
	 * @return the relative path where the file is located inside a THOR file.
	 */
	public String getRelPath() {
		return this.relPath;
	}
	
	/**
	 * @return the deployment / undeployment attempt of this file
	 */
	public int getAttempt() {
		return this.attempt;
	}
	
	/**
	 * Sets the deployment / undeployment attempt of this file.<br />
	 * This method should not be called by a user of this component!
	 * 
	 * @param attempt to set
	 */
	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	
}
