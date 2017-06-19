package org.opentosca.container.api.legacy.instancedata;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.instancedata.model.InstanceDataEntry;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;

/**
 * The root resource for the instance data API.
 * 
 * @author Florian Haupt <florian.haupt@iaas.uni-stuttgart.de>
 *
 */
@Path("/instancedata")
public class InstanceDataRoot {
	
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response doGetXML() {
		
		InstanceDataEntry idr = getRefs();
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON() {
		
		InstanceDataEntry idr = getRefs();
		
		return Response.ok(idr.toJSON()).build();
	}
	
	private InstanceDataEntry getRefs() {
		List<SimpleXLink> links = new LinkedList<SimpleXLink>();
		links.add(LinkBuilder.selfLink(uriInfo));
		//		links.add(new SimpleXLink(LinkBuilder.linkToNodeInstanceList(uriInfo), "Node Instances"));
		//		links.add(new SimpleXLink(LinkBuilder.linkToServiceInstanceList(uriInfo), "Service Instances"));
		
		InstanceDataEntry idr = new InstanceDataEntry(links);
		return idr;
	}
	
	//	@Path("/nodeInstances")
	//	public Object getNodeInstances() {
	//		return new NodeInstanceListResource();
	//	}
	
	//	@Path("/serviceInstances")
	//	public Object getServiceInstances() {
	//		return new ServiceInstanceListResource();
	//	}
	
}