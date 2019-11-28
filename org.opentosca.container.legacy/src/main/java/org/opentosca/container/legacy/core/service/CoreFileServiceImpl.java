package org.opentosca.container.legacy.core.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.opentosca.container.core.common.EntityExistsException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.impl.service.ZipManager;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryDeleteVisitor;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.legacy.core.service.csar.CSARUnpacker;
import org.opentosca.container.legacy.core.service.csar.CSARValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Provides a store and management functionality for CSAR files.
 * <p>
 * Meta data (file and directory paths, TOSCA meta file content and CSAR ID) of a CSAR will be
 * stored locally in the database. This makes it possible to browse in a CSAR and get the TOSCA meta
 * file data without network access.
 *
 * @see CSARContent
 */
@Deprecated
@Service
public class CoreFileServiceImpl implements ICoreFileService {

  private static final Logger LOG = LoggerFactory.getLogger(CoreFileServiceImpl.class);

  private static final CSARMetaDataJPAStore JPA_STORE = new CSARMetaDataJPAStore();
  private final Path baseDirectory;

  @Deprecated
  public CoreFileServiceImpl() {
    this(Settings.CONTAINER_STORAGE_BASEPATH);
  }

  public CoreFileServiceImpl(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  @Override
  public CSARContent getCSAR(final CSARID csarID) throws UserException {
    return JPA_STORE.getCSARMetaData(csarID);
  }

  @Override
  public Set<CSARID> getCSARIDs() {
    return JPA_STORE.getCSARIDsMetaData();
  }

  @Override
  public CSARID storeCSAR(final Path csarFile) throws UserException, SystemException {
    LOG.debug("Given file to store: {}", csarFile);

    CSARUnpacker csarUnpacker = null;
    try {
      if (!Files.isRegularFile(csarFile)) {
        throw new UserException(
          "\"" + csarFile.toString() + "\" to store is not an absolute path to an existent file.");
      }

      final CSARID csarID = new CSARID(csarFile.getFileName().toString());
      if (JPA_STORE.isCSARMetaDataStored(csarID)) {
        throw new EntityExistsException("CSAR \"" + csarID.toString() + "\" is already stored. Overwriting a CSAR is not allowed.");
      }

      csarUnpacker = new CSARUnpacker(csarFile);
      csarUnpacker.unpackAndVisitUnpackDir();

      final Path csarUnpackDir = csarUnpacker.getUnpackDirectory();
      final DirectoryVisitor csarVisitor = csarUnpacker.getFilesAndDirectories();

      final CSARValidator csarValidator = new CSARValidator(csarID, csarUnpackDir, csarVisitor);
      if (!csarValidator.isValid()) {
        throw new UserException(csarValidator.getErrorMessage());
      }

      final Path toscaMetaFileAbsPath = csarUnpackDir.resolve(Settings.TOSCA_META_FILE_REL_PATH);
      final TOSCAMetaFile toscaMetaFile = new TOSCAMetaFileParser().parse(toscaMetaFileAbsPath);
      if (toscaMetaFile == null) {
        throw new UserException("TOSCA meta file is invalid.");
      }

      Path persistentStorageLocation = baseDirectory.resolve(csarID.getFileName());
      try {
        FileSystem.copyDirectory(csarUnpackDir, persistentStorageLocation);
      } catch (IOException e) {
        throw new SystemException("Creating the permanent storage for the CSAR failed", e);
      }

      JPA_STORE.storeCSARMetaData(csarID, persistentStorageLocation, toscaMetaFile);
      LOG.debug("Storing CSAR \"{}\" located at \"{}\" successfully completed.", csarID, csarFile);
      return csarID;
    } finally {
      // At the end or if an exception occurred we should delete the
      // unpack directory, if necessary.
      if (csarUnpacker != null) {
        csarUnpacker.deleteUnpackDir();
      }
    }
  }

  @Override
  public Path exportCSAR(final CSARID csarID) throws UserException, SystemException {
    LOG.debug("Exporting CSAR \"{}\"...", csarID);

    Path csarDownloadDirectory = null;
    try {
      final Path tempDirectory = FileSystem.getTemporaryFolder();
      csarDownloadDirectory = tempDirectory.resolve("content");
      Files.createDirectory(csarDownloadDirectory);

      final Path csarFile = tempDirectory.resolve(csarID.getFileName());
      ZipManager.getInstance().zip(csarDownloadDirectory.toFile(), csarFile.toFile());
      LOG.debug("CSAR \"{}\" was successfully exported to \"{}\".", csarID, csarFile);

      return csarFile;
    } catch (final IOException exc) {
      throw new SystemException("An IO Exception occured.", exc);
    } finally {
      if (csarDownloadDirectory == null) {
        throw new SystemException("Export failed");
      }
      final DirectoryDeleteVisitor csarDeleteVisitor = new DirectoryDeleteVisitor();
      try {
        LOG.debug("Deleting CSAR download directory \"{}\"...", csarDownloadDirectory);
        Files.walkFileTree(csarDownloadDirectory, csarDeleteVisitor);
        LOG.debug("Deleting CSAR download directory \"{}\" completed.", csarDownloadDirectory);
      } catch (final IOException exc) {
        throw new SystemException("An IO Exception occured. Deleting CSAR download directory \"" + csarDownloadDirectory + "\" failed.", exc);
      }
    }
  }

  @Override
  public void deleteCSAR(final CSARID csarID) throws SystemException, UserException {
    LOG.info("Deleting CSAR \"{}\" in planbuilder", csarID);
    if (!JPA_STORE.isCSARMetaDataStored(csarID)) {
      LOG.info("Nothing to delete");
      return;
    }
    // Delete CSAR from disk
    FileUtils.forceDelete(baseDirectory.resolve(csarID.getFileName()));
    JPA_STORE.deleteCSARMetaData(csarID);
    LOG.info("Deleting CSAR \"{}\" completed.", csarID);
  }

  @Override
  public void deleteCSARs() throws SystemException {
    LOG.debug("Deleting all CSARs...");
    final Set<CSARID> csarIDs = JPA_STORE.getCSARIDsMetaData();
    if (csarIDs.isEmpty()) {
      LOG.debug("No CSARs are currently stored.");
      return;
    }

    LOG.debug("{} CSAR(s) is / are currently stored and will be deleted now.", csarIDs.size());
    for (final CSARID csarID : csarIDs) {
      try {
        deleteCSAR(csarID);
      } catch (final UserException exc) {
        throw new SystemException("An System Exception occured.", exc);
      }
    }
    LOG.debug("Deleting all CSARs completed.");
  }
}
