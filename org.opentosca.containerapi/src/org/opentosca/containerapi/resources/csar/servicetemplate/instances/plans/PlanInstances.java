package org.opentosca.containerapi.resources.csar.servicetemplate.instances.plans;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

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
public class PlanInstances {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstances.class);
	
	private final CSARID csarID;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	
	UriInfo uriInfo;
	
	
	public PlanInstances(CSARID csarID, QName serviceTemplateID, int serviceTemplateInstanceId) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
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
		
		PlanInstances.LOG.debug("Access active plans at " + uriInfo.getAbsolutePath().toString());
		
		if (csarID == null) {
			PlanInstances.LOG.debug("The CSAR does not exist.");
			return null;
		}
		
		References refs = new References();
		
		IOpenToscaControlService control = IOpenToscaControlServiceHandler.getOpenToscaControlService();
		
		for (String correlation : control.getActiveCorrelationsOfInstance(new CSARInstanceID(csarID, serviceTemplateInstanceId))) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), correlation), XLinkConstants.SIMPLE, correlation));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
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
	public PlanInstance getPlanJSON(@Context UriInfo uriInfo, @PathParam("CorrelationID") String correlationID) throws URISyntaxException {
		return new PlanInstance(csarID, serviceTemplateID, serviceTemplateInstanceId, correlationID);
	}
	
}
