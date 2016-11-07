package org.opentosca.containerapi.instancedata;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.instancedata.model.NodeInstanceList;
import org.opentosca.containerapi.instancedata.model.ServiceInstanceEntry;
import org.opentosca.containerapi.instancedata.model.SimpleXLink;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;

/**
 * 
 * @author Marcus Eisele <marcus.eisele@gmail.com>
 *
 */
public class ServiceInstanceResource {
	
	
	private int id;
	
	
	public ServiceInstanceResource(int id) {
		this.id = id;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML(@Context UriInfo uriInfo) {
		
		ServiceInstanceEntry idr = getRefs(uriInfo);
		
		if (null == idr){
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON(@Context UriInfo uriInfo) {
		
		ServiceInstanceEntry idr = getRefs(uriInfo);
		
		if (null == idr){
			Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok(idr.toJSON()).build();
	}
	
	public ServiceInstanceEntry getRefs( UriInfo uriInfo) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		URI serviceInstanceIDtoURI = IdConverter.serviceInstanceIDtoURI(id);
		
		try {
			// self link is only link at the moment in the main list
			List<SimpleXLink> serviceInstanceLinks = new LinkedList<SimpleXLink>();
			serviceInstanceLinks.add(LinkBuilder.selfLink(uriInfo));
			
			// its ensured that this serviceInstance exists
			List<ServiceInstance> serviceInstances = service.getServiceInstances(serviceInstanceIDtoURI, null, null);
			ServiceInstance serviceInstance = serviceInstances.get(0);
			
			// extract values
			
			// build nodeInstanceList
			List<NodeInstance> result = service.getNodeInstances(null, null, null, serviceInstanceIDtoURI);
			List<SimpleXLink> nodeInstanceLinks = new LinkedList<SimpleXLink>();
			
			for (NodeInstance nodeInstance : result) {
				URI uriToNodeInstance = LinkBuilder.linkToNodeInstance(uriInfo, nodeInstance.getId());
				// build simpleXLink with the nodeInstanceID as LinkText
				nodeInstanceLinks.add(new SimpleXLink(uriToNodeInstance, nodeInstance.getNodeInstanceID().toString()));
			}
			// we dont want a self link because the InstanceList is part of
			// another list already containing a self link
			NodeInstanceList nil = new NodeInstanceList(null, nodeInstanceLinks);
			
			ServiceInstanceEntry sie = new ServiceInstanceEntry(serviceInstance, serviceInstanceLinks, nil);
			
			return sie;
		} catch (Exception e) {
			return null;
		}
	}
	
	@DELETE
	public Response deleteServiceInstance() {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		service.deleteServiceInstance(IdConverter.serviceInstanceIDtoURI(id));
		return Response.noContent().build();
	}
	
	@Path("/properties")
	public Object getProperties() {
		return new ServiceInstancePropertiesResource(id);
	}
	
}