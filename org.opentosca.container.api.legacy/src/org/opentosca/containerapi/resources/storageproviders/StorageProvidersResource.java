package org.opentosca.containerapi.resources.storageproviders;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main resource of the storage providers of the Core File Service. It shows
 * only XLink references to sub resources.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@Path("/StorageProviders")
public class StorageProvidersResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(StorageProvidersResource.class);
	
	
	/**
	 * Creates a {@link StorageProvidersResource}.
	 */
	public StorageProvidersResource() {
		StorageProvidersResource.LOG.info("{} created: {}", this.getClass(), this);
	}
	
	/**
	 * @param uriInfo
	 * @return 200 (OK) with XLink references to available storage providers and
	 *         default storage provider resources (and self reference).
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		References refs = new References();
		
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Available"), XLinkConstants.SIMPLE, "Available"));
		refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Default"), XLinkConstants.SIMPLE, "Default"));
		refs.getReference().add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return Response.ok(refs.getXMLString()).build();
	}
	
	/**
	 * @return {@link AvailableStorageProvidersResource}
	 */
	@Path("Available")
	public AvailableStorageProvidersResource getAvailableStorageProvidersResource() {
		return new AvailableStorageProvidersResource();
	}
	
	/**
	 * @return {@link DefaultStorageProviderResource}
	 */
	@Path("Default")
	public DefaultStorageProviderResource getDefaultStorageProviderResource() {
		return new DefaultStorageProviderResource();
	}
	
}
