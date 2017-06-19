package org.opentosca.container.api.legacy.resources.csar.servicetemplate.nodetemplate.instances;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.instancedata.exception.GenericRestException;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.service.IInstanceDataService;

import com.google.gson.JsonObject;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class NodeTemplateInstanceStateResource {

	private final int nodeInstanceID;


	public NodeTemplateInstanceStateResource(final int id) {
		this.nodeInstanceID = id;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetXML() {
		
		final String idr = this.getState();
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON() {
		
		final String idr = this.getState();
		
		final JsonObject json = new JsonObject();
		json.addProperty("state", idr);
		
		return Response.ok(json.toString()).build();
	}
	
	public String getState() {
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
		try {
			final QName state = service.getNodeInstanceState(IdConverter.nodeInstanceIDtoURI(this.nodeInstanceID));
			if (state != null) {
				return state.toString();
			} else {
				return null;
			}
		} catch (final ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, "Specified nodeInstance with id: " + this.nodeInstanceID + " doesn't exist");
		}
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_XML)
	public Response setState(@Context final UriInfo uriInfo, final String state) {
		final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
		QName stateQName = null;
		try {
			stateQName = QName.valueOf(state);
			
		} catch (final Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter state: " + e1.getMessage());
		}
		
		try {
			service.setNodeInstanceState(IdConverter.nodeInstanceIDtoURI(this.nodeInstanceID), stateQName);
			
			// SimpleXLink xLink = new
			// SimpleXLink(LinkBuilder.linkToNodeInstanceState(uriInfo,
			// nodeInstanceID), "NodeInstance: " + nodeInstanceID + " State");
			return Response.ok().build();
		} catch (final ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, "Specified nodeInstance with id: " + this.nodeInstanceID + " doesn't exist");
		}
		
	}
	
}
