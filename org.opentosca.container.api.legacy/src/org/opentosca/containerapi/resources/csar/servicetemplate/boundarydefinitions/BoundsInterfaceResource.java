package org.opentosca.containerapi.resources.csar.servicetemplate.boundarydefinitions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundsInterfaceResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceResource.class);
	private CSARID csarID;
	private QName serviceTemplateID = null;
	private String intName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceResource(CSARID csarID, QName serviceTemplateID, String intName) {
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
	public Response getReferencesXML(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(getReferences().getXMLString()).build();
	}
	
	/**
	 * Builds the references of the Boundary Definitions of a CSAR.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(getReferences().getJSONString()).build();
	}
	
	private References getReferences() {
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Operations"), XLinkConstants.SIMPLE, "Operations"));
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns the Interface Operations for a given Interface name.
	 * 
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("Operations")
	public BoundsInterfaceOperationsResource getPublicPlan(@PathParam("InterfaceName") String intName) {
		return new BoundsInterfaceOperationsResource(csarID, serviceTemplateID, intName);
	}
	
}
