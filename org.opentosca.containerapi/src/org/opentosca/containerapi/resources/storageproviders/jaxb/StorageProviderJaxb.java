package org.opentosca.containerapi.resources.storageproviders.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.containerapi.resources.storageproviders.AvailableStorageProviderResource;
import org.opentosca.containerapi.resources.storageproviders.AvailableStorageProvidersResource;
import org.opentosca.containerapi.resources.storageproviders.DefaultStorageProviderResource;

/**
 * JAXB annotated class that represents a storage provider.<br />
 * It will be created by {@link JaxbFactory} and used in
 * {@link AvailableStorageProvidersResource},
 * {@link AvailableStorageProviderResource} and
 * {@link DefaultStorageProviderResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@XmlRootElement(name = "StorageProvider")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageProviderJaxb {
	
	@XmlAttribute(name = "id")
	private String id;
	
	@XmlAttribute(name = "name")
	private String name;
	
	/**
	 * We use the wrapper class {@link Boolean}, because we doesn't want to show
	 * a XML element (with value {@code false}) in case boolean was not set.
	 */
	
	@XmlElement(name = "Active")
	private Boolean active;
	
	@XmlElement(name = "Ready")
	private Boolean ready;
	
	@XmlElement(name = "Default")
	private Boolean def;
	
	@XmlElement(name = "NeedsCredentials")
	private Boolean needsCredentials;
	
	@XmlElement(name = "CredentialsIdentityName")
	private String credentialsIdentityName;
	
	@XmlElement(name = "CredentialsKeyName")
	private String credentialsKeyName;
	
	@XmlElement(name = "HasCredentials")
	private Boolean hasCredentials;
	
	
	/**
	 * @return ID of this storage provider.
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of this storage provider.
	 * 
	 * @param id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return Name of this storage provider.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name of this storage provider.
	 * 
	 * @param name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return {@code true} if this storage provider is active, otherwise
	 *         {@code false}.
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/**
	 * Sets that this storage provider is active or not.
	 * 
	 * @param active to set.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return {@code true} if this storage provider is ready, otherwise
	 *         {@code false}.
	 */
	public boolean isReady() {
		return this.ready;
	}
	
	/**
	 * Sets that this storage provider is ready or not.
	 * 
	 * @param ready to set.
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	/**
	 * @return {@code true} if this storage provider is the default storage
	 *         provider, otherwise {@code false}.
	 */
	public boolean isDefault() {
		return this.def;
	}
	
	/**
	 * Sets that this storage provider is the default storage provider or not.
	 * 
	 * @param def to set.
	 */
	public void setDefault(boolean def) {
		this.def = def;
	}
	
	/**
	 * 
	 * @return {@code true} if this storage provider needs credentials,
	 *         otherwise {@code false}.
	 */
	public boolean getNeedsCredentials() {
		return this.needsCredentials;
	}
	
	/**
	 * Sets that this storage provider needs credentials or not.
	 * 
	 * @param needsCredentials to set.
	 */
	public void setNeedsCredentials(boolean needsCredentials) {
		this.needsCredentials = needsCredentials;
	}
	
	/**
	 * @return Name of the credentials identity of this storage provider.
	 */
	public String getCredentialsIdentityName() {
		return this.credentialsIdentityName;
	}
	
	/**
	 * Sets {@code credentialsIdentityName} as the credentials identity name of
	 * this storage provider.
	 */
	public void setCredentialsIdentityName(String credentialsIdentityName) {
		this.credentialsIdentityName = credentialsIdentityName;
	}
	
	/**
	 * @return Name of the credentials key of this storage provider.
	 */
	public String getCredentialsKeyName() {
		return this.credentialsKeyName;
	}
	
	/**
	 * Sets {@code credentialsKeyName} as the credentials key name of this
	 * storage provider.
	 */
	public void setCredentialsKeyName(String credentialsKeyName) {
		this.credentialsKeyName = credentialsKeyName;
	}
	
	/**
	 * @return {@code true} if this storage provider has credentials set,
	 *         otherwise {@code false}.
	 */
	public Boolean hasCredentials() {
		return this.hasCredentials;
	}
	
	/**
	 * Sets that this storage provider has credentials set or not.
	 * 
	 * @param hasCredentials to set.
	 */
	public void setHasCredentials(boolean hasCredentials) {
		this.hasCredentials = hasCredentials;
	}
	
}
