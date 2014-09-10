package org.opentosca.containerapi.resources.csar;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This represents the PublicPlans of a specific type.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARPublicPlansResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARPublicPlansResource.class);
	
	// If the csarID is null, there is no CSAR file stored in the Container
	private final CSARID csarID;
	private final String publicPlanType;
	
	
	public CSARPublicPlansResource(CSARID csarID, String publicPlanType) {
		
		this.csarID = csarID;
		this.publicPlanType = publicPlanType;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			CSARPublicPlansResource.LOG.error("The ToscaEngineService is not alive.");
		}
		
		CSARPublicPlansResource.LOG.info("{} created: {}", this.getClass(), this);
		CSARPublicPlansResource.LOG.debug("Public Plans for requested CSAR: {} and type {}", this.csarID.getFileName(), this.publicPlanType);
	}
	
	/**
	 * Builds the references of the PublicPlans of a CSAR and a plan type
	 * defined due the constructor.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		if (this.csarID == null) {
			return Response.status(404).build();
		}
		
		CSARPublicPlansResource.LOG.debug("Return available management plans for CSAR {} and type {} .", this.csarID, this.publicPlanType);
		
		References refs = new References();
		
		PublicPlanTypes type = PublicPlanTypes.isPlanTypeEnumRepresentation(this.publicPlanType);
		LinkedHashMap<Integer, PublicPlan> linkedMapOfPublicPlans = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPublicPlans(this.csarID).get(type);
		
		CSARPublicPlansResource.LOG.debug("Getting the list of PublicPlan of the type \"" + type + "\" for CSAR \"" + this.csarID.getFileName() + "\".");
		
		for (Integer itr : linkedMapOfPublicPlans.keySet()) {
			PublicPlan plan = linkedMapOfPublicPlans.get(itr);
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), Integer.toString(itr)), XLinkConstants.SIMPLE, plan.getPlanID().toString()));
		}
		
		CSARPublicPlansResource.LOG.info("Number of References in Root: {}", refs.getReference().size());
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns a PublicPlan for a given Index.
	 * 
	 * @param publicPlanIndex
	 * @return the PublicPlan
	 */
	@GET
	@Path("{PublicPlanIndex}")
	@Produces(ResourceConstants.TOSCA_XML)
	public PublicPlan getPublicPlan(@PathParam("PublicPlanIndex") String publicPlanIndex) {
		
		PublicPlanTypes type = PublicPlanTypes.isPlanTypeEnumRepresentation(this.publicPlanType);
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			CSARPublicPlansResource.LOG.error("ToscaEngineService is null!");
			return null;
		}
		
		Map<PublicPlanTypes, LinkedHashMap<Integer, PublicPlan>> map = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getCSARIDToPublicPlans(this.csarID);
		
		if (null == map) {
			CSARPublicPlansResource.LOG.error("For CSARID \"" + this.csarID.toString() + "\" there is no stored informations about plans.");
			return null;
		}
		
		Integer index = Integer.parseInt(publicPlanIndex);
		
		if ((null == index) || (index < 0)) {
			CSARPublicPlansResource.LOG.error("The given index is not valid!");
			return null;
		}
		
		CSARPublicPlansResource.LOG.debug("Return the PublicPlan with index " + index + " with type \"" + type + "\" for CSAR \"" + this.csarID.toString() + "\".");
		
		PublicPlan plan = map.get(type).get(index);
		
		if (null == plan) {
			CSARPublicPlansResource.LOG.error("The PublicPlan could not be retrieved.");
			return null;
		}
		
		return plan;
	}
}
