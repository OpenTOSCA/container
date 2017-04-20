package org.opentosca.core.model.deployment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.csar.id.CSARIDConverter;

/**
 * Abstract class for deployment information that belongs to a CSAR file.
 */
@MappedSuperclass
@Converter(name = "CSARIDConverter", converterClass = CSARIDConverter.class)
public abstract class AbstractDeploymentInfo {
	
	// TODO: Rename property to csarId
	@Convert("CSARIDConverter")
	@Column(name = "csarID")
	private CSARID csarID;


	protected AbstractDeploymentInfo() {
		
	}

	/**
	 * @param csarID that uniquely identifies a CSAR file
	 */
	public AbstractDeploymentInfo(final CSARID csarID) {
		this.csarID = csarID;
	}

	public CSARID getCsarID() {
		return this.csarID;
	}

	public void setCsarID(final CSARID csarID) {
		this.csarID = csarID;
	}

	// TODO: Remove this method
	public CSARID getCSARID() {
		return this.csarID;
	}
}
