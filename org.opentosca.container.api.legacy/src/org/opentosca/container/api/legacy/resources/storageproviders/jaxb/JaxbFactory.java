package org.opentosca.container.api.legacy.resources.storageproviders.jaxb;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.container.api.legacy.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.storageproviders.AvailableStorageProviderResource;
import org.opentosca.container.api.legacy.resources.storageproviders.AvailableStorageProvidersResource;
import org.opentosca.container.api.legacy.resources.storageproviders.DefaultStorageProviderResource;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.service.ICoreCredentialsService;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the JAXB objects for the {@link AvailableStorageProvidersResource},
 * {@link AvailableStorageProviderResource} and
 * {@link DefaultStorageProviderResource}.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class JaxbFactory {
	
	private final static Logger LOG = LoggerFactory.getLogger(JaxbFactory.class);
	
	private static ICoreFileService fileService = FileRepositoryServiceHandler.getFileHandler();
	private static ICoreCredentialsService credentialsService = CredentialsServiceHandler.getCredentialsService();
	
	
	/**
	 *
	 * @return {@link StorageProvidersJaxb} that contains all available storage
	 *         providers.
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfAllStorageProviders() {
		
		final Set<String> storageProviderIDs = JaxbFactory.fileService.getStorageProviders();
		
		final StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
		return storageProvidersJaxb;
		
	}
	
	/**
	 *
	 * @return {@link StorageProvidersJaxb} that contains the active storage
	 *         provider if it's set (otherwise not storage provider is
	 *         contained).
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfActiveStorageProvider() {
		
		final String activeStorageProviderID = JaxbFactory.fileService.getActiveStorageProvider();
		
		final Set<String> storageProviderIDs = new HashSet<>();
		
		if (activeStorageProviderID != null) {
			storageProviderIDs.add(activeStorageProviderID);
		}
		
		final StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
		return storageProvidersJaxb;
		
	}
	
	/**
	 *
	 * @return {@link StorageProvidersJaxb} that contains the ready storage
	 *         providers.
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfReadyStorageProviders() {
		
		final Set<String> readyStorageProviderIDs = JaxbFactory.fileService.getReadyStorageProviders();
		
		final StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(readyStorageProviderIDs);
		
		return storageProvidersJaxb;
		
	}
	
	/**
	 *
	 * @return {@link StorageProviderJaxb} that contains the default storage
	 *         provider.
	 * @see #createStorageProviderJaxb(String)
	 */
	public static StorageProviderJaxb createStorageProviderJaxbOfDefaultStorageProvider() {
		
		final String defaultStorageProviderID = JaxbFactory.fileService.getDefaultStorageProvider();
		
		final StorageProviderJaxb storageProviderJaxb = JaxbFactory.createStorageProviderJaxb(defaultStorageProviderID);
		
		return storageProviderJaxb;
		
	}
	
	/**
	 *
	 * @return {@link StorageProvidersJaxb} that contains the default storage
	 *         provider.
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfDefaultStorageProvider() {
		
		final String defaultStorageProviderID = JaxbFactory.fileService.getDefaultStorageProvider();
		
		final Set<String> storageProviderIDs = new HashSet<>();
		storageProviderIDs.add(defaultStorageProviderID);
		
		final StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
		return storageProvidersJaxb;
	}
	
	/**
	 *
	 * Builds a {@link StorageProviderJaxb} of the storage provider
	 * {@code storageProviderID}.<br />
	 * Necessary data (e.g. is storage provider ready) will be fetched from the
	 * {@link ICoreFileService} and {@link ICoreCredentialsService}.
	 *
	 * @param storageProviderID of storage provider.
	 * @return {@link StorageProviderJaxb} of the storage provider
	 *         {@code storageProviderID}.
	 */
	public static StorageProviderJaxb createStorageProviderJaxb(final String storageProviderID) {
		
		final StorageProviderJaxb storageProviderJaxb = new StorageProviderJaxb();
		
		storageProviderJaxb.setId(storageProviderID);
		
		// the following information can be only set in the JAXB object if the
		// storage provider is available (otherwise the information is not
		// available)
		if (JaxbFactory.fileService.getStorageProviders().contains(storageProviderID)) {
			
			storageProviderJaxb.setName(JaxbFactory.fileService.getStorageProviderName(storageProviderID));
			storageProviderJaxb.setReady(JaxbFactory.fileService.isReadyStorageProvider(storageProviderID));
			
			try {
				storageProviderJaxb.setNeedsCredentials(JaxbFactory.credentialsService.needsStorageProviderCredentials(storageProviderID));
			} catch (final SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setCredentialsIdentityName(JaxbFactory.credentialsService.getCredentialsIdentityName(storageProviderID));
			} catch (final SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setCredentialsKeyName(JaxbFactory.credentialsService.getCredentialsKeyName(storageProviderID));
			} catch (final SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setHasCredentials(JaxbFactory.credentialsService.hasStorageProviderCredentials(storageProviderID));
			} catch (final SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			if (storageProviderID.equals(JaxbFactory.fileService.getActiveStorageProvider())) {
				storageProviderJaxb.setActive(true);
			} else {
				storageProviderJaxb.setActive(false);
			}
			
		}
		
		if (storageProviderID.equals(JaxbFactory.fileService.getDefaultStorageProvider())) {
			storageProviderJaxb.setDefault(true);
		} else {
			storageProviderJaxb.setDefault(false);
		}
		
		return storageProviderJaxb;
		
	}
	
	/**
	 * Builds {@link StorageProvidersJaxb} containing the storage providers
	 * {@code storageProviderIDs}.<br />
	 * It uses {@link #buildStorageProviderJaxb(String)}.
	 *
	 * @param storageProviderIDs of storage providers.
	 * @return {@link StorageProvidersJaxb} containing the storage providers
	 *         {@code storageProviderIDs}.
	 */
	private static StorageProvidersJaxb createStorageProvidersJaxb(final Set<String> storageProviderIDs) {
		
		final Set<StorageProviderJaxb> storageProviders = new HashSet<>();
		
		for (final String storageProviderID : storageProviderIDs) {
			final StorageProviderJaxb storageProviderJaxb = JaxbFactory.createStorageProviderJaxb(storageProviderID);
			storageProviders.add(storageProviderJaxb);
		}
		
		final StorageProvidersJaxb storageProvidersJaxb = new StorageProvidersJaxb();
		storageProvidersJaxb.setStorageProvider(storageProviders);
		
		return storageProvidersJaxb;
		
	}
}
