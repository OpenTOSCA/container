package org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

public class RelationshipTemplatesResource {

    private final Logger log = LoggerFactory.getLogger(RelationshipTemplatesResource.class);
    private final CSARID csarId;
    private final QName serviceTemplateID;
    private final int serviceTemplateInstanceId;
    private UriInfo uriInfo;


    public RelationshipTemplatesResource(final CSARID csarId, final QName serviceTemplateID,
                                         final int serviceTemplateInstanceId) {
        this.csarId = csarId;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo) throws UnsupportedEncodingException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs().getJSONString()).build();
    }

    public References getRefs() throws UnsupportedEncodingException {

        if (this.csarId == null) {
            return null;
        }

        final References refs = new References();

        for (final String rtID : ToscaServiceHandler.getToscaEngineService().getRelationshipTemplatesOfServiceTemplate(
            this.csarId, this.serviceTemplateID)) {
            refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, rtID), XLinkConstants.SIMPLE, rtID));
        }

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
    }

    @Path("{PlanIdLocalPart}")
    @Produces(ResourceConstants.TOSCA_JSON)
    public RelationshipTemplateResource getRelationshipTemplate(@Context final UriInfo uriInfo,
                    @PathParam("PlanIdLocalPart") final String planIdLocalPart)
        throws URISyntaxException {
        return new RelationshipTemplateResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId,
            planIdLocalPart);
    }
}
