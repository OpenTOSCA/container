package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the PublicPlan types for a given CSAR.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARPublicPlanTypesResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARPublicPlanTypesResource.class);
	
	// If the csarID is null, there is no CSAR file stored in the Container
	private final CSARID csarID;
	
	
	public CSARPublicPlanTypesResource(CSARID csarID) {
		this.csarID = csarID;
		if (null == csarID) {
			CSARPublicPlanTypesResource.LOG.info("{} created: {}", this.getClass(), "but the CSAR does not exist");
		} else {
			CSARPublicPlanTypesResource.LOG.info("{} created: {}", this.getClass(), csarID);
			CSARPublicPlanTypesResource.LOG.debug("PublicPlans for requested CSAR: {}", this.csarID.getFileName());
		}
	}
	
	/**
	 * Produces the xml which shows the valid PublicPlanTypes.
	 * 
	 * @param uriInfo
	 * @return The response with the legal PublicPlanTypes.
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		if (this.csarID == null) {
			CSARPublicPlanTypesResource.LOG.debug("The CSAR does not exist.");
			return Response.status(404).build();
		}
		
		CSARPublicPlanTypesResource.LOG.debug("Return available management plan types for CSAR .", this.csarID);
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), PublicPlanTypes.BUILD.toEnumRepresentation()), XLinkConstants.SIMPLE, PublicPlanTypes.BUILD.toEnumRepresentation()));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), PublicPlanTypes.OTHERMANAGEMENT.toEnumRepresentation()), XLinkConstants.SIMPLE, PublicPlanTypes.OTHERMANAGEMENT.toEnumRepresentation()));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), PublicPlanTypes.TERMINATION.toEnumRepresentation()), XLinkConstants.SIMPLE, PublicPlanTypes.TERMINATION.toEnumRepresentation()));
		CSARPublicPlanTypesResource.LOG.info("Number of References in Root: {}", refs.getReference().size());
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * 
	 * @param publicPlanType
	 * @return
	 */
	@Path("{publicPlanType}")
	@Produces(ResourceConstants.LINKED_XML)
	public Object getPublicPlansWithTypeRefernces(@PathParam("publicPlanType") String publicPlanType) {
		
		// check if the given type as a legal one
		boolean legal = false;
		
		for (PublicPlanTypes type : PublicPlanTypes.values()) {
			if (type.toEnumRepresentation().equals(publicPlanType)) {
				legal = true;
			}
		}
		
		if (!legal) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return new CSARPublicPlansResource(this.csarID, publicPlanType);
	}
}
