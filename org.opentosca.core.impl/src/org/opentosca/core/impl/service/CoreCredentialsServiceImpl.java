package org.opentosca.core.impl.service;

import java.util.Set;

import org.opentosca.core.common.SystemException;
import org.opentosca.core.common.UserException;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.core.service.ICoreCredentialsService;
import org.opentosca.core.service.internal.ICoreInternalCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation currently acts as a Proxy to the Core Internal
 * Credentials Service. It can in future be used to modify the incoming
 * parameters to fit another back end interface / implementation.
 *
 * @see ICoreInternalCredentialsService
 * @see Credentials
 */
public class CoreCredentialsServiceImpl implements ICoreCredentialsService {

	final private static Logger LOG = LoggerFactory.getLogger(CoreCredentialsServiceImpl.class);

	private ICoreInternalCredentialsService internalCredentialsService;


	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public long storeCredentials(final Credentials credentials) throws UserException {
		return this.internalCredentialsService.storeCredentials(credentials);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Credentials getCredentials(final long credentialsID) throws UserException {
		return this.internalCredentialsService.getCredentials(credentialsID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Long> getCredentialsIDs() {
		return this.internalCredentialsService.getCredentialsIDs();
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Credentials> getAllCredentialsOfStorageProvider(final String storageProviderID) {
		return this.internalCredentialsService.getAllCredentialsOfStorageProvider(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public Set<Credentials> getAllCredentials() {
		return this.internalCredentialsService.getAllCredentials();
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteCredentials(final long credentialsID) throws UserException {
		this.internalCredentialsService.deleteCredentials(credentialsID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteAllCredentials() {
		this.internalCredentialsService.deleteAllCredentials();
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void setCredentialsInStorageProvider(final long credentialsID) throws UserException, SystemException {
		this.internalCredentialsService.setCredentialsInStorageProvider(credentialsID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public void deleteCredentialsInStorageProvider(final String storageProviderID) throws SystemException {
		this.internalCredentialsService.deleteCredentialsInStorageProvider(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean hasStorageProviderCredentials(final String storageProviderID) throws SystemException {
		return this.internalCredentialsService.hasStorageProviderCredentials(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean hasStorageProviderCredentials(final long credentialsID) throws UserException {
		return this.internalCredentialsService.hasStorageProviderCredentials(credentialsID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public boolean needsStorageProviderCredentials(final String storageProviderID) throws SystemException {
		return this.internalCredentialsService.needsStorageProviderCredentials(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public String getCredentialsIdentityName(final String storageProviderID) throws SystemException {
		return this.internalCredentialsService.getCredentialsIdentityName(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This currently acts as a proxy.
	 */
	public String getCredentialsKeyName(final String storageProviderID) throws SystemException {
		return this.internalCredentialsService.getCredentialsKeyName(storageProviderID);
	}

	@Override
	/**
	 * {@inheritDoc} <br />
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
	public void bindCoreInternalCredentialsService(final ICoreInternalCredentialsService internalCredentialsService) {
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
	public void unbindCoreInternalCredentialsService(final ICoreInternalCredentialsService internalCredentialsService) {
		this.internalCredentialsService = null;
		CoreCredentialsServiceImpl.LOG.debug("Core Internal Credentials Service unbound.");
	}

}
