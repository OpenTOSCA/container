package org.opentosca.container.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.provenance.exceptions.ProvenanceException;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.importing.CsarImportOptions;
import org.eclipse.winery.repository.importing.CsarImporter;
import org.eclipse.winery.repository.importing.ImportMetaInformation;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.CsarImpl;
import org.opentosca.container.core.next.utils.Consts;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarStorageServiceImpl implements CsarStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarStorageServiceImpl.class);

    // FIXME obtain from settings or otherwise
    private static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting(Settings.CONTAINER_STORAGE_BASEPATH));

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
        throw new NoSuchElementException();
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
            final CsarImportOptions importOptions = new CsarImportOptions();
            importOptions.setValidate(false); // avoid triggering Provenance meddling with this
            importOptions.setAsyncWPDParsing(true);
            importOptions.setOverwrite(false);
            importInfo = importer.readCSAR(Files.newInputStream(csarLocation), importOptions);
            if (!importInfo.errors.isEmpty()) {
                FileUtils.forceDelete(permanentLocation);
            }
        }
        catch (IOException e) {
            // roll back the import
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        }
        catch (ProvenanceException e) {
            LOGGER.debug("Provenance for imported CSAR could not be checked", e);
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        }
        catch (ExecutionException | InterruptedException e) {
            LOGGER.warn("CSAR Import was interrupted or terminated with an exception", e);
            FileUtils.forceDelete(permanentLocation);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        }
        catch (Exception e) {
            LOGGER.warn("CSAR Import failed with an unspecified exception", e);
            FileUtils.forceDelete(permanentLocation);
            if (e instanceof RuntimeException) { throw e; }
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.", e);
        }
        
        if (importInfo == null || !importInfo.errors.isEmpty()) {
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        // apparently there will always be an EntryServiceTemplate??
        ServiceTemplateId entryServiceTemplate = importInfo.entryServiceTemplate;
        // we may be able to "guarantee" it's not null, since we validate CSARs on import
        if (entryServiceTemplate == null) {
            return candidateId;
        }
        // FIXME don't store this in the winery repo location. Use some database for this!
        try (OutputStream os = Files.newOutputStream(permanentLocation.resolve("EntryServiceTemplate"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            os.write(entryServiceTemplate.getQName().toString().getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            // well... we failed to keep track of the entryServiceTemplate
            LOGGER.warn("Could not save EntryServiceTemplate for Csar [{}] due to {}", candidateId.csarName(), e);
            throw new UserException("CSAR \"" + candidateId.csarName() + "\" could not be imported.");
        }
        return candidateId;
    }

    @Override
    public void deleteCSAR(CsarId csarId) throws SystemException, UserException {
        LOGGER.debug("Deleting CSAR \"{}\"...", csarId.csarName());
        FileUtils.forceDelete(csarId.getSaveLocation());
    }

    @Override
    public void purgeCsars() throws SystemException {
        LOGGER.debug("Deleting all CSARs...");
        
        try {
            for (Path csarRepoContent : Files.newDirectoryStream(CSAR_BASE_PATH)) {
                LOGGER.debug("Deleting CSAR at [{}]", csarRepoContent);
                if (Files.isDirectory(csarRepoContent)) {
                    // delete csar here
                    FileUtils.forceDelete(csarRepoContent);
                }
            }
            LOGGER.debug("Deleting all CSARs completed");
        }
        catch (IOException e) {
            throw new SystemException("Could not delete all CSARs.", e);
        }
    }


    @Override
    public Path exportCSAR(final CsarId csarId) throws UserException, SystemException {
        LOGGER.debug("Exporting CSAR \"{}\"...", csarId);
        Csar csar = findById(csarId);
        
        final Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));
        final Path csarDownloadDirectory = tempDirectory.resolve("content");
        try {
            Files.createDirectory(csarDownloadDirectory);
            final Path csarTarget = csarDownloadDirectory.resolve(csarId.csarName());
            if (Files.exists(csarTarget)) {
                // remove previous export result
                FileUtils.forceDelete(csarTarget);
            }
            csar.exportTo(csarTarget);
            return csarTarget;
        }
        catch (final IOException exc) {
            throw new SystemException("An IO Exception occured.", exc);
        }
    }
}
