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
import javax.xml.namespace.QName;

import org.opentosca.containerapi.Activator;
import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.toscaengine.service.IToscaReferenceMapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
public class CSARPlansResource {

    private static final Logger LOG = LoggerFactory.getLogger(CSARPlansResource.class);

    // If the csarID is null, there is no CSAR file stored in the Container
    private final CSARID csarID;

    public CSARPlansResource(CSARID csarID) {

	this.csarID = csarID;

	if (null == ToscaServiceHandler.getToscaEngineService()) {
	    CSARPlansResource.LOG.error("The ToscaEngineService is not alive.");
	}

	CSARPlansResource.LOG.info("{} created: {}", this.getClass(), this);
	CSARPlansResource.LOG.debug("Public Plans for requested CSAR: {}", this.csarID.getFileName());
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

	if (csarID == null) {
	    return Response.status(404).build();
	}

	CSARPlansResource.LOG.debug("Return available management plans for CSAR {}.", csarID);

	References refs = new References();

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> linkedMapOfPublicPlans = ToscaServiceHandler
	    .getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);

	for (PlanTypes type : PlanTypes.values()) {
	    for (QName planId : linkedMapOfPublicPlans.get(type).keySet()) {
		// TPlan plan = linkedMapOfPublicPlans.get(type).get(name);
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), planId.getLocalPart()),
		    XLinkConstants.SIMPLE, planId.getLocalPart()));
	    }
	}

	CSARPlansResource.LOG.trace("Number of References in Root: {}", refs.getReference().size());

	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns a PublicPlan for a given Index.
     * 
     * @param planName
     * @return the PublicPlan
     */
    @GET
    @Path("{PlanName}")
    @Produces(ResourceConstants.TOSCA_XML)
    public TPlanDTO getPublicPlan(@PathParam("PlanName") String planName) {

	// PlanTypes type =
	// PlanTypes.isPlanTypeEnumRepresentation(publicPlanType);

	if (null == ToscaServiceHandler.getToscaEngineService()) {
	    CSARPlansResource.LOG.error("ToscaEngineService is null!");
	    return null;
	}

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> map = ToscaServiceHandler.getToscaEngineService()
	    .getToscaReferenceMapper().getCSARIDToPlans(csarID);

	if (null == map) {
	    CSARPlansResource.LOG
	    .error("For CSARID \"" + csarID.toString() + "\" there is no stored informations about plans.");
	    return null;
	}

	if ((null == planName) || planName.isEmpty()) {
	    CSARPlansResource.LOG.error("The given plan name is not valid!");
	    return null;
	}

	CSARPlansResource.LOG
	.debug("Return the plan with name " + planName + "\" for CSAR \"" + csarID.toString() + "\".");

	Map<PlanTypes, LinkedHashMap<QName, TPlan>> linkedMapOfPublicPlans = ToscaServiceHandler
	    .getToscaEngineService().getToscaReferenceMapper().getCSARIDToPlans(csarID);
	TPlan plan = null;
	for (PlanTypes type : PlanTypes.values()) {
	    if (linkedMapOfPublicPlans.get(type).containsKey(planName)) {
		plan = linkedMapOfPublicPlans.get(type).get(planName);
	    }
	}

	if (null == plan) {
	    CSARPlansResource.LOG.error("The plan could not be retrieved.");
	    return null;
	}

	BundleContext context = Activator.getContext();
	ServiceReference<IToscaReferenceMapper> tmpHttpService = context.getServiceReference(IToscaReferenceMapper.class);
	IToscaReferenceMapper service = context.getService(tmpHttpService);

	return new TPlanDTO(plan, service.getNamespaceOfPlan(csarID, planName));
    }
}
