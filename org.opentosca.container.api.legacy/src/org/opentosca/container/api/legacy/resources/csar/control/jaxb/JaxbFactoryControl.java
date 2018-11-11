package org.opentosca.container.api.legacy.resources.csar.control.jaxb;

import static org.opentosca.container.api.legacy.osgi.servicegetter.OpenToscaControlServiceHandler.getOpenToscaControlService;

import java.util.HashSet;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides static methods to create instances of Classes provided by this package. <br>
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class JaxbFactoryControl {

    private static Logger LOG = LoggerFactory.getLogger(JaxbFactoryControl.class);

    public static DeploymentProcessJaxb createDeploymentProcessJaxb(final CSARID processID) {
        CsarId bridge = new CsarId(processID);
        JaxbFactoryControl.LOG.info("Creating a new DeploymentProcessJaxb object for id: {}", processID);
        final DeploymentProcessJaxb jaxbObject = new DeploymentProcessJaxb();
        jaxbObject.setProcessID(processID);
        jaxbObject.setDeploymentState(getOpenToscaControlService().currentDeploymentProcessState(bridge));
        final HashSet<OperationJaxb> operations = new HashSet<>();
        for (final DeploymentProcessOperation op : getOpenToscaControlService().executableDeploymentProcessOperations(bridge)) {
            operations.add(JaxbFactoryControl.createOperationJaxb(op));
        }
        jaxbObject.setOperations(operations);
        return jaxbObject;
    }

    public static OperationJaxb createOperationJaxb(final DeploymentProcessOperation operation) {
        JaxbFactoryControl.LOG.info("Creating a new OperationJaxb object for id: {}", operation);
        final OperationJaxb jaxbObject = new OperationJaxb();
        jaxbObject.setOperation(operation);
        return jaxbObject;
    }
}
