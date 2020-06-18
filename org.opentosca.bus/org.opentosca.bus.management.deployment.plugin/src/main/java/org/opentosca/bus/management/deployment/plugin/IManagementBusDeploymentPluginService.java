package org.opentosca.bus.management.deployment.plugin;

import java.util.List;

import org.apache.camel.Exchange;
import org.opentosca.bus.management.header.MBHeader;

/**
 * Interface of the Management Bus Deployment Plug-ins.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The interface specifies four methods. One for invoking the deployment of an Implementation Artifact, another for
 * invoking the undeployment of a previously deployed Implementation Artifact and two methods that return the supported
 * deployment types and the capabilities of the specific plug-in.
 */
public interface IManagementBusDeploymentPluginService {

    /**
     * Invokes the deployment of an Implementation Artifact.
     *
     * @param exchange contains all needed information like the NodeTypeImplementation the ArtifactReferences to the
     *                 files that have to be deployed and the "ServiceEndpoint" property if it is defined.
     * @return the endpoint of the deployed Implementation Artifact as header field (see {@link MBHeader#ENDPOINT_URI})
     * of the exchange message or null if the deployment failed.
     */
    public Exchange invokeImplementationArtifactDeployment(Exchange exchange);

    /**
     * Invokes the undeployment of an Implementation Artifact.
     *
     * @param exchange contains all needed information like the endpoint of the deployed Implementation Artifact.
     * @return the result of the undeployment process as header field (see {@link MBHeader#OPERATIONSTATE_BOOLEAN}) of
     * the exchange message.
     */
    public Exchange invokeImplementationArtifactUndeployment(Exchange exchange);

    /**
     * Returns the supported deployment-types of the plug-in.
     *
     * @return list of strings each representing one supported deployment type of the plug-in.
     */
    public List<String> getSupportedTypes();

    /**
     * Returns the provided capabilities of the plug-in.
     *
     * @return list of strings each representing one capability of the plug-in.
     */
    public List<String> getCapabilties();
}
