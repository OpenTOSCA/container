package org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate.instances;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.ExistenceChecker;
import org.opentosca.containerapi.instancedata.LinkBuilder;
import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.instancedata.model.NodeInstanceList;
import org.opentosca.containerapi.instancedata.model.RelationInstanceList;
import org.opentosca.containerapi.instancedata.model.ServiceInstanceEntry;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.instancedata.utilities.Constants;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class RelationshipTemplateInstancesResource {
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);
	
	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final QName relatioshipTemplateID;
	
	
	public RelationshipTemplateInstancesResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId, QName relationshipTemplateID) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.relatioshipTemplateID = relationshipTemplateID;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context UriInfo uriInfo) {
		
		References idr = getRefs(uriInfo);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.getXMLString()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo) {
		
		References idr = getRefs(uriInfo);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.getJSONString()).build();
	}
	
	public References getRefs(UriInfo uriInfo) {
		References refs = new References();
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		URI serviceInstanceIDtoURI = IdConverter.serviceInstanceIDtoURI(serviceTemplateInstanceId);
		
		try {
			// self link is only link at the moment in the main list
			List<SimpleXLink> serviceInstanceLinks = new LinkedList<SimpleXLink>();
			serviceInstanceLinks.add(LinkBuilder.selfLink(uriInfo));
			
			// its ensured that this serviceInstance exists
			List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIDtoURI, null, null);
			ServiceInstance serviceInstance = serviceInstances.get(0);
			
			// extract values
			
			// build nodeInstanceList
			List<RelationInstance> relationInstances = service.getRelationInstances(null, null, null, serviceInstanceIDtoURI);
			List<SimpleXLink> relationInstanceLinks = new LinkedList<SimpleXLink>();
			
			for (RelationInstance relationInstance : relationInstances) {
				// URI uriToNodeInstance =
				// LinkBuilder.linkToNodeInstance(uriInfo,
				// nodeInstance.getId());
				// // build simpleXLink with the nodeInstanceID as LinkText
				// nodeInstanceLinks.add(new SimpleXLink(uriToNodeInstance,
				// nodeInstance.getNodeInstanceID().toString()));
				
				QName relationId = relationInstance.getRelationshipTemplateID();
				int relationInstanceId = relationInstance.getId();
				// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
				// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
				// "/Instances/" + serviceTemplateInstanceId + "/NodeTemplates/"
				// + nodeInstanceId;
				
				if (relatioshipTemplateID.toString().equalsIgnoreCase(relationId.toString()) || relatioshipTemplateID.toString().equalsIgnoreCase(relationId.getLocalPart())) {
					Reference ref = new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), String.valueOf(relationInstanceId)), XLinkConstants.SIMPLE, String.valueOf(relationInstanceId));
					refs.getReference().add(ref);
					// String nodeUrl = "/CSARs/" + csarId +
					// "/ServiceTemplates/" +
					// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8")
					// + "/Instances/" + serviceTemplateInstanceId +
					// "/NodeTemplates/" + nodeTemplateID.getLocalPart() +
					// "Instances" + nodeInstanceId;
					// refs.getReference().add(new
					// Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(),
					// nodeUrl), XLinkConstants.REFERENCE,
					// URLEncoder.encode(nodeId.getLocalPart(), "UTF-8")));
					log.debug("build relation reference {}", ref.getXhref());
				} else {
					log.debug("Skipped node instance {} of node template id {}", relationInstanceId, relationId);
				}
				
				// if (nodeType == null) {
				// QName nodeId = nodeInstance.getNodeTemplateID();
				// int nodeInstanceId = nodeInstance.getId();
				// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
				// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
				// "/Instances/" + serviceTemplateInstanceId + "/NodeTemplates/"
				// + nodeInstanceId;
				// refs.getReference().add(new
				// Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(),
				// nodeUrl), XLinkConstants.REFERENCE,
				// URLEncoder.encode(nodeId.getLocalPart(), "UTF-8")));
				// log.debug("build node reference {}", nodeUrl);
				// } else {
				// if (nodeInstance.getNodeType().equals(nodeType)) {
				// QName nodeId = nodeInstance.getNodeTemplateID();
				// int nodeInstanceId = nodeInstance.getId();
				// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
				// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
				// "/Instances/" + serviceTemplateInstanceId + "/NodeTemplates/"
				// + nodeInstanceId;
				// refs.getReference().add(new
				// Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(),
				// nodeUrl), XLinkConstants.REFERENCE,
				// URLEncoder.encode(nodeId.getLocalPart(), "UTF-8")));
				// log.debug("build node reference {}", nodeUrl);
				// }
				// }
			}
			// we dont want a self link because the InstanceList is part of
			// another list already containing a self link
			RelationInstanceList ril = new RelationInstanceList(null, relationInstanceLinks);
			
			ServiceInstanceEntry sie = new ServiceInstanceEntry(serviceInstance, serviceInstanceLinks, ril);
			
			// selflink
			refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
			
			return refs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createRelationInstance(@Context UriInfo uriInfo, @QueryParam("sourceInstanceId") String sourceInstanceId, @QueryParam("targetInstanceId") String targetInstanceId) {
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		// ServiceInstance serviceInstanceIdURI = service.getservi
		
		// if (Utilities.areEmpty(nodeTemplateID, serviceTemplateInstanceId)) {
		// throw new GenericRestException(Status.BAD_REQUEST, "Missing one of
		// the required parameters: nodeTemplateID, serviceInstanceID");
		// }
		
		// URI serviceInstanceIdURI = null;
		// try {
		//
		// serviceInstanceIdURI = new ServiceInstance(csarId, nodeTemplateID,
		// nodeTemplateID.getLocalPart(),
		// serviceTemplateInstanceId).getServiceInstanceID();
		// if (!IdConverter.isValidServiceInstanceID(serviceInstanceIdURI)) {
		// throw new Exception("Error converting serviceInstanceID: invalid
		// format!");
		// }
		//
		// } catch (Exception e1) {
		// throw new GenericRestException(Status.BAD_REQUEST, "Error converting
		// parameter: " + e1.getMessage());
		// }
		
		try {
			RelationInstance relationInstance = service.createRelationInstance(csarId, serviceTemplateID, serviceTemplateInstanceId, relatioshipTemplateID, sourceInstanceId, targetInstanceId);
			// SimpleXLink response = new
			// SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" +
			// serviceTemplateInstanceId, "simple");
			SimpleXLink response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" + relationInstance.getId(), "simple");
			return Response.ok(response).build();
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	@Path("/{" + Constants.RelationInstanceListResource_getRelationInstance_PARAM + "}")
	public Object getRelationInstance(@PathParam(Constants.RelationInstanceListResource_getRelationInstance_PARAM) int relationshipTemplateInstanceId, @Context UriInfo uriInfo) {
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkRelationInstanceWithException(relationshipTemplateInstanceId, service);
		return new RelationshipTemplateInstanceResource(csarId, serviceTemplateID, serviceTemplateInstanceId, relatioshipTemplateID, relationshipTemplateInstanceId);
	}
}
