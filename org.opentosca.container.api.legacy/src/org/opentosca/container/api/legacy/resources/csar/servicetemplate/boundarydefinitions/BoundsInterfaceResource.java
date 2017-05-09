package org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundsInterfaceResource {

	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceResource.class);
	private final CSARID csarID;
	private QName serviceTemplateID = null;
	private final String intName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceResource(final CSARID csarID, final QName serviceTemplateID, final String intName) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.intName = intName;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			LOG.error("The ToscaEngineService is not alive.");
		}
	}
	
	/**
	 * Builds the references of the Boundary Definitions of a CSAR.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getXMLString()).build();
	}
	
	/**
	 * Builds the references of the Boundary Definitions of a CSAR.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getJSONString()).build();
	}
	
	private References getReferences() {
		
		final References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Operations"), XLinkConstants.SIMPLE, "Operations"));
		
		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns the Interface Operations for a given Interface name.
	 *
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("Operations")
	public BoundsInterfaceOperationsResource getPublicPlan(@PathParam("InterfaceName") final String intName) {
		return new BoundsInterfaceOperationsResource(this.csarID, this.serviceTemplateID, intName);
	}
	
}
