package org.opentosca.core.internal.credentials.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.core.internal.credentials.service.ICoreInternalCredentialsService;
import org.opentosca.core.internal.credentials.service.impl.jpa.CredentialsJPAStore;
import org.opentosca.core.internal.file.storage.providers.service.ICoreInternalFileStorageProviderService;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a store and management functionalities for the credentials of the
 * storage providers.<br />
 * <br />
 * Also has a connection to the storage providers: Credentials can be set /
 * injected in a available storage provider. If some conditions are fulfilled
 * (see {@link #storeCredentials(Credentials)} a stored credentials will be
 * automatically set in the appropriate storage provider. Otherwise it can be
 * done manually by using
 * {@link #setCredentialsInStorageProvider(String, String)}. Credentials can be
 * also stored if the appropriate storage provider is not available. <br />
 * Currently a credentials consists of an identity and a key. You can get the
 * name of the identity and key of a certain storage provider by using
 * {@link #getCredentialsIdentityName(String)} and
 * {@link #getCredentialsKeyName(String)}.<br />
 * For example, for AWS S3 the identity is the Access Key and key is the Secret
 * Access Key.<br />
 * <br />
 * Most of the methods of the credentials service are also accessible via OSGi
 * console commands.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @see ICoreInternalFileStorageProviderService
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 * @todo In future maybe other components needs also a store for credentials.
 *       Then we should extend the Credentials Service (and its model) to a
 *       central Credentials Service of OpenTOSCA.
 */
public class CoreInternalCredentialsServiceImpl implements ICoreInternalCredentialsService {
	
	private final static Logger LOG = LoggerFactory.getLogger(CoreInternalCredentialsServiceImpl.class);
	
	/**
	 * Bound / available file storage providers.
	 */
	private static final Map<String, ICoreInternalFileStorageProviderService> STORAGE_PROVIDERS = Collections.synchronizedMap(new HashMap<String, ICoreInternalFileStorageProviderService>());
	
	private final CredentialsJPAStore JPA_STORE = new CredentialsJPAStore();
	
	
	@Override
	public long storeCredentials(Credentials credentials) throws UserException {
		
		boolean isValid = this.checkCredentials(credentials);
		
		if (!isValid) {
			throw new UserException("Storing credentials not possible, because some required data are missing in credentials.");
		}
		
		String storageProviderID = credentials.getStorageProviderID();
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		// we can only check if storage provider needs credentials if it is
		// available
		if (storageProvider != null) {
			
			boolean needsCredentials = storageProvider.needsCredentials();
			
			if (!needsCredentials) {
				throw new UserException("Credentials can't be stored, because available storage provider \"" + storageProviderID + "\" needs no credentials.");
			}
			
		}
		
		long credentialsID = this.JPA_STORE.storeCredentials(credentials);
		
		if (storageProvider != null) {
			
			if (storageProvider.getCredentialsID() != null) {
				CoreInternalCredentialsServiceImpl.LOG.debug("Credentials \"{}\" will not be set in storage provider \"{}\", because it has already credentials.", credentialsID, storageProviderID);
			} else {
				
				try {
					this.setCredentialsInStorageProvider(credentials);
				} catch (SystemException exc) {
					CoreInternalCredentialsServiceImpl.LOG.warn("A System Exception occured.", exc);
				}
				
			}
			
		} else {
			
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" is not available, so credentials \"{}\" can't be set.", storageProviderID, credentialsID);
			
		}
		
		return credentialsID;
		
	}
	
	/**
	 * Checks the {@code credentials} for missing required data.<br />
	 * In a credentials the storage provider ID, the identity and the key must
	 * be set. A description is optional.
	 * 
	 * @param credentials to check.
	 * @return {@true} if {@code credentials} are valid, otherwise {@code false}
	 *         .
	 */
	private boolean checkCredentials(Credentials credentials) {
		CoreInternalCredentialsServiceImpl.LOG.debug("Checking for missing data in given credentials...", credentials.getStorageProviderID());
		
		String storageProviderID = credentials.getStorageProviderID();
		String identity = credentials.getIdentity();
		String key = credentials.getKey();
		
		boolean noErrorsOccured = true;
		
		if (storageProviderID == null) {
			CoreInternalCredentialsServiceImpl.LOG.warn("Storage Provider ID is missing in credentials.");
			noErrorsOccured = false;
		}
		
		if (identity == null) {
			CoreInternalCredentialsServiceImpl.LOG.warn("Identity is missing in credentials.");
			noErrorsOccured = false;
		}
		
		if (key == null) {
			CoreInternalCredentialsServiceImpl.LOG.warn("Key is missing in credentials.");
			noErrorsOccured = false;
		}
		
		if (noErrorsOccured) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Credentials have no missing data.");
		} else {
			CoreInternalCredentialsServiceImpl.LOG.warn("Credentials have missing data.");
		}
		
		return noErrorsOccured;
		
	}
	
	@Override
	public Credentials getCredentials(long credentialsID) throws UserException {
		Credentials credentials = this.JPA_STORE.getCredentials(credentialsID);
		return credentials;
	}
	
	@Override
	public Set<Credentials> getAllCredentialsOfStorageProvider(String storageProviderID) {
		
		Set<Credentials> allCredentialsOfStorageProvider = this.JPA_STORE.getAllCredentialsOfStorageProvider(storageProviderID);
		return allCredentialsOfStorageProvider;
		
	}
	
	@Override
	public Set<Long> getCredentialsIDs() {
		
		Set<Long> credentialsIDs = this.JPA_STORE.getCredentialsIDs();
		
		return credentialsIDs;
		
	}
	
	@Override
	public Set<Credentials> getAllCredentials() {
		
		Set<Credentials> allCredentials = this.JPA_STORE.getAllCredentials();
		
		return allCredentials;
		
	}
	
	@Override
	public void deleteCredentials(long credentialsID) throws UserException {
		
		Credentials credentials = this.JPA_STORE.getCredentials(credentialsID);
		
		String storageProviderID = credentials.getStorageProviderID();
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider != null) {
			if (((storageProvider.getCredentialsID() != null) && storageProvider.getCredentialsID().equals(credentialsID))) {
				storageProvider.deleteCredentials();
				CoreInternalCredentialsServiceImpl.LOG.debug("Credentials were deleted in storage provider \"{}\".", storageProviderID);
			} else {
				CoreInternalCredentialsServiceImpl.LOG.debug("No credentials have to be deleted in storage provider \"{}\".", storageProviderID);
			}
		} else {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" is not available, so no credentials have to be deleted.", storageProviderID);
		}
		
		this.JPA_STORE.deleteCredentials(credentialsID);
		
	}
	
	@Override
	public void deleteAllCredentials() {
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Deleting credentials in storage providers...");
		
		synchronized (this.JPA_STORE) {
			for (ICoreInternalFileStorageProviderService storageProvider : CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS.values()) {
				storageProvider.deleteCredentials();
			}
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Deleting credentials in storage providers completed.");
		
		this.JPA_STORE.deleteAllCredentials();
		
	}
	
	@Override
	public void setCredentialsInStorageProvider(long credentialsID) throws UserException, SystemException {
		Credentials credentials = this.getCredentials(credentialsID);
		this.setCredentialsInStorageProvider(credentials);
	}
	
	/**
	 * Sets / injects {@code credentials} in appropriate storage provider.
	 * 
	 * @throws SystemException if storage provider to set is not available.
	 * @throws UserException if storage provider to set needs no credentials.
	 * 
	 */
	private void setCredentialsInStorageProvider(Credentials credentials) throws SystemException, UserException {
		
		String storageProviderID = credentials.getStorageProviderID();
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("Can set credentials in storage provider \"" + storageProviderID + "\", because it is not available.");
		}
		
		if (!storageProvider.needsCredentials()) {
			throw new UserException("Credentials were not set in storage provider \"" + storageProviderID + "\", because it needs no credentials.");
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Setting credentials in storage provider \"{}\"...", storageProviderID);
		
		storageProvider.setCredentials(credentials);
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Setting credentials in storage provider \"{}\" completed.", storageProviderID);
		
	}
	
	@Override
	public void deleteCredentialsInStorageProvider(String storageProviderID) throws SystemException {
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("No credentials can be deleted, because storage provider \"" + storageProviderID + "\" is not available.");
		}
		
		if (storageProvider.getCredentialsID() != null) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Deleting credentials in storage provider \"{}\"...", storageProviderID);
			storageProvider.deleteCredentials();
			CoreInternalCredentialsServiceImpl.LOG.debug("Deleting credentials in storage provider \"{}\" completed.", storageProviderID);
		} else {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" has currently no credentials.", storageProviderID);
		}
		
	}
	
	@Override
	public boolean hasStorageProviderCredentials(String storageProviderID) throws SystemException {
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("Can't check if storage provider \"" + storageProviderID + "\" has credentials, because storage provider is not available.");
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Checking if storage provider \"{}\" has credentials...", storageProviderID);
		
		if (storageProvider.getCredentialsID() != null) {
			
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" has credentials.", storageProviderID);
			return true;
			
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" has no credentials.", storageProviderID);
		return false;
	}
	
	@Override
	public boolean hasStorageProviderCredentials(long credentialsID) throws UserException {
		
		Credentials credentials = this.getCredentials(credentialsID);
		String storageProviderID = credentials.getStorageProviderID();
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" is not available, so credentials \"{}\" are not set.", storageProviderID, credentialsID);
			return false;
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Checking if credentials \"{}\" are set in storage provider \"{}\"...", credentialsID, storageProviderID);
		
		Long storageProviderCredentialsID = storageProvider.getCredentialsID();
		
		if ((storageProviderCredentialsID != null) && storageProviderCredentialsID.equals(credentialsID)) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Credentials \"{}\" are set in storage provider \"{}\".", credentialsID, storageProviderID);
			return true;
		}
		
		CoreInternalCredentialsServiceImpl.LOG.debug("Credentials \"{}\" are not set in storage provider \"{}\".", credentialsID, storageProviderID);
		
		return false;
		
	}
	
	@Override
	public boolean needsStorageProviderCredentials(String storageProviderID) throws SystemException {
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("Can't check if storage provider \"" + storageProviderID + "\" needs credentials, because it is not available.");
		}
		
		boolean needsCredentials = storageProvider.needsCredentials();
		
		if (needsCredentials) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" needs credentials.", storageProviderID);
		} else {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" needs no credentials.", storageProviderID);
		}
		
		return needsCredentials;
		
	}
	
	@Override
	public String getCredentialsIdentityName(String storageProviderID) throws SystemException {
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("Can't get credentials identity name of storage provider \"" + storageProviderID + "\", because it is not available.");
		}
		
		String identityName = storageProvider.getCredentialsIdentityName();
		
		return identityName;
		
	}
	
	@Override
	public String getCredentialsKeyName(String storageProviderID) throws SystemException {
		
		ICoreInternalFileStorageProviderService storageProvider = this.getStorageProvider(storageProviderID);
		
		if (storageProvider == null) {
			throw new SystemException("Can't get credentials key name of storage provider \"" + storageProviderID + "\", because it is not available.");
		}
		
		String keyName = storageProvider.getCredentialsKeyName();
		
		return keyName;
		
	}
	
	@Override
	public Set<String> getStorageProviders() {
		
		Set<String> storageProviderIDs = null;
		
		synchronized (CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS) {
			storageProviderIDs = CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS.keySet();
		}
		return storageProviderIDs;
		
	}
	
	/**
	 * @param storageProviderID of storage provider.
	 * @return Storage provider {@code storageProviderID}. If it's not available
	 *         {@code null}.
	 */
	private ICoreInternalFileStorageProviderService getStorageProvider(String storageProviderID) {
		
		ICoreInternalFileStorageProviderService storageProvider;
		
		synchronized (CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS) {
			storageProvider = CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS.get(storageProviderID);
		}
		
		if (storageProvider != null) {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" is available.", storageProviderID);
		} else {
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" is not available.", storageProviderID);
		}
		
		return storageProvider;
		
	}
	
	/**
	 * Binds a storage provider.<br />
	 * If exactly one credentials for a storage provider is stored these
	 * credentials will be directly set in the storage provider after binding.
	 * 
	 * @param storageProvider to bind
	 */
	protected void bindCoreInternalFileStorageProviderService(ICoreInternalFileStorageProviderService storageProvider) {
		if (storageProvider != null) {
			
			CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS.put(storageProvider.getStorageProviderID(), storageProvider);
			
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" ({}) bound.", storageProvider.getStorageProviderID(), storageProvider.getStorageProviderName());
			
			String storageProviderID = storageProvider.getStorageProviderID();
			
			Set<Credentials> credentialsOfStorageProvider = this.getAllCredentialsOfStorageProvider(storageProviderID);
			
			if (credentialsOfStorageProvider.size() == 1) {
				CoreInternalCredentialsServiceImpl.LOG.debug("One credentials is stored for storage provider \"{}\". Thus, we will set it now in storage provider.", storageProviderID);
				// always one loop iteration
				for (Credentials credentials : credentialsOfStorageProvider) {
					try {
						this.setCredentialsInStorageProvider(credentials);
					} catch (SystemException exc) {
						CoreInternalCredentialsServiceImpl.LOG.warn("A System Exception occured.", exc);
					} catch (UserException exc) {
						CoreInternalCredentialsServiceImpl.LOG.warn("An User Exception occured.", exc);
					}
				}
			}
			
		} else {
			CoreInternalCredentialsServiceImpl.LOG.warn("Binding a storage provider failed.");
		}
	}
	
	/**
	 * Unbinds a storage provider.<br />
	 * Deleting credentials in storage provider is not necessary on unbinding,
	 * because a unbound storage provider has automatically lost is set /
	 * injected credentials.
	 * 
	 * @param storageProvider to unbind
	 */
	protected void unbindCoreInternalFileStorageProviderService(ICoreInternalFileStorageProviderService storageProvider) {
		if (storageProvider != null) {
			String storageProviderID = storageProvider.getStorageProviderID();
			CoreInternalCredentialsServiceImpl.STORAGE_PROVIDERS.remove(storageProviderID);
			CoreInternalCredentialsServiceImpl.LOG.debug("Storage provider \"{}\" ({}) unbound.", storageProvider.getStorageProviderID(), storageProvider.getStorageProviderName());
		} else {
			CoreInternalCredentialsServiceImpl.LOG.warn("Unbinding a storage provider failed.");
		}
	}
	
}
