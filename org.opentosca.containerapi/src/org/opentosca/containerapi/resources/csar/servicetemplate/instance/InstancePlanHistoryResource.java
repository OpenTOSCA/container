package org.opentosca.containerapi.resources.csar.servicetemplate.instance;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class InstancePlanHistoryResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(InstancePlanHistoryResource.class);
	
	private final CSARID csarID;
	private final int instanceID;
	
	UriInfo uriInfo;
	
	
	public InstancePlanHistoryResource(CSARID csarID, int instanceID) {
		this.csarID = csarID;
		this.instanceID = instanceID;
	}
	
	/**
	 * Produces the xml which lists the CorrelationIDs of the PublicPlans in
	 * History.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(getReferences().getXMLString()).build();
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
		return Response.ok(getReferences().getJSONString()).build();
	}
	
	public References getReferences() {
		
		InstancePlanHistoryResource.LOG.debug("Access the plan history at " + uriInfo.getAbsolutePath().toString());
		
		if (csarID == null) {
			InstancePlanHistoryResource.LOG.debug("The CSAR does not exist.");
			return null;
		}
		
		References refs = new References();
		
		ICSARInstanceManagementService manager = CSARInstanceManagementHandler.csarInstanceManagement;
		
		for (String correlation : manager.getCorrelationsOfInstance(csarID, new CSARInstanceID(csarID, instanceID))) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), correlation), XLinkConstants.SIMPLE, correlation));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns a PlanInvocationEvent for a CorrelationID.
	 * 
	 * @param correlationID
	 * @return the PublicPlan for the CorrelationID
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_XML)
	public PlanInvocationEvent getInstance(@PathParam("CorrelationID") String correlationID) {
		InstancePlanHistoryResource.LOG.debug("Return plan for correlation " + correlationID);
		return CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
	}
	
	/**
	 * Returns the plan information from history.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getPlanJSON(@PathParam("CorrelationID") String correlationID) {
		
		InstancePlanHistoryResource.LOG.debug("Return plan for correlation " + correlationID);
		PlanInvocationEvent event = CSARInstanceManagementHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
		
		JsonObject json = new JsonObject();
		json.addProperty("ID", event.getPlanID().toString());
		json.addProperty("Name", event.getPlanName());
		json.addProperty("PlanType", event.getPlanType());
		json.addProperty("PlanLanguage", event.getPlanLanguage());
		
		JsonArray input = new JsonArray();
		try {
			for (TParameterDTO param : event.getInputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Value", param.getValue());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("InputParameter", paramDetails);
				input.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		json.add("InputParameters", input);
		
		JsonArray output = new JsonArray();
		try {
			for (TParameterDTO param : event.getOutputParameter()) {
				JsonObject paramObj = new JsonObject();
				JsonObject paramDetails = new JsonObject();
				paramDetails.addProperty("Name", param.getName());
				paramDetails.addProperty("Type", param.getType());
				paramDetails.addProperty("Value", param.getValue());
				paramDetails.addProperty("Required", param.getRequired().value());
				paramObj.add("OutputParameter", paramDetails);
				output.add(paramObj);
			}
		} catch (NullPointerException e) {
		}
		json.add("OutputParameters", output);
		
		JsonObject planModelReference = new JsonObject();
		// planModelReference.addProperty("Reference",
		// event.getPlanModelReference().getReference());
		json.add("PlanModelReference", planModelReference);
		
		return Response.ok(json.toString()).build();
	}
	
}
