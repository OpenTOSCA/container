package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
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
	public Response getReferences(@Context UriInfo uriInfo) {
		
		CSARInstanceActivePlansResource.LOG.debug("Access active plans at " + uriInfo.getAbsolutePath().toString());
		
		if (this.csarID == null) {
			CSARInstanceActivePlansResource.LOG.debug("The CSAR does not exist.");
			return Response.status(404).build();
		}
		
		References refs = new References();
		
		IOpenToscaControlService control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		
		for (String correlation : control.getActiveCorrelationsOfInstance(new CSARInstanceID(this.csarID, this.instanceID))) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), correlation), XLinkConstants.SIMPLE, correlation));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the PublicPlan for the CorrelationID
	 * 
	 * @param correlationID
	 * @return the PublicPlan for the CorrelationID
	 */
	@GET
	@Path("{CorrelationID}")
	@Produces(ResourceConstants.TOSCA_XML)
	public PublicPlan getInstance(@PathParam("CorrelationID") String correlationID) {
		CSARInstanceActivePlansResource.LOG.debug("Return plan for correlation " + correlationID);
		IOpenToscaControlService control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		
		return control.getActivePublicPlanOfInstance(new CSARInstanceID(this.csarID, this.instanceID), correlationID);
	}
	
}
