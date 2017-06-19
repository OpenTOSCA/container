package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances.plans;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInstanceMetaData {
	
	private static final Logger LOG = LoggerFactory.getLogger(PlanInstanceMetaData.class);

	private final CSARID csarID;
	private final QName serviceTemplateID;
	private final int serviceTemplateInstanceId;
	private final String correlationID;

	private UriInfo uriInfo;


	public PlanInstanceMetaData(final CSARID csarID, final QName serviceTemplateID, final int serviceTemplateInstanceId, final String correlationID) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.serviceTemplateInstanceId = serviceTemplateInstanceId;
		this.correlationID = correlationID;
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
	public Response getReferencesXML(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getXMLString()).build();
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
	public Response getReferencesJSON(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getJSONString()).build();
	}

	public References getReferences() {

		final References refs = new References();

		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}

}
