package org.opentosca.core.credentials.service.impl;

import java.util.Set;

import org.opentosca.core.credentials.service.ICoreCredentialsService;
import org.opentosca.core.internal.credentials.service.ICoreInternalCredentialsService;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 * 
 * <br />
 * <br />
 * This implementation currently acts as a Proxy to the Core Internal
 * Credentials Service. It can in future be used to modify the incoming
 * parameters to fit another back end interface / implementation.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @see ICoreInternalCredentialsService
 * @see Credentials
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CoreCredentialsServiceImpl implements ICoreCredentialsService {
	
	final private static Logger LOG = LoggerFactory.getLogger(CoreCredentialsServiceImpl.class);
	
	private ICoreInternalCredentialsService internalCredentialsService;
	
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public long storeCredentials(Credentials credentials) throws UserException {
		return this.internalCredentialsService.storeCredentials(credentials);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Credentials getCredentials(long credentialsID) throws UserException {
		return this.internalCredentialsService.getCredentials(credentialsID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Long> getCredentialsIDs() {
		return this.internalCredentialsService.getCredentialsIDs();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Credentials> getAllCredentialsOfStorageProvider(String storageProviderID) {
		return this.internalCredentialsService.getAllCredentialsOfStorageProvider(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Credentials> getAllCredentials() {
		return this.internalCredentialsService.getAllCredentials();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteCredentials(long credentialsID) throws UserException {
		this.internalCredentialsService.deleteCredentials(credentialsID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteAllCredentials() {
		this.internalCredentialsService.deleteAllCredentials();
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void setCredentialsInStorageProvider(long credentialsID) throws UserException, SystemException {
		this.internalCredentialsService.setCredentialsInStorageProvider(credentialsID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteCredentialsInStorageProvider(String storageProviderID) throws SystemException {
		this.internalCredentialsService.deleteCredentialsInStorageProvider(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean hasStorageProviderCredentials(String storageProviderID) throws SystemException {
		return this.internalCredentialsService.hasStorageProviderCredentials(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean hasStorageProviderCredentials(long credentialsID) throws UserException {
		return this.internalCredentialsService.hasStorageProviderCredentials(credentialsID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean needsStorageProviderCredentials(String storageProviderID) throws SystemException {
		return this.internalCredentialsService.needsStorageProviderCredentials(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public String getCredentialsIdentityName(String storageProviderID) throws SystemException {
		return this.internalCredentialsService.getCredentialsIdentityName(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public String getCredentialsKeyName(String storageProviderID) throws SystemException {
		return this.internalCredentialsService.getCredentialsKeyName(storageProviderID);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 * <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<String> getStorageProviders() {
		return this.internalCredentialsService.getStorageProviders();
	}
	
	/**
	 * Binds the Core Internal Credentials Service.
	 * 
	 * @param credentialsService to bind
	 */
	public void bindCoreInternalCredentialsService(ICoreInternalCredentialsService internalCredentialsService) {
		if (internalCredentialsService == null) {
			CoreCredentialsServiceImpl.LOG.error("Can't bind Core Internal Credentials Service.");
		} else {
			this.internalCredentialsService = internalCredentialsService;
			CoreCredentialsServiceImpl.LOG.debug("Core Internal Credentials Service bound.");
		}
		
	}
	
	/**
	 * Unbinds the Core Internal Credentials Service.
	 * 
	 * @param credentialsService to unbind
	 */
	public void unbindCoreInternalCredentialsService(ICoreInternalCredentialsService internalCredentialsService) {
		this.internalCredentialsService = null;
		CoreCredentialsServiceImpl.LOG.debug("Core Internal Credentials Service unbound.");
	}
	
}
