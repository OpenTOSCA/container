package org.opentosca.container.api.legacy.resources.csar.servicetemplate.relationshiptemplate.instances;

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

import org.opentosca.container.api.legacy.instancedata.ExistenceChecker;
import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.api.legacy.instancedata.exception.GenericRestException;
import org.opentosca.container.api.legacy.instancedata.model.RelationInstanceList;
import org.opentosca.container.api.legacy.instancedata.model.ServiceInstanceEntry;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.instancedata.utilities.Constants;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.service.IInstanceDataService;
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
	
	
	public RelationshipTemplateInstancesResource(final CSARID csarId, final QName serviceTemplateID, final int serviceTemplateInstanceId, final QName relationshipTemplateID) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.relatioshipTemplateID = relationshipTemplateID;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context final UriInfo uriInfo, @QueryParam("sourceNodeInstanceId") String sourceNodeInstanceId, @QueryParam("targetNodeInstanceId") String targetNodeInstanceId) {
		
		final References idr = this.getRefs(uriInfo, sourceNodeInstanceId, targetNodeInstanceId);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.getXMLString()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context final UriInfo uriInfo, @QueryParam("sourceNodeInstanceId") String sourceNodeInstanceId, @QueryParam("targetNodeInstanceId") String targetNodeInstanceId) {
		
		final References idr = this.getRefs(uriInfo, sourceNodeInstanceId, targetNodeInstanceId);
		
		if (null == idr) {
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.getJSONString()).build();
	}
	
	public References getRefs(final UriInfo uriInfo, String sourceNodeInstanceId, String targetNodeInstanceId) {
		final References refs = new References();
		
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		final URI serviceInstanceIDtoURI = IdConverter.serviceInstanceIDtoURI(this.serviceTemplateInstanceId);
		
		try {
			// self link is only link at the moment in the main list
			final List<SimpleXLink> serviceInstanceLinks = new LinkedList<>();
			serviceInstanceLinks.add(LinkBuilder.selfLink(uriInfo));
			
			// its ensured that this serviceInstance exists
			final List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIDtoURI, null, null);
			final ServiceInstance serviceInstance = serviceInstances.get(0);
			
			// extract values
			
			// build nodeInstanceList
			final List<RelationInstance> relationInstances = service.getRelationInstances(null, null, null, serviceInstanceIDtoURI);
			final List<SimpleXLink> relationInstanceLinks = new LinkedList<>();
			
			for (final RelationInstance relationInstance : relationInstances) {
				// URI uriToNodeInstance =
				// LinkBuilder.linkToNodeInstance(uriInfo,
				// nodeInstance.getId());
				// // build simpleXLink with the nodeInstanceID as LinkText
				// nodeInstanceLinks.add(new SimpleXLink(uriToNodeInstance,
				// nodeInstance.getNodeInstanceID().toString()));
				
				if(sourceNodeInstanceId != null ) {
					if(!relationInstance.getSourceInstance().getNodeInstanceID().toString().equals(sourceNodeInstanceId)) {
						continue;
					}
				}
				
				if(targetNodeInstanceId != null ) {
					if(!relationInstance.getTargetInstance().getNodeInstanceID().toString().equals(targetNodeInstanceId)) {
						continue;
					}
				}
				
				final QName relationId = relationInstance.getRelationshipTemplateID();
				final int relationInstanceId = relationInstance.getId();
				// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
				// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
				// "/Instances/" + serviceTemplateInstanceId + "/NodeTemplates/"
				// + nodeInstanceId;
				
				if (this.relatioshipTemplateID.toString().equalsIgnoreCase(relationId.toString()) || this.relatioshipTemplateID.toString().equalsIgnoreCase(relationId.getLocalPart())) {
					final Reference ref = new Reference(Utilities.buildURI(uriInfo, String.valueOf(relationInstanceId)), XLinkConstants.SIMPLE, String.valueOf(relationInstanceId));
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
					this.log.debug("build relation reference {}", ref.getXhref());
				} else {
					this.log.debug("Skipped node instance {} of node template id {}", relationInstanceId, relationId);
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
			final RelationInstanceList ril = new RelationInstanceList(null, relationInstanceLinks);
			
			final ServiceInstanceEntry sie = new ServiceInstanceEntry(serviceInstance, serviceInstanceLinks, ril);
			
			// selflink
			refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
			
			return refs;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response createRelationInstance(@Context final UriInfo uriInfo, @QueryParam("sourceInstanceId") final String sourceInstanceId, @QueryParam("targetInstanceId") final String targetInstanceId) {
		
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
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
			final RelationInstance relationInstance = service.createRelationInstance(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId, this.relatioshipTemplateID, sourceInstanceId, targetInstanceId);
			// SimpleXLink response = new
			// SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" +
			// serviceTemplateInstanceId, "simple");
			final SimpleXLink response = new SimpleXLink(uriInfo.getAbsolutePath().toString() + "/" + relationInstance.getId(), "simple");
			return Response.ok(response).build();
		} catch (final ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
		}
	}
	
	@Path("/{" + Constants.RelationInstanceListResource_getRelationInstance_PARAM + "}")
	public Object getRelationInstance(@PathParam(Constants.RelationInstanceListResource_getRelationInstance_PARAM) final int relationshipTemplateInstanceId, @Context final UriInfo uriInfo) {
		
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		ExistenceChecker.checkRelationInstanceWithException(relationshipTemplateInstanceId, service);
		return new RelationshipTemplateInstanceResource(this.csarId, this.serviceTemplateID, this.serviceTemplateInstanceId, this.relatioshipTemplateID, relationshipTemplateInstanceId);
	}
}
