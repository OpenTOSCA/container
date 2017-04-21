package org.opentosca.containerapi.resources.csar.servicetemplate.nodetemplate.instances;

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
public class NodeTemplateInstanceResource {
	
	
	private final Logger log = LoggerFactory.getLogger(ServiceTemplateInstanceResource.class);
	
	private final CSARID csarId;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final QName nodeTemplateID;
	private final int nodeTemplateInstanceId;
	
	
	public NodeTemplateInstanceResource(CSARID csarId, QName serviceTemplateID, int serviceTemplateInstanceId, QName nodeTemplateID, int id) {
		this.csarId = csarId;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.nodeTemplateID = nodeTemplateID;
		nodeTemplateInstanceId = id;
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
		
		log.debug("try to build node template instance resource");
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		List<NodeInstance> nodeInstances = service.getNodeInstances(IdConverter.nodeInstanceIDtoURI(nodeTemplateInstanceId), null, null, null);
		
		// existence of instance is already checked before invoking this class
		// and its methods
		NodeInstance nodeInstance = nodeInstances.get(0);
		
		QName nodeTypeQName = nodeInstance.getNodeType();
		List<String> nodeType = new ArrayList<String>();
		nodeType.add(nodeTypeQName.toString());
		
		List<SimpleXLink> links = new LinkedList<SimpleXLink>();
		links.add(LinkBuilder.selfLink(uriInfo));
		
		URI serviceInstanceID = nodeInstance.getServiceInstance().getServiceInstanceID();
		//		URI linkToServiceInstance = LinkBuilder.linkToServiceInstance(uriInfo, IdConverter.serviceInstanceUriToID(serviceInstanceID));
		
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
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
	
	@DELETE
	public Response deleteNodeInstance() {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		service.deleteNodeInstance(IdConverter.nodeInstanceIDtoURI(nodeTemplateInstanceId));
		return Response.noContent().build();
		
	}
	
	@Path("/Properties")
	public Object getProperties() {
		return new NodeTemplateInstancePropertiesResource(nodeTemplateInstanceId);
	}
	
	@Path("/State")
	public Object getState() {
		return new NodeTemplateInstanceStateResource(nodeTemplateInstanceId);
	}
	
}