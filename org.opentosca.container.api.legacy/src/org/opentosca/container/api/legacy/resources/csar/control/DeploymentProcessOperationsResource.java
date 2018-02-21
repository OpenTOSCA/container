package org.opentosca.container.api.legacy.resources.csar.control;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class representing the available Methods of a DeploymentProcess Copyright 2012 IAAS
 * University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class DeploymentProcessOperationsResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentProcessOperationsResource.class);
    private final Set<DeploymentProcessOperation> operations;
    private final String sep = "&";


    public DeploymentProcessOperationsResource(final Set<DeploymentProcessOperation> operations) {
        this.operations = operations;
        DeploymentProcessOperationsResource.LOG.info("{} created: {}", this.getClass(), this);

    }

    /**
     *
     * @return all available Operations as String separated by "&"
     */
    @GET
    @Produces(ResourceConstants.TEXT_PLAIN)
    public Response getOperationss() {
        DeploymentProcessOperationsResource.LOG.info("Get Request on DeploymentProcessOperationsResource");
        String operations = "";
        for (final DeploymentProcessOperation operation : this.operations) {
            operations = operations + this.sep + operation.toString();
        }
        return Response.ok(operations).build();
    }

}
