package org.opentosca.containerapi.resources.storageproviders.jaxb;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.containerapi.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.resources.storageproviders.AvailableStorageProviderResource;
import org.opentosca.containerapi.resources.storageproviders.AvailableStorageProvidersResource;
import org.opentosca.containerapi.resources.storageproviders.DefaultStorageProviderResource;
import org.opentosca.core.credentials.service.ICoreCredentialsService;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.exceptions.SystemException;
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
		
		Set<String> storageProviderIDs = JaxbFactory.fileService.getStorageProviders();
		
		StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
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
		
		String activeStorageProviderID = JaxbFactory.fileService.getActiveStorageProvider();
		
		Set<String> storageProviderIDs = new HashSet<String>();
		
		if (activeStorageProviderID != null) {
			storageProviderIDs.add(activeStorageProviderID);
		}
		
		StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
		return storageProvidersJaxb;
		
	}
	
	/**
	 * 
	 * @return {@link StorageProvidersJaxb} that contains the ready storage
	 *         providers.
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfReadyStorageProviders() {
		
		Set<String> readyStorageProviderIDs = JaxbFactory.fileService.getReadyStorageProviders();
		
		StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(readyStorageProviderIDs);
		
		return storageProvidersJaxb;
		
	}
	
	/**
	 * 
	 * @return {@link StorageProviderJaxb} that contains the default storage
	 *         provider.
	 * @see #createStorageProviderJaxb(String)
	 */
	public static StorageProviderJaxb createStorageProviderJaxbOfDefaultStorageProvider() {
		
		String defaultStorageProviderID = JaxbFactory.fileService.getDefaultStorageProvider();
		
		StorageProviderJaxb storageProviderJaxb = JaxbFactory.createStorageProviderJaxb(defaultStorageProviderID);
		
		return storageProviderJaxb;
		
	}
	
	/**
	 * 
	 * @return {@link StorageProvidersJaxb} that contains the default storage
	 *         provider.
	 * @see #createStorageProvidersJaxb(Set)
	 */
	public static StorageProvidersJaxb createStorageProvidersJaxbOfDefaultStorageProvider() {
		
		String defaultStorageProviderID = JaxbFactory.fileService.getDefaultStorageProvider();
		
		Set<String> storageProviderIDs = new HashSet<String>();
		storageProviderIDs.add(defaultStorageProviderID);
		
		StorageProvidersJaxb storageProvidersJaxb = JaxbFactory.createStorageProvidersJaxb(storageProviderIDs);
		
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
	public static StorageProviderJaxb createStorageProviderJaxb(String storageProviderID) {
		
		StorageProviderJaxb storageProviderJaxb = new StorageProviderJaxb();
		
		storageProviderJaxb.setId(storageProviderID);
		
		// the following information can be only set in the JAXB object if the
		// storage provider is available (otherwise the information is not
		// available)
		if (JaxbFactory.fileService.getStorageProviders().contains(storageProviderID)) {
			
			storageProviderJaxb.setName(JaxbFactory.fileService.getStorageProviderName(storageProviderID));
			storageProviderJaxb.setReady(JaxbFactory.fileService.isReadyStorageProvider(storageProviderID));
			
			try {
				storageProviderJaxb.setNeedsCredentials(JaxbFactory.credentialsService.needsStorageProviderCredentials(storageProviderID));
			} catch (SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setCredentialsIdentityName(JaxbFactory.credentialsService.getCredentialsIdentityName(storageProviderID));
			} catch (SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setCredentialsKeyName(JaxbFactory.credentialsService.getCredentialsKeyName(storageProviderID));
			} catch (SystemException exc) {
				JaxbFactory.LOG.debug("A System Exception occured.", exc);
			}
			
			try {
				storageProviderJaxb.setHasCredentials(JaxbFactory.credentialsService.hasStorageProviderCredentials(storageProviderID));
			} catch (SystemException exc) {
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
	private static StorageProvidersJaxb createStorageProvidersJaxb(Set<String> storageProviderIDs) {
		
		Set<StorageProviderJaxb> storageProviders = new HashSet<StorageProviderJaxb>();
		
		for (String storageProviderID : storageProviderIDs) {
			StorageProviderJaxb storageProviderJaxb = JaxbFactory.createStorageProviderJaxb(storageProviderID);
			storageProviders.add(storageProviderJaxb);
		}
		
		StorageProvidersJaxb storageProvidersJaxb = new StorageProvidersJaxb();
		storageProvidersJaxb.setStorageProvider(storageProviders);
		
		return storageProvidersJaxb;
		
	}
}
