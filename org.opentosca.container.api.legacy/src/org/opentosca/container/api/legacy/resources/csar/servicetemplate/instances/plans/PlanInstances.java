package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource represents the active PublicPlans for a CSAR-Instance.
 *
 * Copyright 2013 Christian Endres
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 *
 */
public class PlanInstances {

    private static final Logger LOG = LoggerFactory.getLogger(PlanInstances.class);

    private final CSARID csarID;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;

    UriInfo uriInfo;


    public PlanInstances(final CSARID csarID, final QName serviceTemplateID, final int serviceTemplateInstanceId) {
        this.csarID = csarID;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
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

        PlanInstances.LOG.debug("Access plan instance list at " + this.uriInfo.getAbsolutePath().toString());

        if (this.csarID == null) {
            PlanInstances.LOG.debug("The CSAR does not exist.");
            return null;
        }

        final References refs = new References();

        final IOpenToscaControlService control = IOpenToscaControlServiceHandler.getOpenToscaControlService();

        final ServiceTemplateInstanceRepository repo = new ServiceTemplateInstanceRepository();

        final Optional<ServiceTemplateInstance> o = repo.find(Long.valueOf(this.serviceTemplateInstanceId));
        final ServiceTemplateInstance sit = o.get();

        for (final org.opentosca.container.core.next.model.PlanInstance p : sit.getPlanInstances()) {
            refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, p.getCorrelationId()),
                XLinkConstants.SIMPLE, p.getCorrelationId()));
        }

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        return refs;
    }

    /**
     * Returns the plan information from history.
     *
     * @param uriInfo
     * @return Response
     * @throws URISyntaxException
     */
    @Path("{CorrelationID}")
    @Produces(ResourceConstants.TOSCA_JSON)
    public PlanInstance getPlanJSON(@Context final UriInfo uriInfo,
                                    @PathParam("CorrelationID") final String correlationID) throws URISyntaxException {
        LOG.debug("get plan of corr {}", correlationID);
        return new PlanInstance(this.csarID, this.serviceTemplateID, this.serviceTemplateInstanceId, correlationID);
    }

}
