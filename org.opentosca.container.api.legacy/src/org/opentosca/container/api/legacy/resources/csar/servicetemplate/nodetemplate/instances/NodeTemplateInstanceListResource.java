package org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate.instances;

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
import org.opentosca.container.api.legacy.instancedata.model.NodeInstanceList;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.service.IInstanceDataService;

/**
 * TODO delete this class
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class NodeTemplateInstanceListResource {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context final UriInfo uriInfo, @QueryParam("nodeInstanceID") final String nodeInstanceID, @QueryParam("nodeTemplateID") final String nodeTemplateID, @QueryParam("serviceInstanceID") final String serviceInstanceID, @QueryParam("nodeTemplateName") final String nodeTemplateName) {

		final NodeInstanceList idr = this.getRefs(uriInfo, nodeInstanceID, nodeTemplateID, serviceInstanceID, nodeTemplateName);

		return Response.ok(idr).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context final UriInfo uriInfo, @QueryParam("nodeInstanceID") final String nodeInstanceID, @QueryParam("nodeTemplateID") final String nodeTemplateID, @QueryParam("serviceInstanceID") final String serviceInstanceID, @QueryParam("nodeTemplateName") final String nodeTemplateName) {

		final NodeInstanceList idr = this.getRefs(uriInfo, nodeInstanceID, nodeTemplateID, serviceInstanceID, nodeTemplateName);

		return Response.ok(idr.toJSON()).build();
	}

	public NodeInstanceList getRefs(final UriInfo uriInfo, final String nodeInstanceID, final String nodeTemplateID, final String serviceInstanceID, final String nodeTemplateName) {

		// these parameters are not required and cant therefore be generally
		// checked against null

		URI nodeInstanceIdURI = null;
		URI serviceInstanceIdURI = null;
		QName nodeTemplateIDQName = null;
		try {
			if (nodeInstanceID != null) {
				nodeInstanceIdURI = new URI(nodeInstanceID);
				if (!IdConverter.isValidNodeInstanceID(nodeInstanceIdURI)) {
					throw new Exception("Error converting nodeInstanceID: invalid format!");
				}
			}

			if (serviceInstanceID != null) {
				serviceInstanceIdURI = new URI(serviceInstanceID);
				if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
					throw new Exception("Error converting serviceInstanceID: invalid format!");
				}
			}

			if (nodeTemplateID != null) {
				nodeTemplateIDQName = QName.valueOf(nodeTemplateID);
			}
		} catch (final Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due to bad variable content: " + e1.getMessage());
		}

		try {
			final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			final List<NodeInstance> result = service.getNodeInstances(nodeInstanceIdURI, nodeTemplateIDQName, nodeTemplateName, serviceInstanceIdURI);
			final List<SimpleXLink> links = new LinkedList<>();

			// add links to nodeInstances
			for (final NodeInstance nodeInstance : result) {
				final URI uriToNodeInstance = LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId());
				// build simpleXLink with the internalID as LinkText
				// TODO: is the id the correct linkText?
				links.add(new SimpleXLink(uriToNodeInstance, nodeInstance.getId() + ""));
			}

			final NodeInstanceList nil = new NodeInstanceList(LinkBuilder.selfLink(uriInfo), links);

			return nil;
		} catch (final Exception e) {
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createNodeInstance(@QueryParam("nodeTemplateID") final String nodeTemplateID, @QueryParam("serviceInstanceID") final String serviceInstanceID, @Context final UriInfo uriInfo) {

		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();

		if (Utilities.areEmpty(nodeTemplateID, serviceInstanceID)) {
			throw new GenericRestException(Status.BAD_REQUEST, "Missing one of the required parameters: nodeTemplateID, serviceInstanceID");
		}

		URI serviceInstanceIdURI = null;
		QName nodeTemplateIDQName = null;
		try {
			serviceInstanceIdURI = new URI(serviceInstanceID);
			if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
				throw new Exception("Error converting serviceInstanceID: invalid format!");
			}
			nodeTemplateIDQName = QName.valueOf(nodeTemplateID);

		} catch (final Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter: " + e1.getMessage());
		}

		try {
			final NodeInstance nodeInstance = service.createNodeInstance(nodeTemplateIDQName, serviceInstanceIdURI);
			final SimpleXLink response = new SimpleXLink(LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId()), nodeInstance.getNodeInstanceID().toString());
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