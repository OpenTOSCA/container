package org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions;

import java.util.List;

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

public class BoundsInterfacesResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfacesResource.class);
	private CSARID csarID = null;
	private QName serviceTemplateID = null;

	UriInfo uriInfo;


	public BoundsInterfacesResource(final CSARID csarID, final QName serviceTemplateID) {
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;

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

		final List<String> interfaces = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getBoundaryInterfacesOfCSAR(this.csarID);

		if (null != interfaces) {
			for (final String intf : interfaces) {
				refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), intf), XLinkConstants.SIMPLE, intf));
			}
		}

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
	@Path("{InterfaceName}")
	public BoundsInterfaceResource getPublicPlan(@PathParam("InterfaceName") final String intName) {
		return new BoundsInterfaceResource(this.csarID, this.serviceTemplateID, intName);
	}

}