package org.opentosca.container.api.legacy.resources.csar.control.jaxb;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;

/**
 * Class to provide JAXB-Annotation for a DeploymentProcess <br>
 * <br>
 *
 * To be used by Jersey to automatically generate XML-Responses<br>
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
@XmlRootElement(name = "DeploymentProcess")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentProcessJaxb {

	@XmlAttribute(name = "DeploymentState")
	private DeploymentProcessState deploymentState;
	@XmlElement(name = "ProcessId")
	private CSARID processID;
	@XmlElement(name = "Operations")
	private Set<OperationJaxb> operations;


	public DeploymentProcessJaxb() {
	}

	/**
	 * @return the deploymentState
	 */
	public DeploymentProcessState getDeploymentState() {
		return this.deploymentState;
	}

	/**
	 * @param deploymentState the deploymentState to set
	 */
	public void setDeploymentState(final DeploymentProcessState deploymentState) {
		this.deploymentState = deploymentState;
	}

	/**
	 * @return the CSAR ID
	 */
	public CSARID getProcessID() {
		return this.processID;
	}

	/**
	 * @param csarID to set
	 */
	public void setProcessID(final CSARID csarID) {
		this.processID = csarID;
	}

	/**
	 * @return the operations
	 */
	public Set<OperationJaxb> getOperations() {
		if (this.operations == null) {
			return new HashSet<>();
		} else {
			return this.operations;
		}
	}

	/**
	 * @param operations the operations to set
	 */
	public void setOperations(final Set<OperationJaxb> operations) {
		this.operations = operations;
	}

}
