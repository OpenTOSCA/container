package org.opentosca.container.api.legacy.resources.credentials.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.legacy.resources.credentials.AllCredentialsResource;
import org.opentosca.container.api.legacy.resources.credentials.CredentialsResource;
import org.opentosca.container.core.model.credentials.Credentials;

/**
 * JAXB annotated class that represents credentials (extends {@link Credentials}
 * ) with additional information if the credentials are set in their storage
 * provider or not.<br />
 * It will be created by
 * {@link org.opentosca.container.api.legacy.resources.credentials.jaxb.JaxbFactory}
 * and used in {@link AllCredentialsResource} and
 * {@link CredentialsResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
@XmlRootElement(name = "Credentials")
@XmlAccessorType(XmlAccessType.FIELD)
public class CredentialsJaxb extends Credentials {

	@XmlElement(name = "InjectedInStorageProvider")
	private boolean injectedInStorageProvider;


	/**
	 * Needed by Jersey.
	 */
	protected CredentialsJaxb() {

	}

	/**
	 * Creates {@code CredentialsJaxb} with all data of {@code credentials}.
	 *
	 * @param credentials
	 */
	public CredentialsJaxb(final Credentials credentials) {
		this.setID(credentials.getID());
		this.setStorageProviderID(credentials.getStorageProviderID());
		this.setIdentity(credentials.getIdentity());
		this.setKey(credentials.getKey());
		this.setDescription(credentials.getDescription());
	}

	/**
	 *
	 * @return {@true} if these credentials are set in the appropriate storage
	 *         provider, otherwise {@code false}.
	 */
	public boolean isInjectedInStorageProvider() {
		return this.injectedInStorageProvider;
	}

	/**
	 * Sets that these credentials are set in a storage provider or not.
	 *
	 * @param injectedInStorageProvider to set.
	 */
	public void setInjectedInStorageProvider(final boolean injectedInStorageProvider) {
		this.injectedInStorageProvider = injectedInStorageProvider;
	}

}
