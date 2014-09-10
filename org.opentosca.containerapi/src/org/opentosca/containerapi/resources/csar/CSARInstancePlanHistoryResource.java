package org.opentosca.containerapi.resources.csar;

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
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstancePlanHistoryResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARInstancePlanHistoryResource.class);
	
	private final CSARID csarID;
	private final int instanceID;
	
	
	public CSARInstancePlanHistoryResource(CSARID csarID, int instanceID) {
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
	public Response getReferences(@Context UriInfo uriInfo) {
		
		CSARInstancePlanHistoryResource.LOG.debug("Access the plan history at " + uriInfo.getAbsolutePath().toString());
		
		if (this.csarID == null) {
			CSARInstancePlanHistoryResource.LOG.debug("The CSAR does not exist.");
			return Response.status(404).build();
		}
		
		References refs = new References();
		
		ICSARInstanceManagementService manager = CSARInstanceManagementHandler.csarInstanceManagement;
		
		for (String correlation : manager.getCorrelationsOfInstance(this.csarID, new CSARInstanceID(this.csarID, this.instanceID))) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), correlation), XLinkConstants.SIMPLE, correlation));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns a PublicPlan for a CorrelationID.
	 * 
	 * @param correlationID
	 * @return the PublicPlan for the CorrelationID
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_XML)
	public PublicPlan getInstance(@PathParam("CorrelationID") String correlationID) {
		CSARInstancePlanHistoryResource.LOG.debug("Return plan for correlation " + correlationID);
		return CSARInstanceManagementHandler.csarInstanceManagement.getPublicPlanFromHistory(correlationID);
	}
	
}
