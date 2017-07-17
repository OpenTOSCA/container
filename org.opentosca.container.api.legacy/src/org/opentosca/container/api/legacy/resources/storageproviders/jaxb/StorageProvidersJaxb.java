package org.opentosca.container.api.legacy.resources.storageproviders.jaxb;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.legacy.resources.storageproviders.AvailableStorageProvidersResource;

/**
 * JAXB annotated class that represents a set of storage providers.<br />
 * It will be created by {@link JaxbFactory} and used in
 * {@link AvailableStorageProvidersResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@XmlRootElement(name = "StorageProviders")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageProvidersJaxb {
	
	@XmlElement(name = "StorageProvider")
	private Set<StorageProviderJaxb> storageProviders;
	
	
	/**
	 * @return Storage providers.
	 */
	public Set<StorageProviderJaxb> getStorageProviders() {
		return this.storageProviders;
	}
	
	/**
	 * Sets the storage providers.
	 * 
	 * @param storageProviders to set.
	 */
	public void setStorageProvider(Set<StorageProviderJaxb> storageProviders) {
		this.storageProviders = storageProviders;
	}
	
}
