package org.opentosca.containerapi.resources.csar.servicetemplate.boundarydefinitions;

import java.util.List;

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

public class BoundsInterfaceOperationsResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceOperationsResource.class);
	private CSARID csarID;
	private QName serviceTemplateID;
	private String intName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceOperationsResource(CSARID csarID, QName serviceTemplateID, String intName) {
		
		this.csarID = csarID;
		this.intName = intName;
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
		
		LOG.debug("Find operations for ST {} and Intf {}", serviceTemplateID, intName);
		
		List<String> ops = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper().getBoundaryOperationsOfCSARInterface(csarID, serviceTemplateID, intName);
		
		for (String op : ops) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), op), XLinkConstants.SIMPLE, op));
		}
		
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns a PublicPlan for a given Index.
	 * 
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("{OperationName}")
	public BoundsInterfaceOperationResource getPublicPlan(@PathParam("OperationName") String op) {
		return new BoundsInterfaceOperationResource(csarID, intName, op);
	}
	
}
