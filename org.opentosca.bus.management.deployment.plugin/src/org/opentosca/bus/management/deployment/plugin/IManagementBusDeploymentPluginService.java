package org.opentosca.bus.management.deployment.plugin;

import java.util.List;

import org.apache.camel.Exchange;

/**
 * Interface of the Management Bus Deployment Plug-ins.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * The interface specifies three methods. One for invoking the deployment of an Implementation
 * Artifact, another for invoking the undeployment of a previously deployed Implementation Artifact
 * and one method that returns the supported deployment types of the specific plugin.
 *
 *
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public interface IManagementBusDeploymentPluginService {

    /**
     * Invokes the deployment of an Implementation Artifact.
     *
     * @param exchange contains all needed information like the NodeTypeImplementation the
     *        ArtifactReferences to the files that have to be deployed and the "ServiceEndpoint"
     *        property.
     *
     * @return the result of the deployment process as body of the exchange message.
     *
     */
    public Exchange invokeImplementationArtifactDeployment(Exchange exchange);

    /**
     * Invokes the undeployment of an Implementation Artifact.
     *
     * @param exchange contains all needed information like the NodeTypeImplementation and the path
     *        where the Implementation Artifact was deployed.
     *
     * @return the result of the undeployment process as body of the exchange message.
     *
     */
    public Exchange invokeImplementationArtifactUndeployment(Exchange exchange);

    /**
     * Returns the supported deployment-types of the plug-in.
     *
     */
    public List<String> getSupportedTypes();

}
