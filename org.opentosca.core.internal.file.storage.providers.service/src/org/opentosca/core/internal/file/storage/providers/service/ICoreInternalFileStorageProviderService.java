package org.opentosca.core.internal.file.storage.providers.service;

import java.io.InputStream;
import java.nio.file.Path;

import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.SystemException;

/**
 * This interface of a storage provider provides methods for<br />
 * - getting the ID and name of the storage provider.<br />
 * - checking if the storage provider is ready.<br />
 * - storing, getting and deleting files on the storage provider.<br />
 * - getting the size of files on the storage provider.<br />
 * - managing credentials of the storage provider and getting the storage
 * provider-specific identity and key names of credentials.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public interface ICoreInternalFileStorageProviderService {
	
	/**
	 * 
	 * @return ID of this storage provider.
	 */
	public String getStorageProviderID();
	
	/**
	 * 
	 * @return Name of this storage provider.
	 */
	public String getStorageProviderName();
	
	/**
	 * 
	 * @return {@code true} if all requirements of this storage provider are
	 *         satisfied so it can be used, otherwise {@code false}.
	 */
	public boolean isStorageProviderReady();
	
	/**
	 * Stores the file {@code absFilePath} as {@code relFilePathOnProvider} on
	 * this storage provider.<br />
	 * If the file already exists, it will be overwritten.
	 * 
	 * @param absFilePath - absolute path of file.
	 * @param relFilePathOnProvider - relative path where the file should be
	 *            stored on storage provider.
	 * @throws SystemException if {@code absFilePath} doesn't points to an
	 *             existing file or an error occurred during storing.
	 */
	public void storeFile(Path absFilePath, String relFilePathOnProvider) throws SystemException;
	
	/**
	 * Stores the file given as input stream {@code fileInputStream} as file
	 * {@code relFilePathOnProvider} on this storage provider.<br />
	 * If the file already exists, it will be overwritten.
	 * 
	 * @param fileInputStream of file.
	 * @param fileSize - size of file in bytes.
	 * @param relFilePathOnProvider - relative path where the file should be
	 *            stored on the storage provider.
	 * @throws SystemException if an error occurred during storing.
	 */
	public void storeFile(InputStream fileInputStream, long fileSize, String relFilePathOnProvider) throws SystemException;
	
	/**
	 * Retrieves the file {@code relFilePathOnProvider} from this storage
	 * provider. It will be stored at {@code targetAbsFilePath}.<br />
	 * If file {@code targetAbsFilePath} not exists, it will be created.
	 * Otherwise it will be overwritten.
	 * 
	 * @param relFilePathOnProvider - relative path of file on storage provider
	 * @param targetAbsFilePath - local absolute file path.
	 * @throws SystemException if {@code relFilePathOnProvider} was not found on
	 *             storage provider, creating or writing to file
	 *             {@code targetAbsFilePath} failed or an error occurred during
	 *             retrieving.
	 */
	public void getFile(String relFilePathOnProvider, Path targetAbsFilePath) throws SystemException;
	
	/**
	 * 
	 * Gets the input stream of file {@code relFilePathOnProvider} from this
	 * storage provider.
	 * 
	 * @param relFilePathOnProvider - relative path of file on storage provider.
	 * @return {@link InputStream} of file {@code relFilePathOnProvider}.
	 * @throws SystemException if {@code relFilePathOnProvider} was not found on
	 *             storage provider or an error occurred during getting.
	 */
	public InputStream getFileAsInputStream(String relFilePathOnProvider) throws SystemException;
	
	/**
	 * @param relFilePathOnProvider - relative path of file on storage provider.
	 * @return Size of file {@code relFilePathOnProvider} in bytes.
	 * @throws SystemException if {@code relFilePathOnProvider} was not found on
	 *             storage provider or an error occurred during getting size.
	 */
	public long getFileSize(String relFilePathOnProvider) throws SystemException;
	
	/**
	 * Deletes the file {@code relFilePathOnProvider} on this storage provider,
	 * if it exists (deleting a non-existent file is also a successful
	 * execution).
	 * 
	 * @param relFilePathOnProvider - relative path of file on storage provider.
	 * @throws SystemException if an error occurred during deleting.
	 */
	public void deleteFile(String relFilePathOnProvider) throws SystemException;
	
	/**
	 * @return {@code true} if storage provider needs credentials, otherwise
	 *         {@code false}.
	 */
	public boolean needsCredentials();
	
	/**
	 * 
	 * Sets the credentials {@code credentials} in this storage provider.
	 * 
	 * @param credentials to set.
	 */
	public void setCredentials(Credentials credentials);
	
	/**
	 * Deletes the credentials in this storage provider.
	 */
	public void deleteCredentials();
	
	/**
	 * @return Name of the credentials identity of this storage provider.
	 */
	public String getCredentialsIdentityName();
	
	/**
	 * @return Name of the credentials key of this storage provider.
	 */
	public String getCredentialsKeyName();
	
	/**
	 * 
	 * @return ID of set credentials. If storage provider has currently no
	 *         credentials {@code null}.
	 */
	public Long getCredentialsID();
	
}
