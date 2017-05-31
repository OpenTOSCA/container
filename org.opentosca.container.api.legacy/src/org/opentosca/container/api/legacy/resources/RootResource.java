package org.opentosca.container.api.legacy.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RootResource at Uri: host/containerapi
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 *
 */
@Path("")
public class RootResource {
	
	final private static Logger LOG = LoggerFactory.getLogger(RootResource.class);
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	
	public RootResource() {
		RootResource.LOG.debug("{} created: {}", this.getClass(), this);
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML() {
		
		return Response.ok(this.getRefs().getXMLString()).build();
		
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON() {
		
		return Response.ok(this.getRefs().getJSONString()).build();
		
	}
	
	private References getRefs() {
		
		References refs = new References();
		
		refs.getReference();
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "CSARs"), XLinkConstants.SIMPLE, "CSARs"));
		
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(),
		// "AllDefinitions"), XLinkConstants.SIMPLE, "AllDefinitions"));
		// refs.getReference().add(new
		// Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(),
		// "CSARFiles"), XLinkConstants.SIMPLE, "CSARFiles"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "CSARControl"), XLinkConstants.SIMPLE, "CSARControl"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "Credentials"), XLinkConstants.SIMPLE, "Credentials"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "StorageProviders"), XLinkConstants.SIMPLE, "StorageProviders"));
		
		// InstanceData API and PortabilityAPI
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "instancedata"), XLinkConstants.SIMPLE, "instancedata"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "portability"), XLinkConstants.SIMPLE, "portability"));
		
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "SmartServices"), XLinkConstants.SIMPLE, "SmartServices"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "planbuilder"), XLinkConstants.SIMPLE, "planbuilder"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "marketplace"), XLinkConstants.SIMPLE, "marketplace"));
		refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "packager"), XLinkConstants.SIMPLE, "packager"));

		RootResource.LOG.debug("Number of References in Root: {}", refs.getReference().size());
		// selflink
		
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
	}
}
