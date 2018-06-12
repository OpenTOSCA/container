package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInstance {

    private static final Logger LOG = LoggerFactory.getLogger(PlanInstance.class);

    private final CSARID csarID;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;
    private final String correlationID;
    private UriInfo uriInfo;


    public PlanInstance(final CSARID csarID, final QName serviceTemplateID, final int serviceTemplateInstanceId,
                        final String correlationID) {
        this.csarID = csarID;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.correlationID = correlationID;
    }

    /**
     * Produces the xml which lists the CorrelationIDs of the active PublicPlans.
     *
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getReferences().getXMLString()).build();
    }

    /**
     * Produces the JSON which lists the links to the History and the active plans.
     *
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        return Response.ok(this.getReferences().getJSONString()).build();
    }

    public References getReferences() {

        LOG.debug("return plan instance of corr {}", this.correlationID);

        final References refs = new References();

        refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Logs"), XLinkConstants.SIMPLE, "Logs"));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo, "MetaData"), XLinkConstants.SIMPLE, "MetaData"));
        // if ((null !=
        // CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(this.csarID)) &&
        // CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(this.csarID).contains(this.correlationID))
        // {
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo, "Output"), XLinkConstants.SIMPLE, "Output"));
        // }
        refs.getReference()
            .add(new Reference(Utilities.buildURI(this.uriInfo, "State"), XLinkConstants.SIMPLE, "State"));

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        return refs;
    }

    @Path("State")
    @Produces(ResourceConstants.TOSCA_JSON)
    public PlanInstanceState getPlanState(@Context final UriInfo uriInfo) throws URISyntaxException {
        return new PlanInstanceState(this.csarID, this.serviceTemplateID, this.serviceTemplateInstanceId,
            this.correlationID);
    }

    @Path("Output")
    @Produces(ResourceConstants.TOSCA_JSON)
    public PlanInstanceOutput getPlanOutput(@Context final UriInfo uriInfo) throws URISyntaxException {
        return new PlanInstanceOutput(this.csarID, this.serviceTemplateID, this.serviceTemplateInstanceId,
            this.correlationID);
    }

    @Path("Logs")
    @Produces(ResourceConstants.TOSCA_JSON)
    public PlanInstanceLogs getPlanLogs(@Context final UriInfo uriInfo) throws URISyntaxException {
        return new PlanInstanceLogs(this.csarID, this.serviceTemplateID, this.serviceTemplateInstanceId,
            this.correlationID);
    }

    @Path("MetaData")
    @Produces(ResourceConstants.TOSCA_JSON)
    public PlanInstanceMetaData getPlanMetaData(@Context final UriInfo uriInfo) throws URISyntaxException {
        return new PlanInstanceMetaData(this.csarID, this.serviceTemplateID, this.serviceTemplateInstanceId,
            this.correlationID);
    }

}
