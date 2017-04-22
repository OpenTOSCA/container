package org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate.instances;

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

import org.opentosca.containerapi.instancedata.LinkBuilder;
import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.NodeInstanceList;
import org.opentosca.containerapi.instancedata.model.RelationInstanceList;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;

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
	public Response doGetXML(@Context UriInfo uriInfo, @QueryParam("relationInstanceID") String relationInstanceID, @QueryParam("relationshipTemplateID") String relationshipTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @QueryParam("relationshipTemplateName") String relationshipTemplateName) {
		
		RelationInstanceList idr = getRefs(uriInfo, relationInstanceID, relationshipTemplateID, serviceInstanceID, relationshipTemplateName);
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo, @QueryParam("relationInstanceID") String relationInstanceID, @QueryParam("relationshipTemplateID") String relationshipTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @QueryParam("relationshipTemplateName") String relationshipTemplateName) {
		
		RelationInstanceList idr = getRefs(uriInfo, relationInstanceID, relationshipTemplateID, serviceInstanceID, relationshipTemplateName);
		
		return Response.ok(idr.toJSON()).build();
	}
	
	public RelationInstanceList getRefs(UriInfo uriInfo, String relationInstanceID, String relationshipTemplateID, String serviceInstanceID, String relationshipTemplateName) {
		
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
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due to bad variable content: " + e1.getMessage());
		}
		
		try {
			IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			List<RelationInstance> result = service.getRelationInstances(relationInstanceIdURI, relationshipTemplateIDQName, relationshipTemplateName, serviceInstanceIdURI);
			List<SimpleXLink> links = new LinkedList<SimpleXLink>();
			
			// add links to nodeInstances
			for (RelationInstance relationInstance : result) {
				URI uriToRelationInstance = LinkBuilder.linkToRelationInstance(uriInfo, relationInstance.getId());
				// build simpleXLink with the internalID as LinkText
				// TODO: is the id the correct linkText?
				links.add(new SimpleXLink(uriToRelationInstance, relationInstance.getId() + ""));
			}
			
			RelationInstanceList ril = new RelationInstanceList(LinkBuilder.selfLink(uriInfo), links);
			
			return ril;
		} catch (Exception e) {
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createRelationInstance(@QueryParam("relationshipTemplateID") String relationshipTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @Context UriInfo uriInfo) {
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
		if (Utilities.areEmpty(relationshipTemplateID, serviceInstanceID)) {
			throw new GenericRestException(Status.BAD_REQUEST, "Missing one of the required parameters: relationshipTemplateID, serviceInstanceID");
		}
		
		URI serviceInstanceIdURI = null;
		QName relationshipTemplateIDQName = null;
		try {
			serviceInstanceIdURI = new URI(serviceInstanceID);
			if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
				throw new Exception("Error converting serviceInstanceID: invalid format!");
			}
			relationshipTemplateIDQName = QName.valueOf(relationshipTemplateID);
			
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter: " + e1.getMessage());
		}
		
		try {
			// FIXME at this point a brutal amount confusion is rising up, while
			// implementing I noticed that this method
			// createNodeInstance(para,para) is deprecated and doesn't work. So
			// it seems this whole class is useless? Because I tried to
			// implement relation in the style of the nodeInstances but this
			// method makes literally no sense
			NodeInstance nodeInstance = service.createNodeInstance(relationshipTemplateIDQName, serviceInstanceIdURI);
			SimpleXLink response = new SimpleXLink(LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId()), nodeInstance.getNodeInstanceID().toString());
			return Response.ok(response).build();
		} catch (ReferenceNotFoundException e) {
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