package org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

/**
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class BoundsResource {

	private static final Logger LOG = LoggerFactory.getLogger(BoundsResource.class);
	
	private final CSARID csarID;
	private final QName serviceTemplateID;
	
	UriInfo uriInfo;
	
	
	public BoundsResource(final CSARID csarid, final QName serviceTemplateID) {
		
		this.csarID = csarid;
		this.serviceTemplateID = serviceTemplateID;
		
		if (null == ToscaServiceHandler.getToscaEngineService()) {
			LOG.error("The ToscaEngineService is not alive.");
		}
		
		LOG.info("{} created: {}", this.getClass(), this);
		LOG.debug("Public Plans for requested CSAR: {}", this.csarID.getFileName());
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
		
		if (this.csarID == null) {
			return null;
		}
		
		LOG.trace("Return Boundary Definitions for CSAR {}.", this.csarID);
		
		final References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Properties"), XLinkConstants.SIMPLE, "Properties"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "PropertyConstraints"), XLinkConstants.SIMPLE, "PropertyConstraints"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Requirements"), XLinkConstants.SIMPLE, "Requirements"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Capabilities"), XLinkConstants.SIMPLE, "Capabilities"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Policies"), XLinkConstants.SIMPLE, "Policies"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, "Interfaces"), XLinkConstants.SIMPLE, "Interfaces"));
		
		// selflink
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return refs;
	}
	
	/**
	 * Returns the Boundary Definitions Properties. *
	 *
	 * @param uriInfo
	 * @return Response
	 */
	
	@Path("Properties")
	public BoundsProperties getProperties(@Context final UriInfo uriInfo) {
		return new BoundsProperties(this.csarID, this.serviceTemplateID);
	}
	
	/**
	 * Returns the Boundary Definitions Properties. TODO not yet implemented
	 * yet, thus, just returns itself.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("PropertyConstraints")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getPropertyConstraints(@Context final UriInfo uriInfo) {
		
		if (this.csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions for CSAR {}.", this.csarID);
		
		final References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Properties. TODO not yet implemented
	 * yet, thus, just returns itself.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("Requirements")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getRequirements(@Context final UriInfo uriInfo) {
		
		if (this.csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions for CSAR {}.", this.csarID);
		
		final References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Properties. TODO not yet implemented
	 * yet, thus, just returns itself.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("Capabilities")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getCapabilities(@Context final UriInfo uriInfo) {
		
		if (this.csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions for CSAR {}.", this.csarID);
		
		final References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns the Boundary Definitions Properties. TODO not yet implemented
	 * yet, thus, just returns itself.
	 *
	 * @param uriInfo
	 * @return Response
	 */
	@GET
	@Path("Policies")
	@Produces(ResourceConstants.LINKED_XML)
	public Response getPolicies(@Context final UriInfo uriInfo) {
		
		if (this.csarID == null) {
			return Response.status(404).build();
		}
		
		LOG.trace("Return Boundary Definitions for CSAR {}.", this.csarID);
		
		final References refs = new References();
		// selflink
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * Returns a PublicPlan for a given Index.
	 *
	 * @param planName
	 * @return the PublicPlan
	 */
	@Path("Interfaces")
	public BoundsInterfacesResource getInterfaces(@Context final UriInfo uriInfo) {
		return new BoundsInterfacesResource(this.csarID, this.serviceTemplateID);
	}
	
}
