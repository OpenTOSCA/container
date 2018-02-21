package org.opentosca.container.api.legacy.resources.csar.control;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class representing the current State of a DeploymentProcess Copyright 2012 IAAS
 * University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class DeploymentProcessStateResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentProcessStateResource.class);

    private final DeploymentProcessState deploymentState;


    public DeploymentProcessStateResource(final DeploymentProcessState deploymentState) {
        this.deploymentState = deploymentState;
        DeploymentProcessStateResource.LOG.info("{} created: {}", this.getClass(), this);

    }

    @GET
    @Produces(ResourceConstants.TEXT_PLAIN)
    public Response getDeploymentProcessState() {
        DeploymentProcessStateResource.LOG.info("Get Request on DeploymentProcessStateResource State: {}",
            this.deploymentState.toString());
        return Response.ok(this.deploymentState.toString()).build();
    }
}
