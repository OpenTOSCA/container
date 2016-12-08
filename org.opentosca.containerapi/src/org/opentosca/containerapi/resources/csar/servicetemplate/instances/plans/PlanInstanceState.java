package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class PlanInstanceState {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceState.class);
	
	private final CSARID csarID;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final String correlationID;
	private UriInfo uriInfo;
	
	
	public PlanInstanceState(CSARID csarID, QName serviceTemplateID, int serviceTemplateInstanceId, String correlationID) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.correlationID = correlationID;
	}
	
	/**
	 * Produces the xml which lists the CorrelationIDs of the active
	 * PublicPlans.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		StringBuilder builder = new StringBuilder();
		
		PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement.getPlanForCorrelationId(correlationID);
		
		Boolean finished = hasFinished();
		
		if (null == finished){
			return Response.serverError().build();
		}
		
		builder.append("<PlanInstance planName=\"").append(event.getPlanName()).append("\" correlationID=\"").append(correlationID).append("\"><State>");
		if (finished){
			builder.append("finished");
		} else {
			builder.append("running");
		}
		builder.append("</State></PlanInstance>");
		return Response.ok(builder.toString()).build();
	}
	
	/**
	 * Produces the JSON which lists the links to the History and the active
	 * plans.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		
		JsonObject json = new JsonObject();
		
		PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement.getPlanForCorrelationId(correlationID);
		
		Boolean finished = hasFinished();
		
		if (null == finished){
			return Response.serverError().build();
		}
		
		JsonObject instance = new JsonObject();
		json.add("PlanInstance", instance);
		
		instance.addProperty("PlanName", event.getPlanName());
		instance.addProperty("CorrelationID", correlationID);
		if (finished){
			instance.addProperty("State", "finished");
		} else {
			instance.addProperty("State", "running");
		}
		
		return Response.ok(json.toString()).build();
	}
	
	public Boolean hasFinished() {
		
		// finished
		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID) && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID).contains(correlationID)) {
			return true;
		}
		
		// not finished
		else if (null != CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csarID) && CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csarID).contains(correlationID)) {
			return false;
		}
		
		// ouch
		else {
			return null;
		}
	}
	
}
