package org.opentosca.containerapi.resources.csar.servicetemplate.relationshiptemplate.instances;

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

import org.opentosca.containerapi.instancedata.exception.GenericRestException;
import org.opentosca.containerapi.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.model.instancedata.IdConverter;

import com.google.gson.JsonObject;

/**
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */
public class RelationshipTemplateInstanceStateResource {
	
	
	private int relationInstanceID;
	
	
	public RelationshipTemplateInstanceStateResource(int id) {
		relationInstanceID = id;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetXML() {
		
		String idr = getState();
		
		return Response.ok(idr).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetJSON() {
		
		String idr = getState();
		
		JsonObject json = new JsonObject();
		json.addProperty("state", idr);
		
		return Response.ok(json.toString()).build();
	}
	
	public String getState() {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
		try {
			QName state = service.getRelationInstanceState(IdConverter.relationInstanceIDtoURI(relationInstanceID));
			if (state != null) {
				return state.toString();
			} else {
				return null;
			}
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, "Specified relationInstance with id: " + relationInstanceID + " doesn't exist");
		}
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_XML)
	public Response setState(@Context UriInfo uriInfo, String state) {
		IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
		
		QName stateQName = null;
		try {
			stateQName = QName.valueOf(state);
			
		} catch (Exception e1) {
			throw new GenericRestException(Status.BAD_REQUEST, "Error converting parameter state: " + e1.getMessage());
		}
		
		try {
			service.setRelationInstanceState(IdConverter.relationInstanceIDtoURI(relationInstanceID), stateQName);
			
			//			SimpleXLink xLink = new SimpleXLink(LinkBuilder.linkToNodeInstanceState(uriInfo, nodeInstanceID), "NodeInstance: " + nodeInstanceID + " State");
			return Response.ok().build();
		} catch (ReferenceNotFoundException e) {
			throw new GenericRestException(Status.NOT_FOUND, "Specified relationInstance with id: " + relationInstanceID + " doesn't exist");
		}
		
	}
	
}
