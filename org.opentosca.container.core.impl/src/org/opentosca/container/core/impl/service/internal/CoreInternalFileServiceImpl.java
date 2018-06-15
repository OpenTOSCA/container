package org.opentosca.container.core.impl.service.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARMetaDataJPAStore;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARUnpacker;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARValidator;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryDeleteVisitor;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.container.core.model.csar.toscametafile.TOSCAMetaFileParser;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.core.service.internal.ICoreInternalFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a store and management functionalities for CSAR files.
 *
 * Files and directories in a CSAR can be stored with any of the available storage providers
 * (plug-ins). For using a certain storage provider it must be defined as the active storage
 * provider with {@link #setActiveStorageProvider(String)}. Only available storage providers can be
 * set as active. By default (e.g. after the start of the container), no active storage provider is
 * set. In this case or if the active storage provider is not ready (e.g. has no credentials) the
 * default storage provider will be used instead, if it's available and ready. Otherwise the
 * operation fails. The default one is hard-coded and can be get with
 * {@link #getDefaultStorageProvider()}.
 *
 * A stored CSAR file or only a file / directory of it can be moved to another storage provider. The
 * target storage provider for the move is analogous the active storage provider respectively the
 * default one. If an active storage provider goes unavailable no active storage provider is set
 * (setting will be cleared).
 *
 * Meta data (file and directory paths, TOSCA meta file content and CSAR ID) of a CSAR will be
 * stored locally in the database. This makes it possible to browse in a CSAR and get the TOSCA meta
 * file data without network access.
 *
 * @see ICoreInternalFileStorageProviderService
 * @see CSARContent
 */
public class CoreInternalFileServiceImpl implements ICoreInternalFileService {

    private final static Logger LOG = LoggerFactory.getLogger(CoreInternalFileServiceImpl.class);

//    private final StorageProviderManager STORAGE_PROVIDER_MANAGER = new StorageProviderManager();
    private final CSARMetaDataJPAStore JPA_STORE = new CSARMetaDataJPAStore();

    private static IFileAccessService fileAccessService = null;

    /**
     * Relative path to CSAR root of the TOSCA meta file.
     *
     * @see org.opentosca.settings.Settings
     */
    private final String TOSCA_META_FILE_REL_PATH = Settings.getSetting("toscaMetaFileRelPath");

    @Override
    public CSARID storeCSAR(final Path csarFile) throws UserException, SystemException {
        LOG.debug("Given file to store: {}", csarFile);
        if (!Files.isRegularFile(csarFile)) {
            throw new UserException(
                "\"" + csarFile.toString() + "\" to store is not an absolute path to an existing file.");
        }

        final CSARID csarID = new CSARID(csarFile.getFileName().toString());

        if (this.JPA_STORE.isCSARMetaDataStored(csarID)) {
            throw new UserException(
                "CSAR \"" + csarID.toString() + "\" is already stored. Overwriting a CSAR is not allowed.");
        }


        CSARUnpacker csarUnpacker = new CSARUnpacker(csarFile);
        csarUnpacker.unpackAndVisitUnpackDir();

        Path csarUnpackDir = csarUnpacker.getUnpackDirectory();
        try {
            
            DirectoryVisitor csarVisitor = csarUnpacker.getFilesAndDirectories();
            final CSARValidator csarValidator = new CSARValidator(csarID, csarUnpackDir, csarVisitor);

            if (!csarValidator.isValid()) {
                throw new UserException(csarValidator.getErrorMessage());
            }

            final Path toscaMetaFileAbsPath = csarUnpackDir.resolve(this.TOSCA_META_FILE_REL_PATH);

            final TOSCAMetaFile toscaMetaFile = new TOSCAMetaFileParser().parse(toscaMetaFileAbsPath);

            if (toscaMetaFile == null) {
                throw new UserException("TOSCA meta file is invalid.");
            }

            final Set<Path> directoriesInCSARUnpackDir = csarVisitor.getVisitedDirectories();

            final Map<Path, String> fileToStorageProviderIDMap = new HashMap<>();
            final Set<Path> directories = new HashSet<>();

            for (final Path directoryInCSARUnpackDir : directoriesInCSARUnpackDir) {
                final Path directoryRelToCSARRoot = csarUnpackDir.relativize(directoryInCSARUnpackDir);
                directories.add(directoryRelToCSARRoot);
            }

            this.JPA_STORE.storeCSARMetaData(csarID, directories, fileToStorageProviderIDMap, toscaMetaFile);

            LOG.debug("Storing CSAR \"{}\" located at \"{}\" successfully completed.", csarID, csarFile);
            return csarID;
        }
        finally {
            // clean up the unpack dir
            csarUnpacker.deleteUnpackDir();
        }
    }

    @Override
    public CSARContent getCSAR(final CSARID csarID) throws UserException {

        final CSARContent csarContent = this.JPA_STORE.getCSARMetaData(csarID);
        return csarContent;

    }

    @Override
    public Set<CSARID> getCSARIDs() {

        final Set<CSARID> csarIDs = this.JPA_STORE.getCSARIDsMetaData();
        return csarIDs;

    }

    @Override
    public Path exportCSAR(final CSARID csarID) throws UserException, SystemException {

        CoreInternalFileServiceImpl.LOG.debug("Exporting CSAR \"{}\"...", csarID);

        final Set<Path> directoriesOfCSAR = this.JPA_STORE.getDirectories(csarID);

        final Path tempDirectory = CoreInternalFileServiceImpl.fileAccessService.getTemp().toPath();
        final Path csarDownloadDirectory = tempDirectory.resolve("content");

        try {

            Files.createDirectory(csarDownloadDirectory);

            for (final Path directoryOfCSAR : directoriesOfCSAR) {
                final Path directoryOfCSARAbsPath = csarDownloadDirectory.resolve(directoryOfCSAR);
                Files.createDirectories(directoryOfCSARAbsPath);
            }

            final Path csarFile = tempDirectory.resolve(csarID.getFileName());

            fileAccessService.zip(csarDownloadDirectory.toFile(), csarFile.toFile());

            LOG.debug("CSAR \"{}\" was successfully exported to \"{}\".", csarID, csarFile);

            return csarFile;

        }
        catch (final IOException exc) {
            throw new SystemException("An IO Exception occured.", exc);
        }
        finally {
            final DirectoryDeleteVisitor csarDeleteVisitor = new DirectoryDeleteVisitor();
            try {
                LOG.debug("Deleting CSAR download directory \"{}\"...", csarDownloadDirectory);
                Files.walkFileTree(csarDownloadDirectory, csarDeleteVisitor);
                LOG.debug("Deleting CSAR download directory \"{}\" completed.", csarDownloadDirectory);
            }
            catch (final IOException exc) {
                throw new SystemException("An IO Exception occured. Deleting CSAR download directory \""
                    + csarDownloadDirectory + "\" failed.", exc);
            }
        }
    }

    @Override
    public void deleteCSAR(final CSARID csarID) throws SystemException, UserException {
        LOG.debug("Deleting CSAR \"{}\"...", csarID);
        this.JPA_STORE.deleteCSARMetaData(csarID);
    }

    @Override
    public void deleteCSARs() throws SystemException {

        LOG.debug("Deleting all CSARs...");

        final Set<CSARID> csarIDs = this.JPA_STORE.getCSARIDsMetaData();

        if (!csarIDs.isEmpty()) {

            LOG.debug("{} CSAR(s) is / are currently stored and will be deleted now.", csarIDs.size());

            for (final CSARID csarID : csarIDs) {
                try {
                    this.deleteCSAR(csarID);
                }
                catch (final UserException exc) {
                    throw new SystemException("An System Exception occured.", exc);
                }
            }
            LOG.debug("Deleting all CSARs completed.");
        } else {
            LOG.debug("No CSARs are currently stored.");
        }
    }

    /**
     * Binds the File Access Service.
     *
     * @param fileAccessService to bind
     */
    protected void bindFileAccessService(final IFileAccessService fileAccessService) {
        if (fileAccessService == null) {
            CoreInternalFileServiceImpl.LOG.warn("Can't bind File Access Service.");
        } else {
            CoreInternalFileServiceImpl.fileAccessService = fileAccessService;
            CoreInternalFileServiceImpl.LOG.debug("File Access Service bound.");
        }
    }

    /**
     * Unbinds the File Access Service.
     *
     * @param fileAccessService to unbind
     */
    protected void unbindFileAccessService(final IFileAccessService fileAccessService) {
        CoreInternalFileServiceImpl.fileAccessService = null;
        CoreInternalFileServiceImpl.LOG.debug("File Access Service unbound.");
    }

}
