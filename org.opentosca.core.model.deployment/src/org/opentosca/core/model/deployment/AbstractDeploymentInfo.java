package org.opentosca.core.model.deployment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.util.jpa.converters.CSARIDConverter;

/**
 * Abstract class for deployment information that belongs to a CSAR file.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
@MappedSuperclass
@Converter(name = "CSARIDConverter", converterClass = CSARIDConverter.class)
public abstract class AbstractDeploymentInfo {
	
	// @EmbeddedId not possible here - in the child classes we
	// define the attributes of CSARID together with fileName as composite
	// primary key
	@Convert("CSARIDConverter")
	@Column(name = "csarID")
	private CSARID csarID;
	
	
	/**
	 * Needed by JPA.
	 */
	protected AbstractDeploymentInfo() {
	}
	
	/**
	 * @param csarID that uniquely identifies a CSAR file
	 */
	public AbstractDeploymentInfo(CSARID csarID) {
		this.csarID = csarID;
	}
	
	/**
	 * 
	 * @return CSAR ID of the CSAR.
	 */
	public CSARID getCSARID() {
		return this.csarID;
	}
	
}
