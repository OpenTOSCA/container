package org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTemplatesResource {

    private final Logger log = LoggerFactory.getLogger(NodeTemplatesResource.class);
    private final CSARID csarId;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;
    private UriInfo uriInfo;


    public NodeTemplatesResource(final CSARID csarId, final QName serviceTemplateID,
                                 final int serviceTemplateInstanceId) {
        this.csarId = csarId;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo, @QueryParam("nodeType") final QName nodeType)
        throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs(nodeType).getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs(null).getJSONString()).build();
    }

    public References getRefs(final QName nodeType) throws UnsupportedEncodingException {

        if (this.csarId == null) {
            return null;
        }

        final References refs = new References();

        for (final String ntID : ToscaServiceHandler.getToscaEngineService().getNodeTemplatesOfServiceTemplate(
            this.csarId, this.serviceTemplateID)) {

            if (nodeType != null
                && ToscaServiceHandler.getToscaEngineService()
                                      .getNodeTypeOfNodeTemplate(this.csarId, this.serviceTemplateID, ntID)
                                      .equals(nodeType)) {
                refs.getReference()
                    .add(new Reference(Utilities.buildURI(this.uriInfo, ntID), XLinkConstants.SIMPLE, ntID));
            } else {
                refs.getReference()
                    .add(new Reference(Utilities.buildURI(this.uriInfo, ntID), XLinkConstants.SIMPLE, ntID));
            }
        }

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
    }

    @Path("{PlanIdLocalPart}")
    @Produces(ResourceConstants.TOSCA_JSON)
    public NodeTemplateResource getNodeTemplate(@Context final UriInfo uriInfo,
                    @PathParam("PlanIdLocalPart") final String planIdLocalPart)
        throws URISyntaxException {
        return new NodeTemplateResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId,
            planIdLocalPart);
    }
}
