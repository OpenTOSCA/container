package org.opentosca.container.api.legacy.resources.csar.control;

import static org.opentosca.container.api.legacy.osgi.servicegetter.IOpenToscaControlServiceHandler.getOpenToscaControlService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.csar.control.jaxb.DeploymentProcessJaxb;
import org.opentosca.container.api.legacy.resources.csar.control.jaxb.JaxbFactoryControl;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource class representing a DeploymentProcess
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 *
 */
public class DeploymentProcessResource {
    private static Logger LOG = LoggerFactory.getLogger(DeploymentProcessResource.class);

    private final CSARID processID;
    private QName serviceTemplateId;

    private final String sep = "&";
    public DeploymentProcessResource(final CSARID processID) {
        this.processID = processID;
        DeploymentProcessResource.LOG.info("{} created: {}", this.getClass(), this);
    }

    /**
     * @return String with Information about the DeploymentProcess separated by "&"
     */
    @GET
    @Produces(ResourceConstants.TEXT_PLAIN)
    public Response getDeploymentProcess() {
        DeploymentProcessResource.LOG.info("Get Request on DeploymentProcessResource with ID: {}", this.processID);
        String res = "DeploymentProcessID=" + this.processID + this.sep + "DeploymentState="
            + getOpenToscaControlService().getDeploymentProcessState(this.processID);
        for (final DeploymentProcessOperation operation : getOpenToscaControlService().getExecutableDeploymentProcessOperations(this.processID)) {
            final String o = "OPERATION=" + operation.toString();
            res = res + this.sep + o;
        }
        return Response.ok(res.trim()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public DeploymentProcessJaxb getDeploymentProcessXML() {
        DeploymentProcessResource.LOG.info("Get Request on DeploymentProcessResource with ID: {}", this.processID);
        return JaxbFactoryControl.createDeploymentProcessJaxb(this.processID);
    }

    @POST
    @Consumes(ResourceConstants.TEXT_PLAIN)
    public Response invoke(final String input) {
        final String params[] = input.split("&");
        final DeploymentProcessOperation operation = DeploymentProcessOperation.valueOf(params[0]);
        final CSARID csarID = this.processID;
        DeploymentProcessResource.LOG.info("POST method called on DeploymentProcess: {}, Parameter: {}",
                                           csarID.toString(), operation);
        if (getOpenToscaControlService().getExecutableDeploymentProcessOperations(this.processID).contains(operation)) {
            switch (operation) {
                case PROCESS_TOSCA:
                    DeploymentProcessResource.LOG.info("OPERATION: {}", DeploymentProcessOperation.PROCESS_TOSCA);
                    if (getOpenToscaControlService().invokeTOSCAProcessing(csarID)) {
                        return Response.ok().build();
                    }
                    break;
                case INVOKE_IA_DEPL:
                    DeploymentProcessResource.LOG.info("OPERATION: {}", DeploymentProcessOperation.INVOKE_IA_DEPL);
                    // FIXME Dirty hack. Needs some redesign
                    if (params[1] == null) {
                        return Response.serverError().build();
                    } else {
                        this.serviceTemplateId = QName.valueOf(params[1]);
                    }
                    if (getOpenToscaControlService().invokeIADeployment(csarID, this.serviceTemplateId)) {
                        return Response.ok().build();
                    }
                    break;

                case INVOKE_PLAN_DEPL:
                    DeploymentProcessResource.LOG.info("OPERATION: {}", DeploymentProcessOperation.INVOKE_PLAN_DEPL);
                    // FIXME Dirty hack. Needs some redesign
                    if (params[1] == null) {
                        return Response.serverError().build();
                    } else {
                        this.serviceTemplateId = QName.valueOf(params[1]);
                    }
                    if (getOpenToscaControlService().invokePlanDeployment(csarID, this.serviceTemplateId)) {
                        return Response.ok().build();
                    }
                    break;
            }
        }
        /**
         * 424 Method Failure (WebDAV)[13] Indicates the method was not executed on a particular resource
         * within its scope because some part of the method's execution failed causing the entire method to
         * be aborted.
         */
        return Response.status(424).build();
    }

    @Path("/DeploymentState")
    public DeploymentProcessStateResource getCSARFile() {
        return new DeploymentProcessStateResource(getOpenToscaControlService().getDeploymentProcessState(this.processID));
    }

    @Path("/Operations")
    public DeploymentProcessOperationsResource getMethods() {
        return new DeploymentProcessOperationsResource(getOpenToscaControlService().getExecutableDeploymentProcessOperations(this.processID));
    }

    @Path("/ServiceTemplates")
    public DeploymentProcessServiceTemplatesResource getServiceTemplates() {
        return new DeploymentProcessServiceTemplatesResource(this.processID);
    }
}
