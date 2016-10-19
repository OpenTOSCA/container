package org.opentosca.containerapi.resources.csar.boundarydefinitions;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSARBoundsInterfaceOperationsResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CSARBoundsInterfaceOperationsResource.class);
	CSARID csarID;
	String intName;
	
	
	public CSARBoundsInterfaceOperationsResource(CSARID csarID, String intName) {
		this.csarID = csarID;
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
	public Response getReferences(@Context UriInfo uriInfo) {
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Operations"), XLinkConstants.SIMPLE, "Operations"));
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Interface Operations for a given Interface name.
	 * 
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("Operations")
	public CSARBoundsInterfaceOperationResource getPublicPlan(@PathParam("InterfaceName") String intName) {
		return new CSARBoundsInterfaceOperationResource(csarID, intName);
	}
	
}
