package org.opentosca.core.internal.file.service;

import java.nio.file.Path;
import java.util.Set;

import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;

/**
 * This interface of the Core Internal File Service defines methods for<br />
 * <br />
 * - storing, getting, moving and deleting CSAR files.<br />
 * - moving files or directories contained in CSAR files.<br />
 * - getting available and ready storage providers.<br />
 * - getting and setting the active storage provider.<br />
 * - getting the default storage provider.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @see CSARContent
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public interface ICoreInternalFileService {
	
	/**
	 * 
	 * @return IDs of available storage providers which have no unsatisfied
	 *         requirements (ready) and therefore can be used.<br />
	 * 
	 * @see #isReadyStorageProvider()
	 */
	public Set<String> getReadyStorageProviders();
	
	/**
	 * @param storageProviderID of storage provider to check if it's ready.
	 * @return {@code true} if storage provider {@code storageProviderID} is
	 *         available and has no unsatisfied requirements, so it can be used.
	 *         Otherwise {@code false} will be returned.<br />
	 *         Usually a requirement of a storage provider are the credentials
	 *         (exception: file system storage provider) which must be set in
	 *         the storage provider.
	 */
	public boolean isReadyStorageProvider(String storageProviderID);
	
	/**
	 * @return IDs of available storage providers.<br />
	 *         A storage provider is defined as available if it's bundle is
	 *         installed and is in bundle state {@code ACTIVE}.
	 */
	public Set<String> getStorageProviders();
	
	/**
	 * @return ID of active storage provider that will used for storing a CSAR
	 *         and as target for moving a CSAR or a file / directory contained
	 *         in a CSAR.
	 */
	public String getActiveStorageProvider();
	
	/**
	 * @return Friendly name of the storage provider {@code storageProviderID}.
	 *         If {@code storageProviderID} is not available {@code null}.
	 */
	public String getStorageProviderName(String storageProviderID);
	
	/**
	 * Sets storage provider {@code storageProviderID} as the active storage
	 * provider that will used for storing a CSAR and as target for moving a
	 * CSAR or file / directory contained in a CSAR.<br />
	 * <br />
	 * Notes:<br />
	 * 1) After the start of the Container no active storage provider is set.<br />
	 * 2) If the active storage provider goes unavailable, this setting will be
	 * cleared (no active storage provider is set).<br />
	 * 3) By passing {@code null} you can manually set no active storage
	 * provider.
	 * 
	 * 
	 * @param storageProviderID of the storage provider to set as the active
	 *            one.
	 * @throws UserException if storage provider {@code storageProviderID} is
	 *             not available.
	 */
	public void setActiveStorageProvider(String storageProviderID) throws UserException;
	
	/**
	 * @return ID of the default storage provider. It will be used for storing a
	 *         CSAR and as the target for moving a CSAR or a file / directory of
	 *         a CSAR if no active storage storage provider is set or it's not
	 *         ready. If the default storage provider is also not available and
	 *         ready the operation fails.<br />
	 *         The default storage provider is hard-coded and can't be changed.
	 */
	public String getDefaultStorageProvider();
	
	/**
	 * Stores the CSAR file {@code csarFile}.<br />
	 * First, it will be checked if the given file has correct extension and is
	 * not already stored. Then the CSAR will be unpacked (path is specified in
	 * Settings bundle), its content validated and the TOSCA meta file parsed.
	 * Finally all files of the CSAR will be stored on the active storage
	 * provider. After storing on storage provider the meta data of the CSAR
	 * (file and directory paths, TOSCA meta file content and CSAR ID) will be
	 * stored in the database and the unpack directory deleted.<br />
	 * <br />
	 * Note 1: If no active storage provider is set (e.g. directly after the
	 * start of the container) or it's not ready, the default storage provider
	 * will be used instead, if it's available and ready.<br />
	 * Note 2: Only files of a CSAR will be stored on the storage provider.
	 * Directories will be stored as meta data only.<br />
	 * Note 3: If an error occurred during storing the unpack directory will be
	 * also deleted (if CSAR was already unpacked).
	 * 
	 * @param csarFile to store.
	 * @return CSAR ID thats uniquely identifies the CSAR file.
	 * 
	 * @throws SystemException if active storage provider is not set / can't be
	 *             used and default storage provider also can't be used,
	 *             unpacking CSAR failed, access to an directory denied while
	 *             getting files and directories in unpack directory or if an
	 *             error occurred during storing on the storage provider.
	 * 
	 * @throws UserException if {@code csarFile} does not exist, is not a valid
	 *             ZIP file, is already stored or it's content is invalid, e.g.
	 *             contains no TOSCA file in {@code Definitions} directory or
	 *             has an invalid TOSCA meta file.
	 * 
	 * @see org.opentosca.settings.Settings
	 * 
	 * 
	 */
	public CSARID storeCSAR(Path csarFile) throws UserException, SystemException;
	
	/**
	 * Retrieves meta data of CSAR {@code csarID}.
	 * 
	 * @param csarID of CSAR
	 * @return {@link CSARContent} that gives access to all files and
	 *         directories and the TOSCA meta file of the CSAR.
	 * @throws UserException if CSAR {@code csarID} was not found.
	 */
	public CSARContent getCSAR(CSARID csarID) throws UserException;
	
	/**
	 * Exports a stored CSAR {@code csarID}.<br />
	 * First, it retrieves the meta data of the CSAR and checks if the required
	 * storage provider(s) are available and ready to minimize the risk of a
	 * cancel during retrieving files. Then it creates the directory structure
	 * and retrieves all files of the CSAR from the appropriate storage
	 * provider(s) to a sub directory of a Temp directory. Finally the content
	 * of this directory will be compressed as a CSAR file. The created CSAR
	 * file is located directly in the Temp directory.<br />
	 * <br/>
	 * 
	 * Note: At the end or if an error occurred the directory that contains the
	 * content of the CSAR file will be deleted (if necessary).
	 * 
	 * @param csarID of CSAR
	 * @return CSAR {@code csarID} as {@link Path} object.
	 * @throws UserException if CSAR {@code csarID} was not found.
	 * @throws SystemException if a required storage provider is not available
	 *             and ready or an error occurred during retrieving files of
	 *             CSAR.
	 */
	public Path exportCSAR(CSARID csarID) throws UserException, SystemException;
	
	/**
	 * @return CSAR IDs of all stored CSAR files.
	 */
	public Set<CSARID> getCSARIDs();
	
	/**
	 * Moves a CSAR {@code csarID} from it's storage provider(s) to the active
	 * storage provider (target storage provider).<br />
	 * <br />
	 * First, the files meta data of CSAR {@code csarID} will be retrieved to
	 * check if the CSAR is stored and to get the storage provider(s) of the
	 * files of the CSAR. Then all files will be determined that must be moved
	 * to the target storage provider. If no files were found (completely stored
	 * on target storage provider), the move process is completed. Otherwise the
	 * files will be moved successively to the target storage provider.<br />
	 * If an error occurred while moving files, the process will be canceled
	 * respectively further files will not be moved anymore.<br />
	 * <br />
	 * Note: If no active storage provider is set (e.g. directly after the start
	 * of the container) or it's not ready, the default storage provider will be
	 * used instead, if it's available and ready.
	 * 
	 * @param csarID of CSAR.
	 * @see CoreInternalFileServiceImpl#findFilesToMove
	 * @see CoreInternalFileServiceImpl#moveFilesToStorageProvider
	 * 
	 * @throws UserException if CSAR {@code csarID} was not found.
	 * @throws SystemException if a required storage provider is not available
	 *             and ready or an error occurred during moving files.
	 * 
	 */
	public void moveCSAR(CSARID csarID) throws UserException, SystemException;
	
	/**
	 * Moves the file / directory {@code fileOrDirRelToCSARRoot} of CSAR
	 * {@code csarID} from it's storage provider(s) to the active storage
	 * provider.<br />
	 * <br />
	 * First, the files and directories meta data of CSAR {@code csarID} will be
	 * retrieved to check if the CSAR is stored, given
	 * {@code fileOrDirRelToCSARRoot} is a existent file or directory and to get
	 * the storage provider of the file respectively the storage providers of
	 * all files in the directory to move. If file / directory exists, all files
	 * will be determined that must be moved to the target storage provider. If
	 * no files were found (completely stored on target storage provider), the
	 * move process is completed. Otherwise the files will be moved successively
	 * to the target storage provider.<br />
	 * If an error occurred while moving files, the process will be canceled
	 * respectively further files will not be moved anymore.<br />
	 * <br />
	 * Note 1: If no active storage provider is set (e.g. directly after the
	 * start of the container) or it's not ready, the default storage provider
	 * will be used instead, if it's available and ready.<br />
	 * Note 2: Moving a directory means that all files in the directory will be
	 * moved. Directories are only stored as meta data.
	 * 
	 * @param csarID of CSAR
	 * @param fileOrDirRelToCSARRoot - path relative to CSAR root of a file or
	 *            directory in CSAR {@code csarID}.
	 * 
	 * @see CoreInternalFileServiceImpl#findFilesToMove
	 * @see CoreInternalFileServiceImpl#moveFileToStorageProvider
	 * @see CoreInternalFileServiceImpl#moveFilesToStorageProvider
	 * 
	 * @throws UserException if CSAR {@code csarID} was not found or
	 *             {@code fileOrDirRelToCSARRoot} does not exist in CSAR
	 *             {@code csarID}.
	 * @throws SystemException if a required storage provider is not available
	 *             and ready or an error occurred during moving files.
	 * 
	 */
	public void moveFileOrDirectoryOfCSAR(CSARID csarID, Path fileOrDirRelToCSARRoot) throws UserException, SystemException;
	
	/**
	 * Deletes the CSAR {@code csarID}.<br />
	 * <br />
	 * First, the files meta data of CSAR {@code csarID} will be retrieved to
	 * check if the CSAR exists and to get the storage provider(s) of the files.
	 * Then it will be checked if each storage provider is available and ready
	 * to minimize the risk for a cancel of the deletion process. Finally all
	 * files of the CSAR will be deleted on the storage provider(s). After
	 * deletion the meta data of the CSAR will be deleted, too.<br />
	 * If an error occurred while deleting files, the complete process will be
	 * canceled respectively further files will not be deleted anymore. The meta
	 * data of the CSAR is not deleted, so deletion can be executed again to
	 * delete the remaining files.
	 * 
	 * @param csarID of CSAR
	 * @see StorageProviderManager#deleteFilesOfCSAR
	 * 
	 * @throws UserException if CSAR {@code csarID} was not found.
	 * 
	 * @throws SystemException if a required storage provider is not available
	 *             and ready or an error occurred during deleting files.
	 * 
	 */
	public void deleteCSAR(CSARID csarID) throws SystemException, UserException;
	
	/**
	 * Deletes all CSAR files.<br />
	 * <br />
	 * The CSAR IDs of all stored CSAR will be retrieved and then each CSAR
	 * deleted by using {@link ICoreInternalFileService#deleteCSAR(CSARID)}.<br />
	 * If an error occurred while deleting a CSAR, the complete process will be
	 * canceled respectively further CSARs will not be deleted anymore.<br />
	 * 
	 * @throws SystemException if a required storage provider is not available
	 *             and ready or an error occurred during deleting files of
	 *             CSARs.
	 * 
	 */
	public void deleteCSARs() throws SystemException;
	
}
