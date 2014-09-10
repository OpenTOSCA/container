package org.opentosca.containerapi.resources.csar.control.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.core.model.deployment.process.DeploymentProcessOperation;

/**
 * Class to provide JAXB-Annotation for a DeploymentProcessOperation <br>
 * <br>
 * 
 * To be used by Jersey to automatically generate XML-Responses<br>
 * <br>
 * 
 * 
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationJaxb {
	
	@XmlElement(name = "Operation")
	private DeploymentProcessOperation operation;
	
	
	public OperationJaxb() {
	}
	
	public DeploymentProcessOperation getOperation() {
		return this.operation;
	}
	
	public void setOperation(DeploymentProcessOperation operation) {
		this.operation = operation;
	}
}
