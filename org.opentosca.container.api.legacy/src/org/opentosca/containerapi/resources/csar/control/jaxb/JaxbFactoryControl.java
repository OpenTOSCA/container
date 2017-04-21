package org.opentosca.containerapi.resources.csar.control.jaxb;

import java.util.HashSet;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.opentoscacontrol.service.IOpenToscaControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides static methods to create instances of Classes provided by this
 * package. <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public class JaxbFactoryControl {
	
	private static Logger LOG = LoggerFactory.getLogger(JaxbFactoryControl.class);
	
	private static IOpenToscaControlService openToscaControl = IOpenToscaControlServiceHandler.getOpenToscaControlService();
	
	
	public static DeploymentProcessJaxb createDeploymentProcessJaxb(CSARID processID) {
		JaxbFactoryControl.LOG.info("Creating a new DeploymentProcessJaxb object for id: {}", processID);
		DeploymentProcessJaxb jaxbObject = new DeploymentProcessJaxb();
		jaxbObject.setProcessID(processID);
		jaxbObject.setDeploymentState(JaxbFactoryControl.openToscaControl.getDeploymentProcessState(processID));
		HashSet<OperationJaxb> operations = new HashSet<OperationJaxb>();
		for (DeploymentProcessOperation op : JaxbFactoryControl.openToscaControl.getExecutableDeploymentProcessOperations(processID)) {
			operations.add(JaxbFactoryControl.createOperationJaxb(op));
		}
		jaxbObject.setOperations(operations);
		return jaxbObject;
	}
	
	public static OperationJaxb createOperationJaxb(DeploymentProcessOperation operation) {
		JaxbFactoryControl.LOG.info("Creating a new OperationJaxb object for id: {}", operation);
		OperationJaxb jaxbObject = new OperationJaxb();
		jaxbObject.setOperation(operation);
		return jaxbObject;
	}
}
