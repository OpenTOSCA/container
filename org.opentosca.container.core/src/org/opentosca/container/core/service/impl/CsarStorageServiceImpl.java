package org.opentosca.container.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.importing.CsarImporter;
import org.eclipse.winery.repository.importing.ImportMetaInformation;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.ZipManager;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARMetaDataJPAStore;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARUnpacker;
import org.opentosca.container.core.impl.service.internal.file.csar.CSARValidator;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryDeleteVisitor;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.model.csar.id.CSARID;
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
    private static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting(Settings.CONTAINER_STORAGE_BASEPATH));
    private static final CSARMetaDataJPAStore JPA_STORE = new CSARMetaDataJPAStore();

    @Override
    public Set<Csar> findAll() {
        LOGGER.debug("Requesting all CSARs");
        final Set<Csar> csars = new HashSet<>();
        try {
            for (Path csarId : Files.newDirectoryStream(CSAR_BASE_PATH, Files::isDirectory)) {
                // FIXME make CsarId a name and put the path somewhere else
                csars.add(new CsarImpl(new CsarId(csarId)));
            }
        }
        catch (IOException e) {
            LOGGER.error("Error when traversing '{}' for CSARs", CSAR_BASE_PATH);
            throw new UncheckedIOException(e);
        }
        return csars;
    }

    @Override
    public Csar findById(CsarId id) throws NoSuchElementException {
        if (Files.exists(id.getSaveLocation())) {
            return new CsarImpl(id);
        }
        LOGGER.info("CSAR '{}' could not be found", id.toString());
        throw new NoSuchElementException(String.format("CSAR '%s' could not be found", id.toString()));
    }

    @Override
    public Path storeCSARTemporarily(String filename, InputStream is) {
        try {
            Path tempLocation = Paths.get(Consts.TMPDIR, filename);
            if (Files.exists(tempLocation)) {
                // well ... umm ... let's just delete it, I guess?
                Files.delete(tempLocation);
            }
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

        CsarId candidateId = new CsarId(csarLocation.getFileName().toString());
        Path permanentLocation = CSAR_BASE_PATH.resolve(csarLocation.getFileName());
        if (Files.exists(permanentLocation)) {
            throw new UserException(
                "CSAR \"" + candidateId.csarName() + "\" is already stored. Overwriting a CSAR is not allowed.");
        }
        ImportMetaInformation importInfo = null;
        try {
            Files.createDirectory(permanentLocation);
            // CsarImporter doesn't allow overriding the repository it imports to
            RepositoryFactory.reconfigure(new FileBasedRepositoryConfiguration(permanentLocation));

            CsarImporter importer = new CsarImporter();
            // do not overwrite existing definitions, use async parsing for speed
            importInfo = importer.readCSAR(Files.newInputStream(csarLocation), false, true);
            if (!importInfo.errors.isEmpty()) {
                FileUtils.forceDelete(permanentLocation);
            }
        }
        catch (IOException e) {
            // roll back the import
            FileUtils.forceDelete(permanentLocation);
        }
        if (importInfo == null || !importInfo.errors.isEmpty()) {
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        return candidateId;
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

        final Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));
        final Path csarDownloadDirectory = tempDirectory.resolve("content");

        try {
            Files.createDirectory(csarDownloadDirectory);
            for (final Path directoryOfCSAR : directoriesOfCSAR) {
                final Path directoryOfCSARAbsPath = csarDownloadDirectory.resolve(directoryOfCSAR);
                Files.createDirectories(directoryOfCSARAbsPath);
            }

            final Path csarFile = tempDirectory.resolve(csarId.getSaveLocation().getFileName());
            // FIXME was encapsulated in IFileAccessService. Don't make this a hard dep
            ZipManager.getInstance().zip(csarDownloadDirectory.toFile(), csarFile.toFile());
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
            }
            catch (final IOException exc) {
                throw new SystemException("An IO Exception occured. Deleting CSAR download directory \""
                    + csarDownloadDirectory + "\" failed.", exc);
            }
        }
    }
}
