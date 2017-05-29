package org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate.instances;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.instancedata.LinkBuilder;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstanceResource;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages access to a specific nodeInstance. It also checks the
 * existance of the nodeInstance with the given ID (also for all children
 * resources before passing the request along)
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
public class RelationshipTemplateInstanceResource {
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);
	
	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final QName relationshipTemplateID;
	private final int relationshipTemplateInstanceId;
	
	
	public RelationshipTemplateInstanceResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId, QName relationshipTemplateID, int id) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.relationshipTemplateID = relationshipTemplateID;
		relationshipTemplateInstanceId = id;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		
		References idr = getRefs(uriInfo);
		
		return Response.ok(idr.getXMLString()).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo) throws UnsupportedEncodingException {
		
		References idr = getRefs(uriInfo);
		
		return Response.ok(idr.getJSONString()).build();
	}
	
	public References getRefs(UriInfo uriInfo) throws UnsupportedEncodingException {
		
		References refs = new References();
		
		log.debug("try to build relation template instance resource");
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		List<RelationInstance> relationInstances = service.getRelationInstances(IdConverter.relationInstanceIDtoURI(relationshipTemplateInstanceId), null, null, null);
		
		// existence of instance is already checked before invoking this class
		// and its methods
		RelationInstance relationInstance = relationInstances.get(0);
		
		QName relationshipTypeQName = relationInstance.getRelationshipType();
		List<String> relationshipType = new ArrayList<String>();
		relationshipType.add(relationshipTypeQName.toString());
		
		List<SimpleXLink> links = new LinkedList<SimpleXLink>();
		links.add(LinkBuilder.selfLink(uriInfo));
		
		URI serviceInstanceID = relationInstance.getServiceInstance().getServiceInstanceID();
		// URI linkToServiceInstance =
		// LinkBuilder.linkToServiceInstance(uriInfo,
		// IdConverter.serviceInstanceUriToID(serviceInstanceID));
		
		// String nodeUrl = "/CSARs/" + csarId + "/ServiceTemplates/" +
		// URLEncoder.encode(serviceTemplateID.toString(), "UTF-8") +
		// "/Instances/" + serviceTemplateInstanceId;
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(uriInfo.getBaseUri().toString(),
		// nodeUrl), XLinkConstants.REFERENCE,
		// "ParentServiceTemplateInstance"));
		
		// links.add(new SimpleXLink(linkToServiceInstance, "ServiceInstance"));
		// // properties link
		// URI linkToProperties =
		// LinkBuilder.linkToNodeInstanceProperties(uriInfo,
		// nodeTemplateInstanceId);
		// links.add(new SimpleXLink(linkToProperties, "Properties"));
		// // state link
		// links.add(new
		// SimpleXLink(LinkBuilder.linkToNodeInstanceState(uriInfo,
		// nodeTemplateInstanceId), "State"));
		// NodeInstanceEntry nie = new NodeInstanceEntry(nodeInstance, links);
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Properties"), XLinkConstants.SIMPLE, "Properties"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "State"), XLinkConstants.SIMPLE, "State"));
		
		if (relationInstance.getSourceInstance() != null) {
			refs.getReference().add(new Reference(relationInstance.getSourceInstance().getNodeInstanceID().toString(), XLinkConstants.SIMPLE, "SourceInstanceId"));
		}
		
		if (relationInstance.getTargetInstance() != null) {
			refs.getReference().add(new Reference(relationInstance.getTargetInstance().getNodeInstanceID().toString(), XLinkConstants.SIMPLE, "TargetInstanceId"));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	@DELETE
	public Response deleteRelationInstance() {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		service.deleteRelationInstance(IdConverter.relationInstanceIDtoURI(relationshipTemplateInstanceId));
		return Response.noContent().build();
		
	}
	
	@Path("/Properties")
	public Object getProperties() {
		return new RelationshipTemplateInstancePropertiesResource(relationshipTemplateInstanceId);
	}
	
	@Path("/State")
	public Object getState() {
		return new RelationshipTemplateInstanceStateResource(relationshipTemplateInstanceId);
	}
	
}