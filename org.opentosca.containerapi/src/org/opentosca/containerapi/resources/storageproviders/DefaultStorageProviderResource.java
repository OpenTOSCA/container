package org.opentosca.containerapi.resources.storageproviders;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opentosca.containerapi.resources.storageproviders.jaxb.JaxbFactory;
import org.opentosca.containerapi.resources.storageproviders.jaxb.StorageProviderJaxb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource of the default storage provider of the Core File Service (regardless
 * if it's available or not).<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class DefaultStorageProviderResource {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultStorageProviderResource.class);
	
	
	/**
	 * Creates a {@link DefaultStorageProviderResource}.
	 */
	public DefaultStorageProviderResource() {
		DefaultStorageProviderResource.LOG.info("{} created: {}", this.getClass(), this);
	}
	
	/**
	 * @return {@link StorageProviderJaxb} containing the default storage
	 *         provider that will be showed on this resource.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public StorageProviderJaxb getDefaultStorageProviderXML() {
		return JaxbFactory.createStorageProviderJaxbOfDefaultStorageProvider();
	}
	
}
