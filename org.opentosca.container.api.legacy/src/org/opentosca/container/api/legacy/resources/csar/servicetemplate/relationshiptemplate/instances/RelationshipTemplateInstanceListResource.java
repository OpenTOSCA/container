package org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate.instances;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.api.legacy.instancedata.exception.GenericRestException;
import org.opentosca.container.api.legacy.instancedata.model.RelationInstanceList;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.service.IInstanceDataService;

/**
 * TODO delete this class
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class RelationshipTemplateInstanceListResource {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response doGetXML(@Context final UriInfo uriInfo,
                    @QueryParam("relationInstanceID") final String relationInstanceID,
                    @QueryParam("relationshipTemplateID") final String relationshipTemplateID,
                    @QueryParam("serviceInstanceID") final String serviceInstanceID,
                    @QueryParam("relationshipTemplateName") final String relationshipTemplateName) {

        final RelationInstanceList idr = this.getRefs(uriInfo, relationInstanceID, relationshipTemplateID,
            serviceInstanceID, relationshipTemplateName);

        return Response.ok(idr).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetJSON(@Context final UriInfo uriInfo,
                    @QueryParam("relationInstanceID") final String relationInstanceID,
                    @QueryParam("relationshipTemplateID") final String relationshipTemplateID,
                    @QueryParam("serviceInstanceID") final String serviceInstanceID,
                    @QueryParam("relationshipTemplateName") final String relationshipTemplateName) {

        final RelationInstanceList idr = this.getRefs(uriInfo, relationInstanceID, relationshipTemplateID,
            serviceInstanceID, relationshipTemplateName);

        return Response.ok(idr.toJSON()).build();
    }

    public RelationInstanceList getRefs(final UriInfo uriInfo, final String relationInstanceID,
                    final String relationshipTemplateID, final String serviceInstanceID,
                    final String relationshipTemplateName) {

        // these parameters are not required and cant therefore be generally
        // checked against null

        URI relationInstanceIdURI = null;
        URI serviceInstanceIdURI = null;
        QName relationshipTemplateIDQName = null;
        try {
            if (relationInstanceID != null) {
                relationInstanceIdURI = new URI(relationInstanceID);
                if (!IdConverter.isValidRelationInstanceID(relationInstanceIdURI)) {
                    throw new Exception("Error converting relationInstanceID: invalid format!");
                }
            }

            if (serviceInstanceID != null) {
                serviceInstanceIdURI = new URI(serviceInstanceID);
                if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
                    throw new Exception("Error converting serviceInstanceID: invalid format!");
                }
            }

            if (relationshipTemplateID != null) {
                relationshipTemplateIDQName = QName.valueOf(relationshipTemplateID);
            }
        } catch (final Exception e1) {
            throw new GenericRestException(Status.BAD_REQUEST,
                "Bad Request due to bad variable content: " + e1.getMessage());
        }

        try {
            final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
            final List<RelationInstance> result = service.getRelationInstances(relationInstanceIdURI,
                relationshipTemplateIDQName, relationshipTemplateName, serviceInstanceIdURI);
            final List<SimpleXLink> links = new LinkedList<>();

            // add links to nodeInstances
            for (final RelationInstance relationInstance : result) {
                final URI uriToRelationInstance = LinkBuilder.linkToRelationInstance(uriInfo, relationInstance.getId());
                // build simpleXLink with the internalID as LinkText
                // TODO: is the id the correct linkText?
                links.add(new SimpleXLink(uriToRelationInstance, relationInstance.getId() + ""));
            }

            final RelationInstanceList ril = new RelationInstanceList(LinkBuilder.selfLink(uriInfo), links);

            return ril;
        } catch (final Exception e) {
            throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response createRelationInstance(@QueryParam("relationshipTemplateID") final String relationshipTemplateID,
                    @QueryParam("serviceInstanceID") final String serviceInstanceID, @Context final UriInfo uriInfo) {

        final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();

        if (Utilities.areEmpty(relationshipTemplateID, serviceInstanceID)) {
            throw new GenericRestException(Status.BAD_REQUEST,
                "Missing one of the required parameters: relationshipTemplateID, serviceInstanceID");
        }

        URI serviceInstanceIdURI = null;
        QName relationshipTemplateIDQName = null;
        try {
            serviceInstanceIdURI = new URI(serviceInstanceID);
            if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
                throw new Exception("Error converting serviceInstanceID: invalid format!");
            }
            relationshipTemplateIDQName = QName.valueOf(relationshipTemplateID);

        } catch (final Exception e1) {
            throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter: " + e1.getMessage());
        }

        try {
            // FIXME at this point a brutal amount of confusion is rising up,
            // while
            // implementing I noticed that this method
            // createNodeInstance(para,para) is deprecated and doesn't work. So
            // it seems this whole class is useless? Because I tried to
            // implement relation in the style of the nodeInstances but this
            // method makes literally no sense
            final NodeInstance nodeInstance = service.createNodeInstance(relationshipTemplateIDQName,
                serviceInstanceIdURI);
            final SimpleXLink response = new SimpleXLink(LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId()),
                nodeInstance.getNodeInstanceID().toString());
            return Response.ok(response).build();
        } catch (final ReferenceNotFoundException e) {
            throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
        }
    }

    // @Path("/{" + Constants.NodeInstanceListResource_getNodeInstance_PARAM +
    // "}")
    // public Object
    // getNodeInstance(@PathParam(Constants.NodeInstanceListResource_getNodeInstance_PARAM)
    // int id, @Context UriInfo uriInfo) {
    // IInstanceDataService service =
    // InstanceDataServiceHandler.getInstanceDataService();
    // ExistenceChecker.checkNodeInstanceWithException(id, service);
    // return new NodeTemplateInstanceResource(id);
    // }

}
