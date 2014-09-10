package org.opentosca.containerapi.instancedata;

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

import org.opentosca.containerapi.instancedata.model.NodeInstanceEntry;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;

/**
 * This class manages access to a specific nodeInstance. It also checks the existance of the nodeInstance with the given ID (also for all children resources before passing the request along)
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
public class NodeInstanceResource {
	
	private int id;
	
	public NodeInstanceResource(int id) {
		this.id = id;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getData(@Context UriInfo uriInfo) {
		
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		List<NodeInstance> nodeInstances = service.getNodeInstances(IdConverter.nodeInstanceIDtoURI(id), null, null, null);
		
		//existence of instance is already checked before invoking this class and its methods
		NodeInstance nodeInstance = nodeInstances.get(0);
		
		QName nodeTypeQName = nodeInstance.getNodeType();
		List<String> nodeType = new ArrayList<String>();
		nodeType.add(nodeTypeQName.toString());
		
		List<SimpleXLink> links = new LinkedList<SimpleXLink>();
		links.add(LinkBuilder.selfLink(uriInfo));

		URI serviceInstanceID = nodeInstance.getServiceInstance().getServiceInstanceID();
		URI linkToServiceInstance = LinkBuilder.linkToServiceInstance(uriInfo, IdConverter.serviceInstanceUriToID(serviceInstanceID));

		links.add(new SimpleXLink(linkToServiceInstance,
				"ServiceInstance"));
		//properties link
		URI linkToProperties = LinkBuilder.linkToNodeInstanceProperties(uriInfo, id);
		links.add(new SimpleXLink(linkToProperties,
				"Properties"));
		//state link
		links.add(new SimpleXLink(LinkBuilder.linkToNodeInstanceState(uriInfo, id),
				"State"));
		NodeInstanceEntry nie = new NodeInstanceEntry(nodeInstance, links);
		
		return Response.ok(nie).build();
	}
	
	@DELETE
	public Response deleteNodeInstance() {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		service.deleteNodeInstance(IdConverter.nodeInstanceIDtoURI(this.id));
		return Response.noContent().build();
		
	}
	
	@Path("/properties")
	public Object getProperties() {
		return new NodeInstancePropertiesResource(id);
	}
	
	@Path("/state")
	public Object getState() {
		return new NodeInstanceStateResource(id);
	}
	
}