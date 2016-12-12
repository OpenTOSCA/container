package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.PlanInvocationEngineHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * TODO implement
 * 
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class PlanInstanceLogs {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceLogs.class);
	
	private final CSARID csarID;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final String correlationID;
	
	private UriInfo uriInfo;
	
	
	public PlanInstanceLogs(CSARID csarID, QName serviceTemplateID, int serviceTemplateInstanceId, String correlationID) {
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
		StringBuilder builder = new StringBuilder();
		builder.append("<logs>");
		
		Map<String, String> msgs = PlanInvocationEngineHandler.planInvocationEngine.getPlanLogHandler().getLogsOfPlanInstance(correlationID);
		for (String millis : msgs.keySet()) {
			builder.append("<LogEntry>");
			builder.append("<Millis>");
			builder.append(millis);
			builder.append("</Millis>");
			builder.append("<Entry>");
			builder.append(msgs.get(millis));
			builder.append("</Entry>");
			builder.append("</LogEntry>");
		}
		
		builder.append("</logs>");
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
		JsonArray logs = new JsonArray();
		json.add("PlanLogs", logs);
		
		Map<String, String> msgs = PlanInvocationEngineHandler.planInvocationEngine.getPlanLogHandler().getLogsOfPlanInstance(correlationID);
		for (String millis : msgs.keySet()) {
			JsonObject entry = new JsonObject();
			entry.addProperty("Millisecods", millis);
			entry.addProperty("Entry", msgs.get(millis));
			logs.add(entry);
		}
		return Response.ok(json.toString()).build();
	}
	
	@POST
	@Consumes(ResourceConstants.TOSCA_XML)
	@Produces(ResourceConstants.TOSCA_XML)
	public Response postLogEntry(@Context UriInfo uriInfo, String xml) throws URISyntaxException, UnsupportedEncodingException {
		
		String logEntry = xml.substring(5, xml.length() - 6);
		
		PlanInvocationEngineHandler.planInvocationEngine.getPlanLogHandler().log(correlationID, logEntry);
		
		return Response.ok().build();
	}
}
