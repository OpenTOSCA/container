package org.opentosca.container.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARMetaDataJPAStore;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARUnpacker;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARValidator;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryDeleteVisitor;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.csar.toscametafile.TOSCAMetaFile;
import org.opentosca.container.core.model.csar.toscametafile.TOSCAMetaFileParser;
import org.opentosca.container.core.next.utils.Consts;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IFileAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarStorageServiceImpl implements CsarStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarStorageServiceImpl.class);
    
    /**
     * Relative path to CSAR root of the TOSCA meta file.
     *
     * @see org.opentosca.settings.Settings
     */
    private final String TOSCA_META_FILE_REL_PATH = Settings.getSetting("toscaMetaFileRelPath");
    // FIXME obtain from settings or otherwise
    private static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting("org.opentosca.csar.basepath"));
    private static final CSARMetaDataJPAStore JPA_STORE = new CSARMetaDataJPAStore();
    private static IFileAccessService fileAccessService = null;
    
    @Override
    public Set<Csar> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<Csar> csars = new HashSet<>();
        try {
            for (Path csarId : Files.newDirectoryStream(CSAR_BASE_PATH, Files::isDirectory)) {
                csars.add(new CsarImpl(new CsarId(csarId)));
            }
        }
        catch (IOException e) {
            LOGGER.error("Error when traversing '{}' for CSARs", CSAR_BASE_PATH);
            throw new ServerErrorException(Response.serverError().build());
        }
        return csars;
    }

    @Override
    public Csar findById(CsarId id) throws NoSuchElementException {
        if (Files.exists(id.getSaveLocation())) {
            return new CsarImpl(id); // FIXME pass path here
        }
        LOGGER.info("CSAR '{}' could not be found", id.toString());
        throw new NoSuchElementException(String.format("CSAR '%s' could not be found", id.toString()));
    }

    @Override
    public Path storeCSARTemporarily(String filename, InputStream is) {
        try {
            Path tempLocation = Paths.get(Consts.TMPDIR, filename);
            Files.copy(is, tempLocation);
            return tempLocation;
        }
        catch (IOException e) {
            LOGGER.error("Exception occured when writing temporary CSAR file: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public CsarId storeCSAR(Path csarLocation) throws UserException, SystemException {
        LOGGER.debug("Given file to store: {}", csarLocation);
        if (!Files.isRegularFile(csarLocation)) {
            throw new UserException(
                "\"" + csarLocation.toString() + "\" to store is not an absolute path to an existing file.");
        }

        final CsarId csarID = new CsarId(csarLocation);

        if (JPA_STORE.isCSARMetaDataStored(csarID.toOldCsarId())) {
            throw new UserException(
                "CSAR \"" + csarID.toString() + "\" is already stored. Overwriting a CSAR is not allowed.");
        }


        CSARUnpacker csarUnpacker = new CSARUnpacker(csarLocation);
        csarUnpacker.unpackAndVisitUnpackDir();

        Path csarUnpackDir = csarUnpacker.getUnpackDirectory();
        try {
            DirectoryVisitor csarVisitor = csarUnpacker.getFilesAndDirectories();
            final CSARValidator csarValidator = new CSARValidator(csarID.toOldCsarId(), csarUnpackDir, csarVisitor);

            if (!csarValidator.isValid()) {
                throw new UserException(csarValidator.getErrorMessage());
            }

            final Path toscaMetaFileAbsPath = csarUnpackDir.resolve(this.TOSCA_META_FILE_REL_PATH);
            final TOSCAMetaFile toscaMetaFile = new TOSCAMetaFileParser().parse(toscaMetaFileAbsPath);

            if (toscaMetaFile == null) {
                throw new UserException("TOSCA meta file is invalid.");
            }

            final Set<Path> directoriesInCSARUnpackDir = csarVisitor.getVisitedDirectories();
            final Set<Path> directories = new HashSet<>();

            for (final Path directoryInCSARUnpackDir : directoriesInCSARUnpackDir) {
                final Path directoryRelToCSARRoot = csarUnpackDir.relativize(directoryInCSARUnpackDir);
                directories.add(directoryRelToCSARRoot);
            }

            JPA_STORE.storeCSARMetaData(csarID.toOldCsarId(), directories, new HashMap<>(), toscaMetaFile);

            LOGGER.debug("Storing CSAR \"{}\" located at \"{}\" successfully completed.", csarID, csarLocation);
            return csarID;
        }
        finally {
            // clean up the unpack dir
            csarUnpacker.deleteUnpackDir();
        }
    }

    @Override
    public void deleteCSAR(CsarId csarId) throws SystemException, UserException {
        LOGGER.debug("Deleting CSAR \"{}\"...", csarId);
        JPA_STORE.deleteCSARMetaData(csarId.toOldCsarId());
    }
    
    @Override
    public void purgeCsars() throws SystemException {

        LOGGER.debug("Deleting all CSARs...");

        final Set<CSARID> csarIDs = this.JPA_STORE.getCSARIDsMetaData();

        if (!csarIDs.isEmpty()) {

            LOGGER.debug("{} CSAR(s) is / are currently stored and will be deleted now.", csarIDs.size());

            for (final CSARID csarID : csarIDs) {
                try {
                    this.deleteCSAR(new CsarId(csarID));
                }
                catch (final UserException exc) {
                    throw new SystemException("An System Exception occured.", exc);
                }
            }
            LOGGER.debug("Deleting all CSARs completed.");
        } else {
            LOGGER.debug("No CSARs are currently stored.");
        }
    }
    

    @Override
    public Path exportCSAR(final CsarId csarId) throws UserException, SystemException {
        LOGGER.debug("Exporting CSAR \"{}\"...", csarId);
        final Set<Path> directoriesOfCSAR = JPA_STORE.getDirectories(csarId.toOldCsarId());

        final Path tempDirectory = fileAccessService.getTemp().toPath();
        final Path csarDownloadDirectory = tempDirectory.resolve("content");

        try {
            Files.createDirectory(csarDownloadDirectory);
            for (final Path directoryOfCSAR : directoriesOfCSAR) {
                final Path directoryOfCSARAbsPath = csarDownloadDirectory.resolve(directoryOfCSAR);
                Files.createDirectories(directoryOfCSARAbsPath);
            }

            final Path csarFile = tempDirectory.resolve(csarId.getSaveLocation().getFileName());
            fileAccessService.zip(csarDownloadDirectory.toFile(), csarFile.toFile());
            LOGGER.debug("CSAR \"{}\" was successfully exported to \"{}\".", csarId, csarFile);
            return csarFile;
        }
        catch (final IOException exc) {
            throw new SystemException("An IO Exception occured.", exc);
        }
        finally {
            final DirectoryDeleteVisitor csarDeleteVisitor = new DirectoryDeleteVisitor();
            try {
                LOGGER.debug("Deleting CSAR download directory \"{}\"...", csarDownloadDirectory);
                Files.walkFileTree(csarDownloadDirectory, csarDeleteVisitor);
                LOGGER.debug("Deleting CSAR download directory \"{}\" completed.", csarDownloadDirectory);
            } catch (final IOException exc) {
                throw new SystemException("An IO Exception occured. Deleting CSAR download directory \""
                    + csarDownloadDirectory + "\" failed.", exc);
            }
        }
    }
    
    /**
     * Binds the File Access Service.
     *
     * @param fileAccessService to bind
     */
    protected void bindFileAccessService(final IFileAccessService injectedService) {
        if (injectedService == null) {
            LOGGER.warn("Can't bind File Access Service.");
        } else {
            fileAccessService = injectedService;
            LOGGER.debug("File Access Service bound.");
        }
    }

    /**
     * Unbinds the File Access Service.
     *
     * @param fileAccessService to unbind
     */
    protected void unbindFileAccessService(final IFileAccessService removedService) {
        fileAccessService = null;
        LOGGER.debug("File Access Service unbound.");
    }

}
