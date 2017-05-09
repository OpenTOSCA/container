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

public class BoundsInterfaceOperationResource {

	private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfaceOperationResource.class);
	CSARID csarID;
	QName serviceTemplateID;
	String intName;
	String opName;
	
	UriInfo uriInfo;
	
	
	public BoundsInterfaceOperationResource(final CSARID csarID, final QName serviceTemplateID, final String intName, final String op) {
		
		this.csarID = csarID;
		this.serviceTemplateID = serviceTemplateID;
		this.intName = intName;
		this.opName = op;
		
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
		
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "NodeOperation"), XLinkConstants.SIMPLE, "NodeOperation"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "RelationshipOperation"), XLinkConstants.SIMPLE, "RelationshipOperation"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Plan"), XLinkConstants.SIMPLE, "Plan"));
		
		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns a PublicPlan for a given Index.
	 *
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("{OperationName}")
	public BoundsInterfaceOperationResource getPublicPlan(@PathParam("OperationName") final String op) {
		return new BoundsInterfaceOperationResource(this.csarID, this.serviceTemplateID, this.intName, op);
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
	public Response getNodeOperationXML(@Context final UriInfo uriInfo) {
		
		final References refs = new References();
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
	public Response getNodeOperationJSON(@Context final UriInfo uriInfo) {
		
		final References refs = new References();
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
	public Response getRelationshipOperationXML(@Context final UriInfo uriInfo) {
		
		final References refs = new References();
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
	public Response getRelationshipOperationJSON(@Context final UriInfo uriInfo) {
		
		final References refs = new References();
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
	public BoundsInterfaceOperationPlanResource getPlan(@Context final UriInfo uriInfo) {
		return new BoundsInterfaceOperationPlanResource(this.csarID, this.serviceTemplateID, this.intName, this.opName);
	}
	
}
