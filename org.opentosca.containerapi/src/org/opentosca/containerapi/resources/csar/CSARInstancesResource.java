package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.CSARInstanceManagementHandler;
import org.opentosca.containerapi.osgi.servicegetter.IOpenToscaControlServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The representation lists the IDs of the CSAR-Instances for a CSARID.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class CSARInstancesResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(CSARInstancesResource.class);

	private final CSARID csarID;

	public CSARInstancesResource(CSARID csarID) {
		this.csarID = csarID;
		if (null == csarID) {
			CSARInstancesResource.LOG.debug("{} created: {}", this.getClass(),
					"but the CSAR does not exist");
		} else {
			CSARInstancesResource.LOG.debug("{} created: {}", this.getClass(),
					csarID);
			CSARInstancesResource.LOG.debug(
					"CSAR Instance list for requested CSAR: {}",
					this.csarID.getFileName());
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
			CSARInstancesResource.LOG.debug("The CSAR does not exist.");
			return Response.status(404).build();
		}

		CSARInstancesResource.LOG.debug(
				"Return available instances for CSAR {}.", this.csarID);

		References refs = new References();

		if (null != CSARInstanceManagementHandler.csarInstanceManagement
				.getInstancesOfCSAR(this.csarID)) {
			for (CSARInstanceID id : CSARInstanceManagementHandler.csarInstanceManagement
					.getInstancesOfCSAR(this.csarID)) {
				refs.getReference().add(
						new Reference(Utilities.buildURI(uriInfo
								.getAbsolutePath().toString(), Integer
								.toString(id.getInternalID())),
								XLinkConstants.SIMPLE, Integer.toString(id
										.getInternalID())));
			}
		}

		CSARInstancesResource.LOG.debug("Number of References in Root: {}", refs
				.getReference().size());

		// selflink
		refs.getReference().add(
				new Reference(uriInfo.getAbsolutePath().toString(),
						XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}

	/**
	 * Returns the CSAR-Instance representation for the given ID.
	 * 
	 * @param instanceID
	 * @return the representation object
	 */
	@Path("{instanceID}")
	@Produces(ResourceConstants.LINKED_XML)
	public Object getInstance(@PathParam("instanceID") String instanceID) {
		return new CSARInstanceResource(this.csarID, instanceID);
	}

	/**
	 * PUT for BUILD plans which have no CSAR-Instance-ID yet.
	 * 
	 * @param publicPlanElement
	 *            the BUILD PublicPlan
	 * @return Response
	 */
	@PUT
	@Consumes(ResourceConstants.TOSCA_XML)
	public Response putManagementPlan(JAXBElement<PublicPlan> publicPlanElement) {

		CSARInstancesResource.LOG.debug("Received a build plan for CSAR "
				+ this.csarID);

		PublicPlan publicPlan = publicPlanElement.getValue();

		if (null == publicPlan) {
			LOG.error("The given PublicPlan is null!");
			return Response.status(Status.CONFLICT).build();
		}

		if (null == publicPlan.getPlanID()) {
			LOG.error("The given PublicPlan has no ID!");
			return Response.status(Status.CONFLICT).build();
		}

		if (null == publicPlan.getPlanID().getNamespaceURI()
				|| publicPlan.getPlanID().getNamespaceURI().equals("")) {
			
			String id = publicPlan.getPlanID().getLocalPart();
			QName qname = new QName(id.substring(1, id.indexOf("}")),
					id.substring(id.indexOf("}") + 1, id.length()));
			publicPlan.setPlanID(qname);
		}

		LOG.debug("PublicPlan to invoke: " + publicPlan.getPlanID().toString());

		CSARInstancesResource.LOG.debug("Post of the PublicPlan "
				+ publicPlan.getPlanID().getLocalPart());
		IOpenToscaControlServiceHandler.getOpenToscaControlService()
				.invokePlanInvocation(this.csarID, publicPlan);

		return Response.ok("invoked").build();
	}

}
