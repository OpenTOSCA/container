package org.opentosca.containerapi.resources.csar;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.opentoscacontrol.service.IOpenToscaControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource represents the active PublicPlans for a CSAR-Instance.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceActivePlansResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARInstanceActivePlansResource.class);
	
	private final CSARID csarID;
	private final int instanceID;
	
	UriInfo uriInfo;
	
	
	public CSARInstanceActivePlansResource(CSARID csarID, int instanceID) {
		this.csarID = csarID;
		this.instanceID = instanceID;
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
		
		CSARInstanceActivePlansResource.LOG.debug("Access active plans at " + uriInfo.getAbsolutePath().toString());
		
		if (csarID == null) {
			CSARInstanceActivePlansResource.LOG.debug("The CSAR does not exist.");
			return null;
		}
		
		References refs = new References();
		
		IOpenToscaControlService control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		
		for (String correlation : control.getActiveCorrelationsOfInstance(new CSARInstanceID(csarID, instanceID))) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), correlation), XLinkConstants.SIMPLE, correlation));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns the PublicPlan for the CorrelationID
	 * 
	 * @param correlationID
	 * @return the PublicPlan for the CorrelationID
	 */
	// @GET
	// @Path("{CorrelationID}")
	// @Produces(ResourceConstants.TOSCA_XML)
	// public TPlanDTO getInstance(@PathParam("CorrelationID") String
	// correlationID) {
	// CSARInstanceActivePlansResource.LOG.debug("Return plan for correlation "
	// + correlationID);
	// IOpenToscaControlService control =
	// IOpenToscaControlServiceHandler.getOpenToscaControlService();
	//
	// return control.getActivePlanOfInstance(new CSARInstanceID(csarID,
	// instanceID), correlationID);
	// }
	
	/**
	 * Returns the plan information from history.
	 * 
	 * @param uriInfo
	 * @return Response
	 * @throws URISyntaxException
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_JSON)
	public Response getPlanJSON(@Context UriInfo uriInfo, @PathParam("CorrelationID") String correlationID) throws URISyntaxException {
		
		if (null != CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID) && CSARInstanceManagementHandler.csarInstanceManagement.getFinishedCorrelations(csarID).contains(correlationID)) {
			
			String url = Utilities.buildURI(uriInfo.getBaseUri().toString(), "CSARs/" + csarID.getFileName() + "/Instances/" + instanceID + "/PlanResults/" + correlationID);
			URI uri = new URI(url);
			LOG.trace("Redirect for correlation {}:\n{}", correlationID, uri);
			return Response.seeOther(uri).build();
			
		} else if (null != CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csarID) && CSARInstanceManagementHandler.csarInstanceManagement.getActiveCorrelations(csarID).contains(correlationID)) {
			
			LOG.trace("Pending for correlation {}", correlationID);
			return Response.ok("{\"result\":{\"status\":\"PENDING\"}}", MediaType.APPLICATION_JSON).build();
			
		} else {
			
			LOG.warn("Correlation not known for corr ", correlationID);
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"result\":\"Given correlation is not known\"}").build();
			
		}
		
		// CSARInstanceActivePlansResource.LOG.debug("Return plan for
		// correlation " + correlationID);
		// TPlanDTO plan =
		// IOpenToscaControlServiceHandler.getOpenToscaControlService().getActivePlanOfInstance(new
		// CSARInstanceID(csarID, instanceID), correlationID);
		//
		// JsonObject json = new JsonObject();
		// json.addProperty("ID", plan.getId().toString());
		// json.addProperty("Name", plan.getName());
		// json.addProperty("PlanType", plan.getPlanType());
		// json.addProperty("PlanLanguage", plan.getPlanLanguage());
		//
		// JsonArray input = new JsonArray();
		// try {
		// for (TParameterDTO param :
		// plan.getInputParameters().getInputParameter()) {
		// JsonObject paramObj = new JsonObject();
		// JsonObject paramDetails = new JsonObject();
		// paramDetails.addProperty("Name", param.getName());
		// paramDetails.addProperty("Type", param.getType());
		// paramDetails.addProperty("Value", param.getValue());
		// paramDetails.addProperty("Required", param.getRequired().value());
		// paramObj.add("InputParameter", paramDetails);
		// input.add(paramObj);
		// }
		// } catch (NullPointerException e) {
		// }
		// json.add("InputParameters", input);
		//
		// JsonArray output = new JsonArray();
		// try {
		// for (TParameterDTO param :
		// plan.getOutputParameters().getOutputParameter()) {
		// JsonObject paramObj = new JsonObject();
		// JsonObject paramDetails = new JsonObject();
		// paramDetails.addProperty("Name", param.getName());
		// paramDetails.addProperty("Type", param.getType());
		// paramDetails.addProperty("Value", param.getValue());
		// paramDetails.addProperty("Required", param.getRequired().value());
		// paramObj.add("OutputParameter", paramDetails);
		// output.add(paramObj);
		// }
		// } catch (NullPointerException e) {
		// }
		// json.add("OutputParameters", output);
		//
		// JsonObject planModelReference = new JsonObject();
		// // planModelReference.addProperty("Reference",
		// // event.getPlanModelReference().getReference());
		// json.add("PlanModelReference", planModelReference);
		//
		// return Response.ok(json.toString()).build();
	}
	
}
