package org.opentosca.container.api.legacy.resources.credentials.jaxb;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.legacy.resources.credentials.AllCredentialsResource;

/**
 * JAXB annotated class that represents a set of credentials.<br />
 * It will be created by {@link JaxbFactory} and used in
 * {@link AllCredentialsResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@XmlRootElement(name = "AllCredentials")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllCredentialsJaxb {
	
	@XmlElement(name = "Credentials")
	private Set<CredentialsJaxb> allCredentials;
	
	
	/**
	 * 
	 * @return All Credentials.
	 */
	public Set<CredentialsJaxb> getCredentials() {
		return this.allCredentials;
	}
	
	/**
	 * Sets all {@code credentials}.
	 * 
	 * @param credentials to set.
	 */
	public void setCredentials(Set<CredentialsJaxb> allCredentials) {
		this.allCredentials = allCredentials;
	}
	
}
