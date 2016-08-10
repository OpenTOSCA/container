package org.opentosca.containerapi.resources.credentials.jaxb;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.containerapi.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.containerapi.resources.credentials.AllCredentialsResource;
import org.opentosca.containerapi.resources.credentials.CredentialsResource;
import org.opentosca.core.credentials.service.ICoreCredentialsService;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the JAXB objects for the {@link AllCredentialsResource} and
 * {@link CredentialsResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class JaxbFactory {
	
	private final static Logger LOG = LoggerFactory.getLogger(JaxbFactory.class);
	
	private static ICoreCredentialsService credentialsService = CredentialsServiceHandler.getCredentialsService();
	
	
	/**
	 * @return {@link AllCredentialsJaxb} containing stored credentials for
	 *         storage provider {@code storageProviderID}. If {@code null} or an
	 *         empty string is passed instead all stored credentials are
	 *         contained.<br />
	 *         Necessary data will be fetched from
	 *         {@link ICoreCredentialsService}.<br />
	 * 
	 * @see #createCredentialsJaxb(Credentials)
	 */
	public static AllCredentialsJaxb createAllCredentialsJaxb(String storageProviderID) {
		
		Set<Credentials> allCredentials;
		
		if ((storageProviderID == null) || storageProviderID.isEmpty()) {
			allCredentials = JaxbFactory.credentialsService.getAllCredentials();
		} else {
			allCredentials = JaxbFactory.credentialsService.getAllCredentialsOfStorageProvider(storageProviderID);
		}
		
		Set<CredentialsJaxb> credentialsJaxbSet = new HashSet<CredentialsJaxb>();
		
		for (Credentials credentials : allCredentials) {
			CredentialsJaxb credentialsJaxb = JaxbFactory.createCredentialsJaxb(credentials);
			credentialsJaxbSet.add(credentialsJaxb);
		}
		
		AllCredentialsJaxb allCredentialsJaxb = new AllCredentialsJaxb();
		allCredentialsJaxb.setCredentials(credentialsJaxbSet);
		
		return allCredentialsJaxb;
		
	}
	
	/**
	 * Builds {@link CredentialsJaxb} of credentials {@code credentials}. It
	 * contains additionally the information if these credentials are currently
	 * set in their storage provider (if storage provider is not available it's
	 * {@code false}).<br />
	 * Necessary data will be fetched from {@link ICoreCredentialsService}.<br />
	 * 
	 * @param credentials
	 * @return {@link CredentialsJaxb} of credentials {@code credentials}
	 */
	public static CredentialsJaxb createCredentialsJaxb(Credentials credentials) {
		
		CredentialsJaxb credentialsJaxb = new CredentialsJaxb(credentials);
		
		long credentialsID = credentials.getID();
		
		try {
			boolean injectedInStorageProvider = JaxbFactory.credentialsService.hasStorageProviderCredentials(credentialsID);
			credentialsJaxb.setInjectedInStorageProvider(injectedInStorageProvider);
		} catch (UserException exc) {
			JaxbFactory.LOG.warn("An User Exception occured.", exc);
		}
		
		return credentialsJaxb;
		
	}
	
}
