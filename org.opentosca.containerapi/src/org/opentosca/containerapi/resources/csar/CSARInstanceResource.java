package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource represents a CSAR-Instance.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(CSARInstancesResource.class);

    // If the csarID is null, there is no CSAR file stored in the Container
    private final CSARID csarID;
    private final int instanceID;

    public CSARInstanceResource(CSARID csarID, String instanceID) {
	if (null == csarID) {
	    this.csarID = null;
	    this.instanceID = -1;
	    CSARInstanceResource.LOG.error("{} created: {}", this.getClass(), "but the CSAR does not exist");
	} else {
	    if ((null == instanceID) || instanceID.equals("")) {
		CSARInstanceResource.LOG.error("CSAR Instance " + instanceID + " does not exit for requested CSAR: {}",
		    csarID.getFileName());
		this.csarID = null;
		this.instanceID = -1;
	    } else {
		this.csarID = csarID;
		this.instanceID = Integer.parseInt(instanceID);
		CSARInstanceResource.LOG.debug("{} created: {}", this.getClass(), csarID);
		CSARInstanceResource.LOG.debug("CSAR Instance " + instanceID + " for requested CSAR: {}",
		    this.csarID.getFileName());
	    }
	}
    }

    /**
     * Produces the xml which lists the links to the History and the active
     * plans.
     * 
     * @param uriInfo
     * @return The response with the legal PublicPlanTypes.
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferences(@Context UriInfo uriInfo) {

	CSARInstanceResource.LOG.debug("Access the CSAR instance at " + uriInfo.getAbsolutePath().toString());

	if (csarID == null) {
	    CSARInstanceResource.LOG.debug("The CSAR does not exist.");
	    return Response.status(404).build();
	}

	// selflink
	References refs = new References();
	refs.getReference()
	.add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "activePublicPlans"),
	    XLinkConstants.SIMPLE, "history"));
	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "history"),
	    XLinkConstants.SIMPLE, "history"));
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Post of a PublicPlan. Dedicated to OTHERMANAGEMENT and TERMINATION.
     * 
     * @param transferElement
     * @return Response
     */
    @POST
    @Consumes(ResourceConstants.TOSCA_XML)
    public Response postManagementPlan(JAXBElement<TPlanDTO> transferElement) {

	CSARInstanceResource.LOG.debug(
	    "Received a management request to invoke the plan for Instance " + instanceID + " of CSAR " + csarID);

	TPlanDTO plan = transferElement.getValue();
	// QName id = plan.getId();
	// QName qname = new QName(id.substring(1, id.indexOf("}")),
	// id.substring(id.indexOf("}") + 1, id.length()));
	// plan.setPlanID(qname);
	// plan.setInternalInstanceInternalID(instanceID);

	CSARInstanceResource.LOG.debug("Post of the PublicPlan " + plan.getId());
	IOpenToscaControlServiceHandler.getOpenToscaControlService().invokePlanInvocation(csarID, instanceID, plan);

	return Response.ok("invoked").build();
    }

    /**
     * This returns the History object.
     * 
     * @return the History representation
     */
    @Path("history")
    @Produces(ResourceConstants.LINKED_XML)
    public Object getInstanceHistory() {
	CSARInstanceResource.LOG.debug("Access history");
	return new CSARInstancePlanHistoryResource(csarID, instanceID);
    }

    /**
     * This returns the active plans.
     * 
     * @return active plans representation
     */
    @Path("activePublicPlans")
    @Produces(ResourceConstants.LINKED_XML)
    public Object getInstanceActivePublicPlans() {
	CSARInstanceResource.LOG.debug("Access active PublicPlans");
	return new CSARInstanceActivePlansResource(csarID, instanceID);
    }
}
