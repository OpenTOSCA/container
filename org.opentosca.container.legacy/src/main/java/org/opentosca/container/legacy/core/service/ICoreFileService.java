package org.opentosca.container.legacy.core.service;

import java.nio.file.Path;
import java.util.Set;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * This interface of the Core File Service defines methods for<br />
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
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * @see CSARContent
 */
@Deprecated
public interface ICoreFileService {

  /**
   * Stores the CSAR file {@code csarFile}.<br />
   * First, it will be checked if the given file has correct extension and is not already stored. Then
   * the CSAR will be unpacked (path is specified in Settings bundle), its content validated and the
   * TOSCA meta file parsed. Finally all files of the CSAR will be stored on the active storage
   * provider. After storing on storage provider the meta data of the CSAR (file and directory paths,
   * TOSCA meta file content and CSAR ID) will be stored in the database and the unpack directory
   * deleted.<br />
   * <br />
   * Note 1: If no active storage provider is set (e.g. directly after the start of the container) or
   * it's not ready, the default storage provider will be used instead, if it's available and
   * ready.<br />
   * Note 2: Only files of a CSAR will be stored on the storage provider. Directories will be stored
   * as meta data only.<br />
   * Note 3: If an error occurred during storing the unpack directory will be also deleted (if
   * necessary).
   *
   * @param csarFile to store.
   * @return CSAR ID thats uniquely identifies the CSAR file.
   * @throws SystemException if active storage provider is not set / can't be used and default storage
   *                         provider also can't be used, unpacking CSAR failed, access to an directory denied while
   *                         getting files and directories in unpack directory or if an error occurred during storing
   *                         on the storage provider.
   * @throws UserException   if {@code csarFile} is not a existent file, has wrong file extension, is
   *                         already stored or it's content is invalid, e.g. contains no TOSCA file or has an invalid
   *                         TOSCA meta file.
   * @see org.opentosca.settings.Settings
   */
  public CSARID storeCSAR(Path csarFile) throws UserException, SystemException;

  /**
   * Retrieves meta data of CSAR {@code csarID}.
   *
   * @param csarID of CSAR
   * @return {@link CSARContent} that gives access to all files and directories and the TOSCA meta
   * file of the CSAR.
   * @throws UserException if CSAR {@code csarID} was not found.
   */
  public CSARContent getCSAR(CSARID csarID) throws UserException;

  /**
   * Exports a stored CSAR {@code csarID}.<br />
   * First, it retrieves the meta data of the CSAR and checks if the required storage provider(s) are
   * available and ready to minimize the risk of a cancel during retrieving files. Then it creates the
   * directory structure and retrieves all files of the CSAR from the appropriate storage provider(s)
   * to a sub directory of a Temp directory. Finally the content of this directory will be compressed
   * as a CSAR file. The created CSAR file is located directly in the Temp directory.<br />
   * <br/>
   * <p>
   * Note: At the end or if an error occurred the directory that contains the content of the CSAR file
   * will be deleted (if necessary).
   *
   * @param csarID of CSAR
   * @return CSAR {@code csarID} as {@link Path} object.
   * @throws UserException   if CSAR {@code csarID} was not found.
   * @throws SystemException if a required storage provider is not available and ready or an error
   *                         occurred during retrieving files of CSAR.
   */
  public Path exportCSAR(CSARID csarID) throws UserException, SystemException;

  /**
   * @return CSAR IDs of all stored CSAR files.
   */
  public Set<CSARID> getCSARIDs();

  /**
   * Deletes the CSAR {@code csarID}.<br />
   * <br />
   * First, the files meta data of CSAR {@code csarID} will be retrieved to check if the CSAR exists
   * and to get the storage provider(s) of the files. Then it will be checked if each storage provider
   * is available and ready to minimize the risk for a cancel of the deletion process. Finally all
   * files of the CSAR will be deleted on the storage provider(s). After deletion the meta data of the
   * CSAR will be deleted, too.<br />
   * If an error occurred while deleting files, the complete process will be canceled respectively
   * further files will not be deleted anymore. The meta data of the CSAR is not deleted, so deletion
   * can be executed again to delete the remaining files.
   *
   * @param csarID of CSAR
   * @throws UserException   if CSAR {@code csarID} was not found.
   * @throws SystemException if a required storage provider is not available and ready or an error
   *                         occurred during deleting files.
   */
  public void deleteCSAR(CSARID csarID) throws SystemException, UserException;

  /**
   * Deletes all CSAR files.<br />
   * <br />
   * The CSAR IDs of all stored CSAR will be retrieved and then each CSAR deleted by using {@link #deleteCSAR(CSARID)}.<br />
   * If an error occurred while deleting a CSAR, the complete process will be canceled respectively
   * further CSARs will not be deleted anymore.<br />
   *
   * @throws SystemException if a required storage provider is not available and ready or an error
   *                         occurred during deleting files of CSARs.
   */
  public void deleteCSARs() throws SystemException;

}
