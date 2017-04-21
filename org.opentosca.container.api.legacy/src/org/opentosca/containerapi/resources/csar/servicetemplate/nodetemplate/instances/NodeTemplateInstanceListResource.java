package org.opentosca.containerapi.resources.csar.servicetemplate.nodetemplate.instances;

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
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;

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
	public Response doGetXML(@Context UriInfo uriInfo, @QueryParam("nodeInstanceID") String nodeInstanceID, @QueryParam("nodeTemplateID") String nodeTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @QueryParam("nodeTemplateName") String nodeTemplateName) {
		
		NodeInstanceList idr = getRefs(uriInfo, nodeInstanceID, nodeTemplateID, serviceInstanceID, nodeTemplateName);
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo, @QueryParam("nodeInstanceID") String nodeInstanceID, @QueryParam("nodeTemplateID") String nodeTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @QueryParam("nodeTemplateName") String nodeTemplateName) {
		
		NodeInstanceList idr = getRefs(uriInfo, nodeInstanceID, nodeTemplateID, serviceInstanceID, nodeTemplateName);
		
		return Response.ok(idr.toJSON()).build();
	}
	
	public NodeInstanceList getRefs(UriInfo uriInfo, String nodeInstanceID, String nodeTemplateID, String serviceInstanceID, String nodeTemplateName) {
		
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
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Bad Request due to bad variable content: " + e1.getMessage());
		}
		
		try {
			IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
			List<NodeInstance> result = service.getNodeInstances(nodeInstanceIdURI, nodeTemplateIDQName, nodeTemplateName, serviceInstanceIdURI);
			List<SimpleXLink> links = new LinkedList<SimpleXLink>();
			
			// add links to nodeInstances
			for (NodeInstance nodeInstance : result) {
				URI uriToNodeInstance = LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId());
				// build simpleXLink with the internalID as LinkText
				// TODO: is the id the correct linkText?
				links.add(new SimpleXLink(uriToNodeInstance, nodeInstance.getId() + ""));
			}
			
			NodeInstanceList nil = new NodeInstanceList(LinkBuilder.selfLink(uriInfo), links);
			
			return nil;
		} catch (Exception e) {
			throw new GenericRestException(Status.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createNodeInstance(@QueryParam("nodeTemplateID") String nodeTemplateID, @QueryParam("serviceInstanceID") String serviceInstanceID, @Context UriInfo uriInfo) {
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
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
			
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter: " + e1.getMessage());
		}
		
		try {
			NodeInstance nodeInstance = service.createNodeInstance(nodeTemplateIDQName, serviceInstanceIdURI);
			SimpleXLink response = new SimpleXLink(LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId()), nodeInstance.getNodeInstanceID().toString());
			return Response.ok(response).build();
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	//	@Path("/{" + Constants.NodeInstanceListResource_getNodeInstance_PARAM + "}")
	//	public Object getNodeInstance(@PathParam(Constants.NodeInstanceListResource_getNodeInstance_PARAM) int id, @Context UriInfo uriInfo) {
	//		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
	//		ExistenceChecker.checkNodeInstanceWithException(id, service);
	//		return new NodeTemplateInstanceResource(id);
	//	}
	
}