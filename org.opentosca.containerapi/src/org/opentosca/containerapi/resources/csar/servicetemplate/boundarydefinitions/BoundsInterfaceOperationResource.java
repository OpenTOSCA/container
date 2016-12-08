package org.opentosca.containerapi.resources.csar.servicetemplate.boundarydefinitions;

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

public class BoundsInterfaceOperationResource {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceOperationResource.class);
	CSARID csarID;
	String intName;
	String opName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceOperationResource(CSARID csarID, String intName, String op) {
		
		this.csarID = csarID;
		this.intName = intName;
		opName = op;
		
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
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "NodeOperation"), XLinkConstants.SIMPLE, "NodeOperation"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "RelationshipOperation"), XLinkConstants.SIMPLE, "RelationshipOperation"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Plan"), XLinkConstants.SIMPLE, "Plan"));
		
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
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("NodeOperation")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getNodeOperationXML(@Context UriInfo uriInfo) {
		
		References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("NodeOperation")
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getNodeOperationJSON(@Context UriInfo uriInfo) {
		
		References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getJSONString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("RelationshipOperation")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getRelationshipOperationXML(@Context UriInfo uriInfo) {
		
		References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("RelationshipOperation")
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getRelationshipOperationJSON(@Context UriInfo uriInfo) {
		
		References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getJSONString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Node Operation. TODO not yet implemented
	 * yet, thus, just returns itself.
	 * 
	 * @param uriInfo
	 * @return Response
	 */
	@Path("Plan")
	public BoundsInterfaceOperationPlanResource getPlan(@Context UriInfo uriInfo) {
		return new BoundsInterfaceOperationPlanResource(csarID, intName, opName);
	}
	
}
